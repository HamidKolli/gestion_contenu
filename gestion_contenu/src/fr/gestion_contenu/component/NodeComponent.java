package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class, ContentManagementCI.class })
@OfferedInterfaces(offered = { NodeCI.class, ContentManagementCI.class })
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

			this.inPortContentManagement.publishPort();
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
		System.out.println("re " + contentDescriptorI.getContentNodeAddress().getNodeIdentifier());
		for (PeerNodeAddressI peerNodeAddressI : peersVoisins) {
			if (!peerNodeAddressI.equals(contentDescriptorI.getContentNodeAddress())) {
				connect(peerNodeAddressI);
			}

		}
		
		
	

		super.execute();
	}

	public void leave() throws Exception {
		portNodeManagement.leave(contentDescriptorI.getContentNodeAddress());
		
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++)
			disconnect(ports.get(i).getKey());
	}

	public void connect(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect to " + peer.getNodeIdentifier());
		OutPortNode port = new OutPortNode(this);
		port.publishPort();
		doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.connect(contentDescriptorI.getContentNodeAddress());

		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(this);
		portContent.publishPort();
		doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		connectNodeContent.put(peer, portContent);
		System.out.println("connect reussi " + peer.getNodeIdentifier());
	}

	public void disconnect(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect from " + peer.getNodeIdentifier());
		OutPortNode port = connectOutPort.get(peer);

		port.disconnect(contentDescriptorI.getContentNodeAddress());

		doPortDisconnection(port.getPortURI());
		port.unpublishPort();
		connectOutPort.remove(peer);

		OutPortContentManagement portContent = connectNodeContent.get(peer);
		doPortDisconnection(portContent.getPortURI());

		portContent.unpublishPort();

		connectNodeContent.remove(peer);
		System.out.println("fin disconnect " + peer.getNodeIdentifier());

	}

	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		System.out.println("start find node" + cd);
		if (hops == 0) {
			System.out.println("fin find node" + cd);
			return null;
		}
		if (contentDescriptorI.match(cd)) {
			System.out.println("fin find node" + cd);
			return contentDescriptorI;
		}
		ContentDescriptorI tmp;
		for (OutPortContentManagement port : connectNodeContent.values()) {

			if ((tmp = port.find(cd, hops - 1)) != null) {
				System.out.println("fin find node" + cd);
				return tmp;
			}
		}
		System.out.println("fin find node" + cd);
		return null;
	}

	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {
		System.out.println("start match node" + cd);

		for (OutPortContentManagement op : connectNodeContent.values()) {
			boolean bool;
			if ((bool = contentDescriptorI.match(cd))) {
				System.out.println("match node trouve ? " + bool);
				if (!matched.contains(contentDescriptorI))
					matched.add(contentDescriptorI);
			}
			System.out.println("loop match node");
			if (hops != 0)
				matched = op.match(cd, matched, hops - 1);
		}
		System.out.println("fin match node" + cd);

		return matched;
	}

	@Override
	public PeerNodeAddressI connectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("connect back to " + peer.getNodeIdentifier());
		OutPortNode port = new OutPortNode(this);
		port.publishPort();
		doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(this);
		portContent.publishPort();
		doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		connectNodeContent.put(peer, portContent);
		System.out.println("fin connect back to " + peer.getNodeIdentifier());
		return contentDescriptorI.getContentNodeAddress();
	}

	@Override
	public void disconnectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect back from " + peer.getNodeIdentifier());
		OutPortNode port = connectOutPort.get(peer);
		connectOutPort.remove(peer);
		doPortDisconnection(port.getPortURI());
		port.unpublishPort();
		OutPortContentManagement portContent = connectNodeContent.get(peer);
		doPortDisconnection(portContent.getPortURI());
		portContent.unpublishPort();
		connectNodeContent.remove(peer);
		System.out.println("fin disconnect back from " + peer.getNodeIdentifier());

	}

	@Override
	public synchronized void finalise() throws Exception {
		this.portNodeManagement.doDisconnection();
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++)
			ports.get(i).getValue().doDisconnection();
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().doDisconnection();
		}
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			portNodeManagement.unpublishPort();
			this.inPortContentManagement.unpublishPort();
			this.connectInPort.unpublishPort();
			for (Map.Entry<PeerNodeAddressI, OutPortNode> port : connectOutPort.entrySet()) {
				port.getValue().unpublishPort();
			}
			for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
				port.getValue().unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
