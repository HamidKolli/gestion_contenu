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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { NodeManagementCI.class, NodeCI.class, ContentManagementCI.class })
@OfferedInterfaces(offered = { NodeCI.class, ContentManagementCI.class })

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe conctrete qui gere les composant de noeud 
 *
 */
public class NodeComponent extends AbstractNodeComponent {
	
	private ContentDescriptorI contentDescriptorI;
	private NodePortNodeManagement portNodeManagement;
	private Map<PeerNodeAddressI, OutPortNode> connectOutPort;
	private Map<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private InPortNode connectInPort;
	private InPortContentManagement inPortContentManagement;
	private String portFacadeManagementURI;

	/**
	 * 
	 * Constructeur NodeComponent 
	 * @param contentDescriptorI : le descripteur du noeud
	 * @param portFacadeManagementURI : l'uri du port entrant d'une facade (pour demander de joindre le reseau)
	 * @throws Exception
	 */
	protected NodeComponent(ContentDescriptorI contentDescriptorI, String portFacadeManagementURI) throws Exception {
		super(1, 0);
		this.contentDescriptorI = contentDescriptorI;
		this.portFacadeManagementURI = portFacadeManagementURI;
		this.portNodeManagement = new NodePortNodeManagement(this);
		this.connectOutPort = new HashMap<>();
		this.connectNodeContent = new HashMap<>();

	}
	
	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
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

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#join()
	*
	 */
	@Override
	public Set<PeerNodeAddressI> join() throws Exception {
		doPortConnection(this.portNodeManagement.getPortURI(), this.portFacadeManagementURI,
				ConnectorNodeManagement.class.getCanonicalName());
		Set<PeerNodeAddressI> peersVoisins = portNodeManagement.join(contentDescriptorI.getContentNodeAddress());
		return peersVoisins;
	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#execute()
	*
	 */
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
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#leave()
	*
	 */
	@Override
	public void leave() throws Exception {
		portNodeManagement.leave(contentDescriptorI.getContentNodeAddress());
		
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++)
			disconnect(ports.get(i).getKey());
		
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#connect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
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

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#disconnect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public void disconnect(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect from " + peer.getNodeIdentifier());
		OutPortNode port = connectOutPort.get(peer);

		port.disconnect(contentDescriptorI.getContentNodeAddress());

		doPortDisconnection(port.getPortURI());
		port.unpublishPort();
		port.destroyPort();
		connectOutPort.remove(peer);

		OutPortContentManagement portContent = connectNodeContent.get(peer);
		doPortDisconnection(portContent.getPortURI());

		portContent.unpublishPort();
		portContent.destroyPort();

		connectNodeContent.remove(peer);
		System.out.println("fin disconnect " + peer.getNodeIdentifier());

	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI, int)
	*
	 */
	@Override
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

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI, java.util.Set, int)
	*
	 */
	@Override
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

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#connectBack(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public void connectBack(PeerNodeAddressI peer) throws Exception {
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
		
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#disconnectBack(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public void disconnectBack(PeerNodeAddressI peer) throws Exception {
		System.out.println("disconnect back from " + peer.getNodeIdentifier());
		OutPortNode port = connectOutPort.get(peer);
		connectOutPort.remove(peer);
		doPortDisconnection(port.getPortURI());
		port.unpublishPort();
		port.destroyPort();
		OutPortContentManagement portContent = connectNodeContent.get(peer);
		doPortDisconnection(portContent.getPortURI());
		portContent.unpublishPort();
		portContent.destroyPort();
		connectNodeContent.remove(peer);
		System.out.println("fin disconnect back from " + peer.getNodeIdentifier());

	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#finalise()
	*
	 */
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

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	*
	 */
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
