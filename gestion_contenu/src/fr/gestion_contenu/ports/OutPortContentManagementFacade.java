package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 *         Classe qui represente le port d'une facade pour les
 *         requetes sur les contenus
 */
public class OutPortContentManagementFacade extends AbstractOutboundPort implements FacadeContentManagementCI {


	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 * 
	 * @param owner
	 * @throws Exception
	 */
	public OutPortContentManagementFacade(ComponentI owner) throws Exception {
		super(FacadeContentManagementCI.class, owner);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#find(ContentTemplateI, int)
	 *
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI template, int hops) throws Exception {
		return ((FacadeContentManagementCI) getConnector()).find(template, hops);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#match(ContentTemplateI, int, Set)
	 *
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template, int hops, Set<ContentDescriptorI> matched)
			throws Exception {
		return ((FacadeContentManagementCI) getConnector()).match(template, hops,matched);
	}

	
	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptFound(ContentDescriptorI, String)
	 *
	 */
	@Override
	public void acceptFound(ContentDescriptorI found, String requestURI) throws Exception {
		((FacadeContentManagementCI) getConnector()).acceptFound(found, requestURI);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptMatched(Set, String)
	 *
	 */
	@Override
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) throws Exception {
		((FacadeContentManagementCI) getConnector()).acceptMatched(matched, requestURI);
	}

}
