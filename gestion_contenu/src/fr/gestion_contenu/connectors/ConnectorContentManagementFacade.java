package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Connecteur des traitements de contenu entre noeud et facade
 */

public class ConnectorContentManagementFacade extends AbstractConnector implements FacadeContentManagementCI{

	@Override
	public ContentDescriptorI find(ContentTemplateI template, int hops) throws Exception {
		return ((FacadeContentManagementCI)this.offering).find(template, hops);
	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template, int hops, Set<ContentDescriptorI> matched)
			throws Exception {
		return ((FacadeContentManagementCI)this.offering).match(template, hops, matched);
	}

	@Override
	public void acceptFound(ContentDescriptorI found, String requestURI) throws Exception {
		 ((FacadeContentManagementCI)this.offering).acceptFound(found, requestURI);
		
	}

	@Override
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) throws Exception {
		((FacadeContentManagementCI)this.offering).acceptMatched(matched, requestURI);
	}

}
