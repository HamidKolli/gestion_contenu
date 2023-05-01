package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentTemplateI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Classe representant un ContentTemplate
 */
public class ContentTemplate implements ContentTemplateI {
	private String title;
	private String albumTitle;
	private Set<String> interpreters;
	private Set<String> composers;

	/**
	 * Constructeur d'un ContentTemplate
	 * 
	 * @param title
	 * @param albumTitle
	 * @param interpreters
	 * @param composers
	 */
	public ContentTemplate(String title, String albumTitle, Set<String> interpreters, Set<String> composers) {
		super();
		
		this.title = title;
		this.albumTitle = albumTitle;
		this.interpreters = interpreters;
		this.composers = composers;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentTemplateI#getTitle()
	 */
	@Override
	public String getTitle() {

		return title;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentTemplateI#getAlbumTitle()
	 */
	@Override
	public String getAlbumTitle() {

		return albumTitle;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentTemplateI#getInterpreters()
	 */
	@Override
	public Set<String> getInterpreters() {

		return interpreters;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentTemplateI#getComposers()
	 */
	@Override
	public Set<String> getComposers() {

		return composers;
	}

	
	
	
	

	/**
	 * Methode toString
	 * 
	 * @return String : l'affichage du contenu du ContentTemplate
	 */
	@Override
	public String toString() {
		return "\n Template >>>> \n titre : " + getTitle() + "\n Album : " + getAlbumTitle() + "\n Composers : "
				+ getComposers() + "\n Interpreters : " + getInterpreters();
	}

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ContentTemplate))
			return false;
		ContentTemplate other = (ContentTemplate) obj;
		if (albumTitle == null) {
			if (other.albumTitle != null)
				return false;
		} else if (!albumTitle.equals(other.albumTitle))
			return false;
		if (composers == null) {
			if (other.composers != null)
				return false;
		} else if (!composers.equals(other.composers))
			return false;
		if (interpreters == null) {
			if (other.interpreters != null)
				return false;
		} else if (!interpreters.equals(other.interpreters))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
