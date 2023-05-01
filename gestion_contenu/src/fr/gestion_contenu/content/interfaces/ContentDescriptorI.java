package fr.gestion_contenu.content.interfaces;

import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Contrat d'un ContentDescriptor
 */
public interface ContentDescriptorI extends ContentTemplateI{
	
	/**
	 * getter
	 * @return ContentNodeAddressI : le contentNodeAddress passe en parametre du constructeur
	 */
	public ContentNodeAddressI getContentNodeAddress();
	
	/**
	 * getter
	 * @return long : la taille du ContentDescriptor passe en parametre du constructeur
	 */
	public long getSize();
	
	/**
	 * Methode equals
	 *@param cd : le contentDescriptor a comparer
	 *@return boolean : le resultat de l'egalite
	 */
	public boolean equals(ContentDescriptorI cd);
	
	/**
	 * Methode qui verifie si le contentDescriptor courant match avec un template
	 * @param t : le template du contenu que l'on recherche
	 * @return boolean : le resultat du match
	 */
	public boolean match(ContentTemplateI t);
}


