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
	 * Methode equals
	 * 
	 * @param obj : l'objet a comparer
	 * @return boolean : le resultat de l'egalite
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (!(obj instanceof ContentTemplate))
			return false;

		ContentTemplate o = (ContentTemplate) obj;
		return o.getAlbumTitle().equals(getAlbumTitle()) && o.getTitle().equals(getTitle())
				&& o.getInterpreters().equals(getInterpreters()) && o.getComposers().equals(getComposers());

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

}
