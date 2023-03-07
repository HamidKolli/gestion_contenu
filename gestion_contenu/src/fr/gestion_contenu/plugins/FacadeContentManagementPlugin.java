package fr.gestion_contenu.plugins;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;

public class FacadeContentManagementPlugin extends FacadeNodeManagementPlugin implements IContentRequest {

	private static final long serialVersionUID = 1L;

	private String contentManagementURI;
	private InPortContentManagement portContentManagement;

	public FacadeContentManagementPlugin(ApplicationNodeAddress application, int nbRoot) {
		super(application.getNodeManagementURI(), nbRoot);
		this.contentManagementURI = application.getContentManagementURI();
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
		portContentManagement = new InPortContentManagement(contentManagementURI, getOwner(), getPluginURI());
		portContentManagement.publishPort();

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      int)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops,String uriReturn) throws Exception {
		getOwner().traceMessage("start find facade" + cd + "\n");
		for (OutPortContentManagement op : connectNodeRoot.values()) {
				op.find(cd, hops, uriReturn);
		}
		getOwner().traceMessage("fin find facade" + cd + "\n");
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      java.util.Set, int)
	 *
	 */
	@Override
	public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops,String uriReturn)
			throws Exception {
		getOwner().traceMessage("start match facade" + cd + "\n");
		for (OutPortContentManagement op : connectNodeRoot.values()) {
			getOwner().traceMessage("loop match facade");
			op.match(cd, matched, hops,uriReturn);
		}
		getOwner().traceMessage("fin match facade" + cd + "\n");
	}

	@Override
	public void uninstall() throws Exception {
		portContentManagement.unpublishPort();
		portContentManagement.destroyPort();
		super.uninstall();
	}

}
