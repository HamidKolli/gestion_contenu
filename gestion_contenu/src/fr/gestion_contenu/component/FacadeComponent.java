package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractFacadeComponent;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { NodeCI.class, ContentManagementCI.class })
@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
public class FacadeComponent extends AbstractFacadeComponent {

	private ApplicationNodeAddress applicationNodeAddress;
	private FacadePortNodeManagement facadePortNodeManagement;
	private InPortContentManagement inPortContentManagement;
	private Map<PeerNodeAddressI, OutPortNode> connectToNode;
	private Map<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	public static final int NODE_RETURN = 2;

	protected FacadeComponent(ApplicationNodeAddress applicationNodeAddress) throws Exception {
		super(1, 0);
		this.applicationNodeAddress = applicationNodeAddress;
		
		this.connectToNode = new HashMap<>();
		this.connectNodeContent = new HashMap<>();
	}

	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.facadePortNodeManagement = new FacadePortNodeManagement(applicationNodeAddress.getNodeManagementURI(),this);
			this.inPortContentManagement = new InPortContentManagement(applicationNodeAddress.getContentManagementURI(),this);
			this.facadePortNodeManagement.publishPort();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
		System.out.println("join " + a.getNodeIdentifier());

		OutPortNode port = new OutPortNode(this);
		connectToNode.put(a, port);
		doPortConnection(port.getPortURI(), a.getNodeURI(), ConnectorNode.class.getCanonicalName());

		if (connectToNode.size() < NODE_RETURN) {
			return connectToNode.keySet();
		}
		Set<PeerNodeAddressI> result = new HashSet<>();

		List<PeerNodeAddressI> resultList = new ArrayList<>();
		PeerNodeAddressI[] resultTab = ((PeerNodeAddressI[]) connectToNode.keySet().toArray());
		for (PeerNodeAddressI peer : resultTab) {
			resultList.add(peer);
		}
		Collections.shuffle(resultList);

		result.addAll(resultList.subList(0, NODE_RETURN));

		return result;
	}

	public void leave(PeerNodeAddressI a) throws Exception {
		System.out.println("leave " + a.getNodeIdentifier());
		OutPortNode port = connectToNode.get(a);
		port.doDisconnection();
		port.destroyPort();
		connectToNode.remove(a);
	}

	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) {
		return null;
	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) {
		return null;
	}

	@Override
	public synchronized void finalise() throws Exception {
		for (Map.Entry<PeerNodeAddressI,OutPortNode> port : connectToNode.entrySet()) {
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
			this.facadePortNodeManagement.destroyPort();
			this.inPortContentManagement.destroyPort();
			for (Map.Entry<PeerNodeAddressI,OutPortNode> port : connectToNode.entrySet()) {
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
