package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.classes.ContentDescriptor;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class ConnectionNodePlugin extends AbstractPlugin {

	
	private static final long serialVersionUID = 1L;
	
	
	private PeerNodeAddressI nodeAddresses;
	private Map<PeerNodeAddressI, OutPortNode> connectOutPort;
	private InPortNode connectInPort;


	public ConnectionNodePlugin(PeerNodeAddressI nodeAddresses, String portFacadeManagementURI) {
		super();
		this.nodeAddresses = nodeAddresses;
		connectOutPort = new HashMap<>();
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 *
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		
		assert owner instanceof AbstractNodeComponent;

		this.addRequiredInterface(NodeCI.class);
		
		this.addOfferedInterface(NodeCI.class);
		
		
		

		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		this.connectInPort = new InPortNode(nodeAddresses.getNodeURI(), getOwner());
		this.connectInPort.publishPort();
		super.initialise();
	}
	
	



	public  OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect to " + peer.getNodeIdentifier());
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.connect(nodeAddresses);
		connectOutPort.put(peer, port);
		
		
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		System.out.println("connect reussi " + peer.getNodeIdentifier());
		return  portContent;
	}


	public void disconnect(PeerNodeAddressI peer,OutPortContentManagement portContent) throws Exception {
		OutPortNode port = connectOutPort.get(peer);
		connectOutPort.remove(peer);
		System.out.println("disconnect from " + peer.getNodeIdentifier());
		port.disconnect(nodeAddresses);
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();
		
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();
		
		System.out.println("fin disconnect " + peer.getNodeIdentifier());

	}

	
	

	public OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect back to " + peer.getNodeIdentifier());
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		
		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		
		System.out.println("fin connect back to " + peer.getNodeIdentifier());
		return portContent;
		
	}


	public void disconnectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect back from " + peer.getNodeIdentifier());
		OutPortNode port = connectOutPort.get(peer);
		connectOutPort.remove(peer);
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();
		System.out.println("fin disconnect back from " + peer.getNodeIdentifier());
		

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