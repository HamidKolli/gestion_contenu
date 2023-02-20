package fr.gestion_contenu.plugins;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;

public class FacadeContentManagementPlugin extends FacadeNodeManagementPlugin implements IContentRequest{

	
	private static final long serialVersionUID = 1L;
	
	private String contentManagementURI;
	private InPortContentManagement portContentManagement;
	public FacadeContentManagementPlugin(ApplicationNodeAddress application, int nbRoot) {
		super(application.getNodeManagementURI(),nbRoot);
		this.contentManagementURI = application.getContentManagementURI();
	}
	
	@Override
	public void installOn(ComponentI owner) throws Exception {

		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
	}
	
	@Override
	public void initialise() throws Exception {
		super.initialise();
		portContentManagement = new InPortContentManagement(contentManagementURI, getOwner(),getPluginURI());
		portContentManagement.publishPort();

	}
	
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI, int)
	*
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		System.out.println("start find facade"+ cd);
		ContentDescriptorI c;
		for (OutPortContentManagement op : connectNodeRoot.values()) {
			if((c = op.find(cd, hops)) != null) {
				System.out.println("fin find facade"+ cd);
				return c;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI, java.util.Set, int)
	*
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) throws Exception {
		System.out.println("start match facade"+ cd);
		for (OutPortContentManagement op : connectNodeRoot.values()) {
			System.out.println("loop match facade");
			op.match(cd, matched, hops);
		}
		System.out.println("fin match facade"+ cd);
		
		return matched;
	}
	

	
	@Override
	public void uninstall() throws Exception {
		portContentManagement.unpublishPort();
		portContentManagement.destroyPort();
		super.uninstall();
	}
	
	
	
	
	
}
