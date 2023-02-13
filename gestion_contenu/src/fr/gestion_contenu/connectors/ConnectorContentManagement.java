package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ConnectorContentManagement extends AbstractConnector implements ContentManagementCI {

	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {

		return ((InPortContentManagement) this.offering).find(cd, hops);
	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {

		return ((InPortContentManagement) this.offering).match(cd, matched, hops);
	}

}
