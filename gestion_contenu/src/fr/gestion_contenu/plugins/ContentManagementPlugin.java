package fr.gestion_contenu.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;

public class ContentManagementPlugin extends ConnectionNodePlugin implements IContentRequest{

	
	private static final long serialVersionUID = 1L;
	private Map<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private ContentNodeAddressI contentNodeAddress;
	private InPortContentManagement inPortContentManagement;
	
	public ContentManagementPlugin( ContentNodeAddressI contentNodeAddress) {
		super(contentNodeAddress);
		connectNodeContent = new HashMap<>();
		this.contentNodeAddress = contentNodeAddress;
	}	
	
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
		System.out.println(getPluginURI());
		inPortContentManagement = new InPortContentManagement(contentNodeAddress.getContentManagementURI(), owner,getPluginURI());
	}
	
	@Override
	public void initialise() throws Exception {
		inPortContentManagement.publishPort();
		super.initialise();
	}
	
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		System.out.println("start find node" + cd);
		if (hops == 0) {
			System.out.println("fin find node" + cd);
			return null;
		}
		ContentDescriptorI contentDescriptor;
		if ((contentDescriptor = ((AbstractNodeComponent)getOwner()).match(cd)) != null) {
			System.out.println("fin find node" + cd);
			return contentDescriptor;
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
			ContentDescriptorI contentDescriptor;
			if ((contentDescriptor = ((AbstractNodeComponent)getOwner()).match(cd)) != null){

				if (!matched.contains(contentDescriptor))
					matched.add(contentDescriptor);
			}
			System.out.println("loop match node");
			if (hops != 0)
				matched = op.match(cd, matched, hops - 1);
		}
		System.out.println("fin match node" + cd);

		return matched;
	}
	
	
	public OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		OutPortContentManagement c = super.connect(peer);
		connectNodeContent.put(peer,c);
		return c;
	}
	

	public void disconnect(PeerNodeAddressI peer) throws Exception {
		super.disconnect(peer, connectNodeContent.remove(peer));
	}
	
	@Override
	public OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		OutPortContentManagement c =super.connectBack(peer);
		connectNodeContent.put(peer,c);
		return c;
	}
	
	@Override
	public void disconnectBack(PeerNodeAddressI peer) throws Exception {
		super.disconnectBack(peer);
		OutPortContentManagement portContent = connectNodeContent.get(peer);
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();
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
