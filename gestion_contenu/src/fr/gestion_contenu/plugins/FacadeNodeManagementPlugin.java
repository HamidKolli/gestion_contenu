package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.IConnectFacadeRequest;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes connexions et deconnexions entre une fa�ade et les noeuds pairs racines
 */
public class FacadeNodeManagementPlugin extends AbstractPlugin implements IConnectFacadeRequest {

	private static final long serialVersionUID = 1L;
	protected List<PeerNodeAddressI> connectToNode;
	protected ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private FacadePortNodeManagement facadePortNodeManagement;
	private ApplicationNodeAddress facadePortNodeManagementURI;
	private final int numberRootNode;
	public static final int NODE_RETURN = 3;

	/**
	 * Constructeur
	 * @param application
	 * @param nbRoot
	 */
	public FacadeNodeManagementPlugin(ApplicationNodeAddress application, int nbRoot) {
		super();
		this.facadePortNodeManagementURI = application;
		connectToNode = new ArrayList<>();
		connectNodeRoot = new ConcurrentHashMap<>();
		this.numberRootNode = nbRoot;
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
		facadePortNodeManagement = new FacadePortNodeManagement(facadePortNodeManagementURI.getNodeManagementURI(), getOwner(),
				getPluginURI());
		facadePortNodeManagement.publishPort();
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void join(PeerNodeAddressI a) throws Exception {
		
		OutPortNode portNode = new OutPortNode(getOwner());
		portNode.publishPort();
		getOwner().doPortConnection(portNode.getPortURI(), a.getNodeURI(), ConnectorNode.class.getCanonicalName());
		
		getOwner().traceMessage("join " + a.getNodeIdentifier() + "\n");
		if (connectNodeRoot.size() < numberRootNode) {
			OutPortContentManagement port = new OutPortContentManagement(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), ((ContentNodeAddressI) a).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			connectNodeRoot.put(a, port);
		}

		connectToNode.add(a);
		Set<PeerNodeAddressI> result;

		if (connectToNode.size() < NODE_RETURN) {
			result = new HashSet<>(connectToNode);
			getOwner().traceMessage("fin join " + a.getNodeIdentifier() + "\n");
			portNode.acceptNeighbours(result);
			portNode.doDisconnection();
			portNode.unpublishPort();
			portNode.destroyPort();
		}

		
		int index = Math.abs((new Random()).nextInt() % connectToNode.size());
		List<PeerNodeAddressI> resultList = new ArrayList<>();
		for (int i = 0; i < NODE_RETURN; i++) {
			resultList.add(connectToNode.get((i + index)% connectToNode.size()));
		}
		
		result = new HashSet<>(resultList);
		getOwner().traceMessage("fin join " + a.getNodeIdentifier() + "\n");

		portNode.acceptNeighbours(result);
		portNode.doDisconnection();
		portNode.unpublishPort();
		portNode.destroyPort();
		
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public synchronized void leave(PeerNodeAddressI a) throws Exception {
		getOwner().traceMessage("leave " + a.getNodeIdentifier() + "\n");
		connectToNode.remove(a);
		if (connectNodeRoot.containsKey(a)) {
			getOwner().traceMessage("leave racine " + a.getNodeIdentifier() + "\n");
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

				getOwner().traceMessage("leave add racine " + p.getNodeIdentifier() + "\n");
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
	@Override
	public void finalise() throws Exception {

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet())
			port.getValue().doDisconnection();
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		this.facadePortNodeManagement.unpublishPort();
		this.facadePortNodeManagement.destroyPort();

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		super.uninstall();
	}

	@Override
	public void acceptProbed(PeerNodeAddressI peer, String requestURI) {
		// TODO Auto-generated method stub
		
	}

}
