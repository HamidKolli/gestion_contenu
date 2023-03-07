package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class ConnectionNodePlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	private PeerNodeAddressI nodeAddresses;
	private ConcurrentMap<PeerNodeAddressI, OutPortNode> connectOutPort;
	private InPortNode connectInPort;

	public ConnectionNodePlugin(PeerNodeAddressI nodeAddresses) {
		super();
		this.nodeAddresses = nodeAddresses;
		connectOutPort = new ConcurrentHashMap<>();
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 *
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		assert owner instanceof AbstractNodeComponent;
		super.installOn(owner);
		this.addRequiredInterface(NodeCI.class);
		this.addOfferedInterface(NodeCI.class);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		this.connectInPort = new InPortNode(nodeAddresses.getNodeURI(), getOwner(), getPluginURI());

		this.connectInPort.publishPort();

	}

	protected OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		getOwner().traceMessage("connect to " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.connect(nodeAddresses);
		connectOutPort.put(peer, port);

		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		getOwner().traceMessage("connect reussi " + peer.getNodeIdentifier() + "\n");
		return portContent;
	}

	protected void disconnect(PeerNodeAddressI peer, OutPortContentManagement portContent) throws Exception {
		if(!connectOutPort.containsKey(peer))
			return;
		OutPortNode port = connectOutPort.remove(peer);
		getOwner().traceMessage("disconnect from " + peer.getNodeIdentifier() + "\n");
		port.disconnect(nodeAddresses);
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();

		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();

		getOwner().traceMessage("fin disconnect " + peer.getNodeIdentifier() + "\n");

	}

	protected OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		if(connectOutPort.containsKey(peer))
			return null;
		getOwner().traceMessage("connect back to " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());

		getOwner().traceMessage("fin connect back to " + peer.getNodeIdentifier() + "\n");
		return portContent;

	}

	protected void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if(!connectOutPort.containsKey(peer))
			return;
		getOwner().traceMessage("disconnect back from " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = connectOutPort.remove(peer);
		
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();
		getOwner().traceMessage("fin disconnect back from " + peer.getNodeIdentifier() + "\n");

	}

	@Override
	public void finalise() throws Exception {
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++)
			ports.get(i).getValue().doDisconnection();
		super.finalise();
	}

	@Override
	public void uninstall() throws Exception {
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++) {
			ports.get(i).getValue().unpublishPort();
			ports.get(i).getValue().destroyPort();
		}
		connectInPort.unpublishPort();
		connectInPort.destroyPort();
		super.uninstall();
	}

}
