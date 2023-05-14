package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 */
public interface FacadeContentManagementCI extends ContentManagementClientCI{
	
	/**
	 * Methode de retour du find
	 * 
	 * @param found : le descriptor trouvé par le find
	 * @param requestURI : l'uri associé a la requete emise
	 * 
	 * @throws Exception
	 */
	public void acceptFound(ContentDescriptorI found,String requestURI) throws Exception;
	
	/**
	 * Methode de retour du match
	 * 
	 * @param matched : l'ensemble des descriptors trouvés par le match
	 * @param requestURI : l'uri associé a la requete emise
	 * 
	 * @throws Exception
	 */
	public void acceptMatched(Set<ContentDescriptorI> matched,String requestURI) throws Exception;
}
