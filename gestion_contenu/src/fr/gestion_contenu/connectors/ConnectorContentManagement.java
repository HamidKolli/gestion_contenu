package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Connecteur des traitements de contenu
 */
public class ConnectorContentManagement extends AbstractConnector implements ContentManagementCI {

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      int)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI) throws Exception {
		((InPortContentManagement) this.offering).find(cd, hops, facade, requestURI);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      java.util.Set, int)
	 *
	 */
	@Override
	public void match(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception {

		((InPortContentManagement) this.offering).match(cd, hops, facade,requestURI,matched);
	}

}
