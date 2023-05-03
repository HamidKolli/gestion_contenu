package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.plugins.FacadeContentManagementPlugin;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD Classe concrete qui gere les facades
 *
 */
@OfferedInterfaces(offered = { NodeManagementCI.class })
@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class })
public class FacadeComponent extends AbstractComponent {

	public static final String DIR_LOGGER_NAME = "loggers/facades/";
	public static final String FILE_LOGGER_NAME = "facade";

	private final int numberRootNode;

	private final int nombreThreadNodeManagement;
	private final int nombreThreadContentManagement;

	private FacadeContentManagementPlugin plugin;

	private ApplicationNodeAddressI uriFacadeSuivante;

	private static final int HOPS_PROBE = 15;
	private static final int NB_VOISIN = 3;
	private static final int NB_THREAD_RELEASE_CLIENT = 5;

	protected List<PeerNodeAddressI> connectToNode;
	protected ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private FacadePortNodeManagement facadePortNodeManagement;
	private NodePortNodeManagement outFacadeNodeManagement;
	private ApplicationNodeAddressI facadeAdresses;
	private OutPortNode outPortNode;
	private ConcurrentMap<String, Semaphore> resultProbeSem;
	private ConcurrentMap<String, Set<PeerNodeAddressI>> resultProbe;

	/**
	 * 
	 * Constructeur FacadeComponent
	 * 
	 * @param applicationNodeAddress : les addresses des ports entrants de la facade
	 * @param nbrRacine              : le nombre de noeuds racines de la facade
	 * @throws Exception
	 */
	protected FacadeComponent(String clockURI, ApplicationNodeAddressI applicationNodeAddress, int nbrRacine,
			int nombreThreadNodeManagement, int nombreThreadContentManagement,
			ApplicationNodeAddressI uriFacadeSuivante) throws Exception {
		super(1, 0);
		this.nombreThreadNodeManagement = nombreThreadNodeManagement;
		this.nombreThreadContentManagement = nombreThreadContentManagement;
		this.getTracer().setTitle("Facade " + applicationNodeAddress.getNodeIdentifier());
		this.uriFacadeSuivante = uriFacadeSuivante;

		this.facadeAdresses = applicationNodeAddress;
		connectToNode = new ArrayList<>();
		connectNodeRoot = new ConcurrentHashMap<>();
		resultProbeSem = new ConcurrentHashMap<>();
		resultProbe = new ConcurrentHashMap<>();
		this.numberRootNode = nbrRacine;

	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 *
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {

			String uriContentManagement = AbstractPort.generatePortURI();
			String uriNodeManagement = AbstractPort.generatePortURI();
			String uriExecutorReleaseClient = AbstractPort.generatePortURI();
			createNewExecutorService(uriExecutorReleaseClient, NB_THREAD_RELEASE_CLIENT, false);
			createNewExecutorService(uriContentManagement, nombreThreadContentManagement, false);
			createNewExecutorService(uriNodeManagement, nombreThreadNodeManagement, false);
			
			
			
			plugin = new FacadeContentManagementPlugin(facadeAdresses, numberRootNode, uriContentManagement,
					uriExecutorReleaseClient,uriFacadeSuivante, connectNodeRoot);
			plugin.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(plugin);

			facadePortNodeManagement = new FacadePortNodeManagement(facadeAdresses.getNodeManagementURI(), this,
					uriNodeManagement);
			outFacadeNodeManagement = new NodePortNodeManagement(this);
			outPortNode = new OutPortNode(this);
			outPortNode.publishPort();
			outFacadeNodeManagement.publishPort();
			facadePortNodeManagement.publishPort();

			logMessage(facadePortNodeManagement.getPortURI());

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	public void join(PeerNodeAddressI a) throws Exception {

		this.traceMessage(a.getNodeIdentifier() + " join network ");
		this.logMessage(
				"join |" + a.getNodeIdentifier() + " root? " + (connectNodeRoot.size() < numberRootNode) + "\n");
		OutPortNode portNode = new OutPortNode(this);
		portNode.publishPort();
		this.doPortConnection(portNode.getPortURI(), a.getNodeURI(), ConnectorNode.class.getCanonicalName());

		Set<PeerNodeAddressI> result;

		if (connectNodeRoot.size() < numberRootNode) {
			OutPortContentManagement port = new OutPortContentManagement(this);
			port.publishPort();
			this.doPortConnection(port.getPortURI(), ((ContentNodeAddressI) a).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			connectNodeRoot.put(a, port);
		}

		if (connectToNode.size() < NB_VOISIN) {
			result = new HashSet<>(connectToNode);
			this.logMessage("join | fin " + a.getNodeIdentifier() + "\n");
			portNode.acceptNeighbours(result);
			portNode.doDisconnection();
			portNode.unpublishPort();
			portNode.destroyPort();
			connectToNode.add(a);
			return;
		}

//		int index = Math.abs((new Random()).nextInt() % connectToNode.size());
//		List<PeerNodeAddressI> resultList = new ArrayList<>();
//		for (int i = 0; i < NODE_RETURN; i++) {
//			resultList.add(connectToNode.get((i + index) % connectToNode.size()));
//		}

		String request = AbstractPort.generatePortURI();

		resultProbe.put(request, new HashSet<>());

		Semaphore s = new Semaphore(-NB_VOISIN);

		resultProbeSem.put(request, s);
		probe(request);
		resultProbeSem.get(request).acquire();
		portNode.acceptNeighbours(resultProbe.get(request));
		portNode.doDisconnection();
		portNode.unpublishPort();
		portNode.destroyPort();
		connectToNode.add(a);
		this.logMessage("join | fin " + a.getNodeIdentifier() + "\n");

	}

	public synchronized void acceptProbed(PeerNodeAddressI peer, String requestURI) {
		this.logMessage("acceptProbed | accept probe " + peer.getNodeIdentifier() + "\n");

		resultProbe.get(requestURI).add(peer);
		resultProbeSem.get(requestURI).release();

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */

	public synchronized void leave(PeerNodeAddressI a) throws Exception {
		this.traceMessage(a.getNodeIdentifier() + " leave the network \n");
		this.logMessage("leave | leave " + a.getNodeIdentifier() + "\n");

		connectToNode.remove(a);
		if (connectNodeRoot.containsKey(a)) {
			this.logMessage("leave | leave racine " + a.getNodeIdentifier() + "\n");
			OutPortContentManagement portAncien = connectNodeRoot.remove(a);
			portAncien.doDisconnection();
			portAncien.unpublishPort();
			portAncien.destroyPort();

			if (connectToNode.size() == 0) {
				return;
			}

			for (PeerNodeAddressI p : connectToNode) {
				if (connectNodeRoot.containsKey(p))
					continue;

				this.logMessage("leave | add racine  " + a.getNodeIdentifier() + "\n");

				OutPortContentManagement port = new OutPortContentManagement(this);
				port.publishPort();
				this.doPortConnection(port.getPortURI(), ((ContentNodeAddressI) p).getContentManagementURI(),
						ConnectorContentManagement.class.getCanonicalName());

				connectNodeRoot.put(p, port);
				return;
			}
		}

	}

	public void finalise() throws Exception {
		this.printExecutionLogOnFile(FacadeComponent.DIR_LOGGER_NAME + FacadeComponent.FILE_LOGGER_NAME
				+ facadeAdresses.getNodeIdentifier());
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet())
			port.getValue().doDisconnection();
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.facadePortNodeManagement.unpublishPort();
			this.facadePortNodeManagement.destroyPort();

			this.outFacadeNodeManagement.unpublishPort();
			this.outFacadeNodeManagement.destroyPort();

			this.outPortNode.unpublishPort();
			this.outPortNode.destroyPort();
			for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
				port.getValue().unpublishPort();
				port.getValue().destroyPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	private void probeRoot(int hops, FacadeNodeAddressI facade, String request, int nbVoisin,
			PeerNodeAddressI addressNode) throws Exception {

		int rand = (new Random()).nextInt(connectNodeRoot.size());
		int i = 0;

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			if (i == rand) {
				this.doPortConnection(outPortNode.getPortURI(), port.getKey().getNodeURI(), new ConnectorNode());
				outPortNode.probe(hops, facade, request, nbVoisin, addressNode);
				outPortNode.doDisconnection();
			}
			i++;
		}

		this.doPortConnection(outFacadeNodeManagement.getPortURI(), uriFacadeSuivante.getNodeManagementURI(),
				ConnectorNodeManagement.class.getCanonicalName());
		outFacadeNodeManagement.probe(hops, facade, request, nbVoisin, addressNode);
		outFacadeNodeManagement.doDisconnection();

	}

	private synchronized void probe(String request) throws Exception {

		this.logMessage("probe (private) | debut " + request + "\n");

		probeRoot(HOPS_PROBE, facadeAdresses, request, Integer.MAX_VALUE, null);

		this.logMessage("probe | fin " + request + "\n");

	}

	public synchronized void probe(int remaingHops, FacadeNodeAddressI facade, String request, int nbVoisin,
			PeerNodeAddressI addressNode) throws Exception {
		this.logMessage("probe (public) | debut request = " + request + " facade = " + facade + "\n");
		
		if (facadeAdresses.equals(facade))
			return;
		
		if(connectNodeRoot.size() == 0) {
			this.doPortConnection(outFacadeNodeManagement.getPortURI(), uriFacadeSuivante.getNodeManagementURI(),
					ConnectorNodeManagement.class.getCanonicalName());
			outFacadeNodeManagement.probe(remaingHops, facade, request, nbVoisin, addressNode);
			outFacadeNodeManagement.doDisconnection();
		}
		
		

		probeRoot(remaingHops, facade, request, nbVoisin, addressNode);

		this.logMessage("probe (public) | fin request = " + request + " facade = " + facade + "\n");

	}

}
