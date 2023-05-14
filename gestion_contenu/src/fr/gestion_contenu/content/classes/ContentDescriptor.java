package fr.gestion_contenu.content.classes;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *        Classe representant un ContentDescriptor
 */
public class ContentDescriptor extends ContentTemplate implements ContentDescriptorI {

	private ContentNodeAddressI nodeAdress;
	private long size;

	/**
	 * Constructeur de ContentDescriptor
	 * 
	 * @param title : le titre de la chanson
	 * @param albumTitle : l'album de la chanson
	 * @param interpreters : les interpreters de la chanson
	 * @param composers : les compositeurs de la chanson
	 * @param nodeAdress : les adresses du noeud ou est stoquee la chanson 
	 * @param size : la taille du fichier de la chanson
	 */
	public ContentDescriptor(String title, String albumTitle, Set<String> interpreters, Set<String> composers,
			ContentNodeAddressI nodeAdress, long size) {
		super(title, albumTitle, interpreters, composers);
		
		assert nodeAdress != null;
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
		
		if (t.getComposers() != null) {
			for (String com : t.getComposers()) {
				result &= getComposers().contains(com);
				
			}
		}
		if (t.getInterpreters() != null)
			for (String inter : t.getInterpreters()) {
				result &= getInterpreters().contains(inter);
			}
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
