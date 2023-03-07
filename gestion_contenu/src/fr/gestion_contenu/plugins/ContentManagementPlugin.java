package fr.gestion_contenu.plugins;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.connectors.ConnectorResult;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.ReturnResultOutPort;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;

public class ContentManagementPlugin extends ConnectionNodePlugin implements IContentRequest {

	private static final long serialVersionUID = 1L;
	private ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private ContentNodeAddressI contentNodeAddress;
	private InPortContentManagement inPortContentManagement;

	public ContentManagementPlugin(ContentNodeAddressI contentNodeAddress) {
		super(contentNodeAddress);
		connectNodeContent = new ConcurrentHashMap<>();
		this.contentNodeAddress = contentNodeAddress;
	}

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
		this.addRequiredInterface(ReturnResultCI.class);
	}

	@Override
	public void initialise() throws Exception {

		super.initialise();
		inPortContentManagement = new InPortContentManagement(contentNodeAddress.getContentManagementURI(), getOwner(),
				getPluginURI());
		inPortContentManagement.publishPort();

	}

	private void returnFind(ContentDescriptorI cd, String uriReturn) throws Exception {
		getOwner().traceMessage("fin find node" + cd + "\n");
		ReturnResultOutPort result = new ReturnResultOutPort(getOwner());
		result.publishPort();
		getOwner().doPortConnection(result.getPortURI(), uriReturn, ConnectorResult.class.getCanonicalName());
		result.returnFind(null);
		result.doDisconnection();
		result.unpublishPort();
		result.destroyPort();
	}

	@Override
	public void find(ContentTemplateI cd, int hops, String uriReturn) throws Exception {
		getOwner().traceMessage("start find node" + cd + "\n");

		if (hops == 0) {
			returnFind(null, uriReturn);
			return;
		}
		ContentDescriptorI contentDescriptor;
		if ((contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd)) != null) {
			returnFind(contentDescriptor, uriReturn);
		}
		for (OutPortContentManagement port : connectNodeContent.values()) {
			port.find(cd, hops-1, uriReturn);
		}
		getOwner().traceMessage("fin find node" + cd + "\n");

	}

	@Override
	public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String uriReturn)
			throws Exception {
		getOwner().traceMessage("start match node" + cd + "\n");

		if (hops == 0) {
			ReturnResultOutPort result = new ReturnResultOutPort(getOwner());
			result.publishPort();
			getOwner().doPortConnection(result.getPortURI(), uriReturn, ConnectorResult.class.getCanonicalName());
			result.returnMatch(matched);
			result.doDisconnection();
			result.unpublishPort();
			result.destroyPort();
			return;
		}

		ContentDescriptorI contentDescriptor;
		if ((contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd)) != null
				&& !matched.contains(contentDescriptor)) {
			matched.add(contentDescriptor);
		}
		for (OutPortContentManagement op : connectNodeContent.values()) {
			op.match(cd, matched, hops-1, uriReturn);
		}
		getOwner().traceMessage("fin match node" + cd + "\n");

	}
	
	@Override
	public synchronized OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		if(connectNodeContent.containsKey(peer))
			return null;
		OutPortContentManagement c = super.connect(peer);
		connectNodeContent.put(peer, c);
		return c;
	}
	
	public synchronized void disconnect(PeerNodeAddressI peer) throws Exception {
		if(!connectNodeContent.containsKey(peer))
			return;
		super.disconnect(peer, connectNodeContent.remove(peer));
	}

	@Override
	public synchronized OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		if(connectNodeContent.containsKey(peer))
			return null;
		OutPortContentManagement c = super.connectBack(peer);
		connectNodeContent.put(peer, c);
		return c;
	}

	@Override
	public synchronized void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if(!connectNodeContent.containsKey(peer))
			return;
		super.disconnectBack(peer);
		OutPortContentManagement portContent = connectNodeContent.remove(peer);
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();
	}
	
	public synchronized void leave() throws Exception {
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement>  entry : connectNodeContent.entrySet()) {
			disconnect(entry.getKey());
		}
		getOwner().traceMessage("Fin leave \n");
	} 

	@Override
	public void finalise() throws Exception {
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().doDisconnection();
		}
		super.finalise();
	}

	@Override
	public void uninstall() throws Exception {
		inPortContentManagement.unpublishPort();
		inPortContentManagement.destroyPort();
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		super.uninstall();
	}

}
