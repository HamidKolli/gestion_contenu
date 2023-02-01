package fr.gestion_contenu.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class NodeComponent extends AbstractNodeComponent {
	private ContentDescriptorI contentDescriptorI;
	private NodePortNodeManagement portNodeManagement;
	private Map<PeerNodeAddressI, OutPortNode> connectOutPort;
	private Map<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private InPortNode connectInPort;
	private InPortContentManagement inPortContentManagement;
	private String portFacadeManagementURI;

	protected NodeComponent(ContentDescriptorI contentDescriptorI, String portFacadeManagementURI) throws Exception {
		super(1, 0);
		this.contentDescriptorI = contentDescriptorI;
		this.portFacadeManagementURI = portFacadeManagementURI;
		this.portNodeManagement = new NodePortNodeManagement(this);
		this.connectOutPort = new HashMap<>();
		this.connectNodeContent = new HashMap<>();

	}

	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.inPortContentManagement = new InPortContentManagement(
					contentDescriptorI.getContentNodeAddress().getContentManagementURI(), this);
			this.connectInPort = new InPortNode(contentDescriptorI.getContentNodeAddress().getNodeURI(), this);
			this.portNodeManagement = new NodePortNodeManagement(this);
			this.portNodeManagement.publishPort();
			this.connectInPort.publishPort();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	public Set<PeerNodeAddressI> join() throws Exception {
		doPortConnection(this.portNodeManagement.getPortURI(), this.portFacadeManagementURI,
				ConnectorNodeManagement.class.getCanonicalName());
		Set<PeerNodeAddressI> peersVoisins = portNodeManagement.join(contentDescriptorI.getContentNodeAddress());
		return peersVoisins;
	}

	@Override
	public void execute() throws Exception {
		Set<PeerNodeAddressI> peersVoisins = join();
		for (PeerNodeAddressI peerNodeAddressI : peersVoisins) {
			connect(peerNodeAddressI);
		}
		super.execute();
	}

	public void leave() throws Exception {
		portNodeManagement.leave(contentDescriptorI.getContentNodeAddress());
		this.portNodeManagement.doDisconnection();
		for (Map.Entry<PeerNodeAddressI, OutPortNode> port : connectOutPort.entrySet()) {
			port.getValue().disconnect(port.getKey());
			port.getValue().doDisconnection();
		}
	}

	public void connect(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect to " + peer);
		OutPortNode port = new OutPortNode(this);
		port.publishPort();
		doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		PeerNodeAddressI result = port.connect(contentDescriptorI.getContentNodeAddress());
		connectOutPort.put(result, port);
		OutPortContentManagement portContent = new OutPortContentManagement(this);
		portContent.publishPort();
		doPortConnection(portContent.getPortURI(),
				((ContentNodeAddressI) result).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		connectNodeContent.put(result, portContent);
	}

	public void disconnect(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect from " + peer);
		OutPortNode port = connectOutPort.get(peer);
		port.disconnect(peer);
		connectOutPort.remove(peer);
		port.doDisconnection();
		OutPortContentManagement portContent = connectNodeContent.get(peer);
		portContent.doDisconnection();
		connectNodeContent.remove(peer);
	}

	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		return null;
	}

	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {
		return null;
	}

	@Override
	public PeerNodeAddressI connectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect back to " + peer);
		OutPortNode port = new OutPortNode(this);
		port.publishPort();
		doPortConnection(port.getPortURI(), peer.getNodeURI(),
				ConnectorNode.class.getCanonicalName());
		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(this);
		portContent.publishPort();
		doPortConnection(portContent.getPortURI(),
				((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		connectNodeContent.put(peer, portContent);
		return contentDescriptorI.getContentNodeAddress();
	}

	@Override
	public void disconnectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect back from " + peer);
		OutPortNode port = connectOutPort.get(peer);
		connectOutPort.remove(peer);
		port.doDisconnection();
		OutPortContentManagement portContent = connectNodeContent.get(peer);
		portContent.doDisconnection();
		connectNodeContent.remove(peer);
		
	}

	@Override
	public synchronized void finalise() throws Exception {
		portNodeManagement.doDisconnection();
		
		for (Map.Entry<PeerNodeAddressI,OutPortNode> port : connectOutPort.entrySet()) {
			port.getValue().doDisconnection();
		}
		for (Map.Entry<PeerNodeAddressI,OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().doDisconnection();
		}
		
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			portNodeManagement.destroyPort();
			for (Map.Entry<PeerNodeAddressI,OutPortNode> port : connectOutPort.entrySet()) {
				port.getValue().destroyPort();
			}
			for (Map.Entry<PeerNodeAddressI,OutPortContentManagement> port : connectNodeContent.entrySet()) {
				port.getValue().destroyPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
