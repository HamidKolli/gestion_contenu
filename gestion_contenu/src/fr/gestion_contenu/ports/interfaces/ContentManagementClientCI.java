package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Interface pour les operations de match et find cote client
 */
public interface ContentManagementClientCI extends OfferedCI,RequiredCI {
	
	/**
	 * Methode qui permet de rechercher un contenu
	 * 
	 * @param template   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * 
	 * @throws Exception
	 * 
	 * @return le contenu si elle le trouve ou null sinon
	 */
	public ContentDescriptorI find(ContentTemplateI template,int hops) throws Exception;
	
	/**
	 * Methode qui permet de rechercher des contenus similaires a un template
	 * 
	 * @param template   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * @param matched : les descriptors qui matchent jusqu'a present
	 * 
	 * @return Set(ContentDescriptorI) l'ensemble des contenus qui matchent
	 * 
	 * @throws Exception
	 */
	public Set<ContentDescriptorI> match(ContentTemplateI template,int hops,Set<ContentDescriptorI> matched)throws Exception;

}
