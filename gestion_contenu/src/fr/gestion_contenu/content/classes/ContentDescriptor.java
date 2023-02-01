package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

public class ContentDescriptor implements ContentDescriptorI{

	private String title;
	private String albumTitle;
	private Set<String> interpreters;
	private Set<String> composers;
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
		super();
		this.title = title;
		this.albumTitle = albumTitle;
		this.interpreters = interpreters;
		this.composers = composers;
		this.nodeAdress = nodeAdress;
		this.size = size;
	}

	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public String getAlbumTitle() {
		// TODO Auto-generated method stub
		return albumTitle;
	}

	@Override
	public Set<String> getInterpreters() {
		// TODO Auto-generated method stub
		return interpreters;
	}

	@Override
	public Set<String> getComposers() {
		// TODO Auto-generated method stub
		return composers;
	}
	
	

	@Override
	public ContentNodeAddressI getContentNodeAddress() {
		// TODO Auto-generated method stub
		return nodeAdress;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean equals(ContentDescriptorI cd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean match(ContentDescriptorI t) {
		// TODO Auto-generated method stub
		return false;
	}

}
