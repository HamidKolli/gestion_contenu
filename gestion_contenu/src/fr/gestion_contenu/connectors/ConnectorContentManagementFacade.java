package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Connecteur des traitements de contenu entre noeud et facade
 */

public class ConnectorContentManagementFacade extends AbstractConnector implements FacadeContentManagementCI{

	
	/**
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#find(ContentTemplateI, int)
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI template, int hops) throws Exception {
		return ((FacadeContentManagementCI)this.offering).find(template, hops);
	}

	/**
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#match(ContentTemplateI, int, Set)
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template, int hops, Set<ContentDescriptorI> matched)
			throws Exception {
		return ((FacadeContentManagementCI)this.offering).match(template, hops, matched);
	}

	/**
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptFound(ContentDescriptorI, String)
	 */
	@Override
	public void acceptFound(ContentDescriptorI found, String requestURI) throws Exception {
		 ((FacadeContentManagementCI)this.offering).acceptFound(found, requestURI);
		
	}

	
	/**
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptMatched(Set, String)
	 */
	@Override
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) throws Exception {
		((FacadeContentManagementCI)this.offering).acceptMatched(matched, requestURI);
	}

}
