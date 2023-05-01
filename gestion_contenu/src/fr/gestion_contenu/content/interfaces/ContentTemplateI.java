package fr.gestion_contenu.content.interfaces;

import java.util.Set;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Contrat d'un ContentTemplate
 */
public interface ContentTemplateI {
	
	/**
	 * getter
	 * @return String : le titre de la musique
	 */
	public String getTitle();
	/**
	 * getter
	 * @return String : le titre de l'album
	 */
	public String getAlbumTitle();
	/**
	 * getter
	 * @return Set<String> : le ou les interpretes de cette musique
	 */
	public Set<String> getInterpreters();
	/**
	 * getter
	 * @return Set<String> : le ou les compositeurs de cette musique
	 */
	public Set<String> getComposers();
}
