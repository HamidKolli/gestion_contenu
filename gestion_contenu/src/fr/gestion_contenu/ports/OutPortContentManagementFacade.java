package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OutPortContentManagementFacade extends AbstractOutboundPort implements FacadeContentManagementCI {

	/**
	 * serialVersionUID : de type long gere
	 */
	private static final long serialVersionUID = 1L;

	public OutPortContentManagementFacade(ComponentI owner) throws Exception {
		super(FacadeContentManagementCI.class, owner);
	}

	@Override
	public ContentDescriptorI find(ContentTemplateI template, int hops) throws Exception {
		return ((FacadeContentManagementCI) getConnector()).find(template, hops);
	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template, int hops, Set<ContentDescriptorI> matched)
			throws Exception {
		return ((FacadeContentManagementCI) getConnector()).match(template, hops,matched);
	}

	@Override
	public void acceptFound(ContentDescriptorI found, String requestURI) throws Exception {
		((FacadeContentManagementCI) getConnector()).acceptFound(found, requestURI);
	}

	@Override
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) throws Exception {
		((FacadeContentManagementCI) getConnector()).acceptMatched(matched, requestURI);
	}

}
