package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;


/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Contrat pour retourner les resultats des requetes
 */
public interface IReturnResult {
	/**
	 * 
	 * Methode permettant d'afficher le descriptor retourn� par le find.
	 * @param descriptors : le descriptor retourn� par le find
	 * @throws Exception
	 */
	public void returnFind(ContentDescriptorI cd) throws Exception;
	/**
	 * Methode permettant d'afficher les differents descriptors qui correspondent au match.
	 * @param descriptors : les descriptors retourn�s par le match
	 * @throws Exception
	 */
	public void returnMatch(Set<ContentDescriptorI> cd) throws Exception;
}
