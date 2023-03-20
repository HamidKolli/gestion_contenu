package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Contrat pour effectuer des requetes de contenu
 */
public interface IContentRequest {

	/**
	 * Methode qui permet de rechercher un contenu
	 * 
	 * @param cd   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * @throws Exception
	 */
	public void find(ContentTemplateI cd, int hops, NodeAddressI requester, String requestURI) throws Exception;

	/**
	 * Methode qui permet de rechercher des contenus similaire a un template
	 * 
	 * @param cd      : le template
	 * @param matched : l'ensemble des contenus deja trouves
	 * @param hops    : le nombre de pas pour eviter les appels infinis
	 * @throws Exception
	 */
	public void match(ContentTemplateI cd, int hops, NodeAddressI requester, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception;

}
