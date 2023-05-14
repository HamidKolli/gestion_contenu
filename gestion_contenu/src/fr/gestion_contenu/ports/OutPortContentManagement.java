package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 *         Classe qui represente le port sortant d'un noeud pour les
 *         requetes sur les contenus
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
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#find(ContentTemplateI, int, NodeAddressI, String)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops,NodeAddressI facade,String requestURI) throws Exception {
		 ((ContentManagementCI) getConnector()).find(cd, hops,facade,requestURI);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#match(ContentTemplateI, int, NodeAddressI, String, Set)
	 *
	 */
	@Override
	public void match(ContentTemplateI cd, int hops,NodeAddressI facade,String requestURI, Set<ContentDescriptorI> matched)
			throws Exception {
		 ((ContentManagementCI) getConnector()).match(cd, hops,facade,requestURI,matched);
	}

}
