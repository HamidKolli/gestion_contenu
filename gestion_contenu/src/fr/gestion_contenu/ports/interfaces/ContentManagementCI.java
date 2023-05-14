package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;


/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *			Contrat implante par les ports de ContentManagement entrants et sortants
 */
public interface ContentManagementCI extends OfferedCI,RequiredCI{

	/**
	 * Methode qui permet de rechercher un contenu
	 * 
	 * @param cd   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * @param requester : L'adresse du port entrant du noeud facade ayant effectue la requete afin de retourner le resultat
	 * @param requestURI : son URI
	 * @throws Exception
	 */
	public void find(ContentTemplateI cd, int hops, NodeAddressI requester, String requestURI) throws Exception;

	/**
	 * Methode qui permet de rechercher des contenus similaire a un template
	 * 
	 * @param cd      : le template du contenu qu'on cherche
	 * @param hops    : le nombre de pas pour eviter les appels infinis
	 * @param requester : L'adresse du port entrant du noeud facade ayant effectue la requete afin de retourner le resultat
	 * @param requestURI : son URI
	 * @param matched : l'ensemble des contenus deja trouves
	 * 
	 * @throws Exception
	 */
	public void match(ContentTemplateI cd, int hops, NodeAddressI requester, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception;

}
