package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Classe qui represente le port sortant d'un noeud/facade pour les
 *         requettes sur les contenus
 */
public class OutPortContentManagement extends AbstractOutboundPort implements ContentManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur OutPortContentManagement.java
	 * 
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public OutPortContentManagement(ComponentI owner) throws Exception {
		super(ContentManagementCI.class, owner);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      int)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops,String uriReturn) throws Exception {
		 ((ConnectorContentManagement) getConnector()).find(cd, hops,uriReturn);
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
		 ((ConnectorContentManagement) getConnector()).match(cd, matched, hops,uriReturn);
	}

}
