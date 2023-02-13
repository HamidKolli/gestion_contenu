package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentTemplateI;

public class ContentTemplate implements ContentTemplateI {
	private String title;
	private String albumTitle;
	private Set<String> interpreters;
	private Set<String> composers;

	protected ContentTemplate(String title, String albumTitle, Set<String> interpreters, Set<String> composers) {
		super();
		this.title = title;
		this.albumTitle = albumTitle;
		this.interpreters = interpreters;
		this.composers = composers;
	}

	@Override
	public String getTitle() {

		return title;
	}

	@Override
	public String getAlbumTitle() {

		return albumTitle;
	}

	@Override
	public Set<String> getInterpreters() {

		return interpreters;
	}

	@Override
	public Set<String> getComposers() {

		return composers;
	}

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

}
