package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OutPortContentManagement extends AbstractOutboundPort implements ContentManagementCI {

	private static final long serialVersionUID = 1L;

	public OutPortContentManagement(ComponentI owner) throws Exception {
		super(ContentManagementCI.class, owner);
	}

	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		return ((ConnectorContentManagement) getConnector()).find(cd, hops);
	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {
		return ((ConnectorContentManagement) getConnector()).match(cd, matched, hops);
	}

}
