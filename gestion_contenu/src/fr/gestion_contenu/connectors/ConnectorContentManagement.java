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
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Connecteur des traitements de contenu
 */
public class ConnectorContentManagement extends AbstractConnector implements ContentManagementCI {

	/**
	 * 
	* @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#find(fr.gestion_contenu.content.interfaces.ContentTemplateI, int, fr.gestion_contenu.node.interfaces.NodeAddressI, java.lang.String)
	*
	 */
	@Override
	public void find(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI) throws Exception {
		((InPortContentManagement) this.offering).find(cd, hops, facade, requestURI);
	}

	/**
	 * 
	* @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#match(fr.gestion_contenu.content.interfaces.ContentTemplateI, int, fr.gestion_contenu.node.interfaces.NodeAddressI, java.lang.String, java.util.Set)
	*
	 */
	@Override
	public void match(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception {

		((InPortContentManagement) this.offering).match(cd, hops, facade,requestURI,matched);
	}

}
