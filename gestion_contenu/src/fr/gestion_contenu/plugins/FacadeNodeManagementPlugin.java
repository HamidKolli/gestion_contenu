package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.IConnectFacadeRequest;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class FacadeNodeManagementPlugin extends AbstractPlugin implements IConnectFacadeRequest {

	private static final long serialVersionUID = 1L;
	protected Set<PeerNodeAddressI> connectToNode;
	protected ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private FacadePortNodeManagement facadePortNodeManagement;
	private String facadePortNodeManagementURI;
	private final int numberRootNode;
	public static final int NODE_RETURN = 2;

	public FacadeNodeManagementPlugin(String facadePortNodeManagementURI, int nbRoot) {
		super();
		this.facadePortNodeManagementURI = facadePortNodeManagementURI;
		connectToNode = new HashSet<>();
		connectNodeRoot = new ConcurrentHashMap<>();
		this.numberRootNode = nbRoot;
	}

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(NodeManagementCI.class);
		this.addRequiredInterface(NodeManagementCI.class);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		facadePortNodeManagement = new FacadePortNodeManagement(facadePortNodeManagementURI, getOwner(),
				getPluginURI());
		facadePortNodeManagement.publishPort();
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
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
			return result;
		}

		result = new HashSet<>();
		List<PeerNodeAddressI> resultList = new ArrayList<>(connectToNode);

		Collections.shuffle(resultList);

		result.addAll(resultList.subList(0, Math.max(NODE_RETURN, resultList.size())));
		getOwner().traceMessage("fin join " + a.getNodeIdentifier() + "\n");

		return result;
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

	@Override
	public void finalise() throws Exception {

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet())
			port.getValue().doDisconnection();
		super.finalise();
	}

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

}
