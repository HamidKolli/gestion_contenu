package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *        Classe representant un ContentDescriptor
 */
public class ContentDescriptor extends ContentTemplate implements ContentDescriptorI {

	private ContentNodeAddressI nodeAdress;
	private long size;

	/**
	 * Constructeur de ContentDescriptor
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
	

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentDescriptorI#getContentNodeAddress()
	 */
	@Override
	public ContentNodeAddressI getContentNodeAddress() {
		return nodeAdress;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentDescriptorI#getSize()
	 */
	@Override
	public long getSize() {
		return size;
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentDescriptorI#equals(ContentDescriptorI)
	 */
	@Override
	public boolean equals(ContentDescriptorI cd) {
		return super.equals(cd) && getContentNodeAddress().equals(cd.getContentNodeAddress())
				&& getSize() == cd.getSize();
	}

	/**
	 * @see fr.gestion_contenu.content.interfaces.ContentDescriptorI#match(ContentTemplateI)
	 */
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
	
	/**
	 * Methode toString
	 *@return String : l'affichage du contenu du ContentDescriptor
	 */
	@Override
	public String toString() {
		return "\nDescriptor >>>> "+ super.toString() + "\n Node Address : " + getContentNodeAddress().getContentManagementURI();
	}

}
