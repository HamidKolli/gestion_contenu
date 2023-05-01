package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes connexions et deconnexions entre
 *         une fa�ade et les noeuds pairs racines
 */
public class FacadeNodeManagementPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;
	private static final int HOPS_PROBE = 5;
	private static final int NB_VOISIN = 2;
	protected List<PeerNodeAddressI> connectToNode;
	protected ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private FacadePortNodeManagement facadePortNodeManagement;
	private NodePortNodeManagement outFacadeNodeManagement;
	private ApplicationNodeAddressI facadePortNodeManagementURI;
	private OutPortNode outPortNode;
	private ApplicationNodeAddressI uriFacadeSuivante;
	private final int numberRootNode;
	private String nodeManagementURI;
	private ConcurrentMap<String, Semaphore> resultProbeSem;
	private ConcurrentMap<String, Set<PeerNodeAddressI>> resultProbe;

	/**
	 * Constructeur
	 * 
	 * @param applicationNodeAddress
	 * @param nbRoot
	 * @param uriNodeManagement
	 */
	public FacadeNodeManagementPlugin(ApplicationNodeAddressI applicationNodeAddress, int nbRoot,
			String uriNodeManagement, ApplicationNodeAddressI uriFacadeSuivante2) {
		super();
		this.facadePortNodeManagementURI = applicationNodeAddress;
		connectToNode = new ArrayList<>();
		connectNodeRoot = new ConcurrentHashMap<>();
		resultProbeSem = new ConcurrentHashMap<>();
		resultProbe = new ConcurrentHashMap<>();
		this.numberRootNode = nbRoot;
		this.nodeManagementURI = uriNodeManagement;
		this.uriFacadeSuivante = uriFacadeSuivante2;

	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#installOn(ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(NodeManagementCI.class);
		this.addRequiredInterface(NodeManagementCI.class);
		this.addRequiredInterface(NodeCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		super.initialise();
//		facadePortNodeManagement = new FacadePortNodeManagement(facadePortNodeManagementURI.getNodeManagementURI(),
//				getOwner(), getPluginURI(), nodeManagementURI);
//		outFacadeNodeManagement = new NodePortNodeManagement(getOwner());
		outPortNode = new OutPortNode(getOwner());
		outPortNode.publishPort();
		outFacadeNodeManagement.publishPort();
		facadePortNodeManagement.publishPort();

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */

	public void join(PeerNodeAddressI a) throws Exception {

		getOwner().traceMessage(a.getNodeIdentifier() + " join network ");
		getOwner().logMessage("join |"+a.getNodeIdentifier()+" root? "+(connectNodeRoot.size() < numberRootNode)+"\n");
		OutPortNode portNode = new OutPortNode(getOwner());
		portNode.publishPort();
		getOwner().doPortConnection(portNode.getPortURI(), a.getNodeURI(), ConnectorNode.class.getCanonicalName());

		Set<PeerNodeAddressI> result;

		if (connectNodeRoot.size() < numberRootNode) {
			OutPortContentManagement port = new OutPortContentManagement(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), ((ContentNodeAddressI) a).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			connectNodeRoot.put(a, port);
		}

		if (connectToNode.size() < NB_VOISIN) {
			result = new HashSet<>(connectToNode);
			getOwner().logMessage("join | fin "+a.getNodeIdentifier()+"\n");
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
		getOwner().logMessage("join | fin "+a.getNodeIdentifier()+"\n");

	}

	public synchronized void acceptProbed(PeerNodeAddressI peer, String requestURI) {
		getOwner().logMessage("acceptProbed | accept probe "+peer.getNodeIdentifier()+"\n");

		resultProbe.get(requestURI).add(peer);
		resultProbeSem.get(requestURI).release();

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */

	public synchronized void leave(PeerNodeAddressI a) throws Exception {
		getOwner().traceMessage(a.getNodeIdentifier() + " leave the network \n");
		getOwner().logMessage("leave | leave "+a.getNodeIdentifier()+"\n");

		connectToNode.remove(a);
		if (connectNodeRoot.containsKey(a)) {
			getOwner().logMessage("leave | leave racine "+a.getNodeIdentifier()+"\n");
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

				getOwner().logMessage("leave | add racine  "+a.getNodeIdentifier()+"\n");

				OutPortContentManagement port = new OutPortContentManagement(getOwner());
				port.publishPort();
				getOwner().doPortConnection(port.getPortURI(), ((ContentNodeAddressI) p).getContentManagementURI(),
						ConnectorContentManagement.class.getCanonicalName());

				connectNodeRoot.put(p, port);
				return;
			}
		}

	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */

	public void finalise() throws Exception {
		getOwner().printExecutionLogOnFile(FacadeComponent.DIR_LOGGER_NAME + FacadeComponent.FILE_LOGGER_NAME
				+ facadePortNodeManagementURI.getNodeIdentifier());
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet())
			port.getValue().doDisconnection();
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */

	public void uninstall() throws Exception {
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
		super.uninstall();
	}

	private synchronized void probe(String request) throws Exception {

		getOwner().logMessage("probe (private) | debut "+request+"\n");

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			
			getOwner().doPortConnection(outPortNode.getPortURI(), port.getKey().getNodeURI(),
					ConnectorNode.class.getCanonicalName());
			outPortNode.probe(HOPS_PROBE, facadePortNodeManagementURI, request, Integer.MAX_VALUE, null);
			outPortNode.doDisconnection();
			

		}

		getOwner().doPortConnection(outFacadeNodeManagement.getPortURI(), uriFacadeSuivante.getNodeManagementURI(),
				ConnectorNodeManagement.class.getCanonicalName());
		outFacadeNodeManagement.probe(HOPS_PROBE, facadePortNodeManagementURI, request, Integer.MAX_VALUE, null);
		outFacadeNodeManagement.doDisconnection();
		getOwner().logMessage("probe | fin "+request+"\n");

	}

	public synchronized void probe(int remaingHops, FacadeNodeAddressI facade, String request, int nbVoisin, PeerNodeAddressI addressNode) throws Exception {
		getOwner().logMessage("probe (public) | debut request = "+request+" facade = "+facade+"\n");
		if (facadePortNodeManagementURI.equals(facade))
			return;

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			getOwner().doPortConnection(outPortNode.getPortURI(), port.getKey().getNodeURI(), new ConnectorNode());
			outPortNode.probe(HOPS_PROBE, facade, request,nbVoisin,addressNode);
			outPortNode.doDisconnection();
		}

		getOwner().doPortConnection(outFacadeNodeManagement.getPortURI(), uriFacadeSuivante.getNodeManagementURI(),
				ConnectorNodeManagement.class.getCanonicalName());
		outFacadeNodeManagement.probe(remaingHops, facade, request,nbVoisin,addressNode);
		outFacadeNodeManagement.doDisconnection();
		getOwner().logMessage("probe (public) | fin request = "+request+" facade = "+facade+"\n");

	}

}
