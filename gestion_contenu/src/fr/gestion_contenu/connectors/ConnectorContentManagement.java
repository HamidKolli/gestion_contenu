package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 * Connecteur des traitements de contenu
 */
public class ConnectorContentManagement extends AbstractConnector implements ContentManagementCI {

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI, int)
	*
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {

		return ((InPortContentManagement) this.offering).find(cd, hops);
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI, java.util.Set, int)
	*
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops)
			throws Exception {

		return ((InPortContentManagement) this.offering).match(cd, matched, hops);
	}

}
