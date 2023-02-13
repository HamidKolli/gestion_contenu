package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

public class ContentDescriptor extends ContentTemplate implements ContentDescriptorI {

	private ContentNodeAddressI nodeAdress;
	private long size;

	/**
	 * 
	 * @param title
	 * @param albumTitle
	 * @param interpreters
	 * @param composers
	 * @param nodeAdress
	 * @param size
	 */
	public ContentDescriptor(String title, String albumTitle, Set<String> interpreters, Set<String> composers,
			ContentNodeAddressI nodeAdress, long size) {
		super(title, albumTitle, interpreters, composers);
		this.nodeAdress = nodeAdress;
		this.size = size;
	}

	@Override
	public ContentNodeAddressI getContentNodeAddress() {
		return nodeAdress;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public boolean equals(ContentDescriptorI cd) {
		return super.equals(cd) && getContentNodeAddress().equals(cd.getContentNodeAddress())
				&& getSize() == cd.getSize();
	}

	@Override
	public boolean match(ContentTemplateI t) {
		if (t == null)
			return false;
		boolean result = true;
		if (t.getTitle() != null)
			result = result && t.getTitle().equals(getTitle());
		if (t.getAlbumTitle() != null)
			result = result && t.getAlbumTitle().equals(getAlbumTitle());
		if (t.getComposers() != null)
			result = result && t.getComposers().equals(getComposers());
		if (t.getInterpreters() != null)
			result = result && t.getInterpreters().equals(getInterpreters());
		return result;
	}

}
