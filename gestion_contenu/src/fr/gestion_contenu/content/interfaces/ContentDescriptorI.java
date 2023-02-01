package fr.gestion_contenu.content.interfaces;

import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

public interface ContentDescriptorI extends ContentTemplateI{
	public ContentNodeAddressI getContentNodeAddress();
	public long getSize();
	public boolean equals(ContentDescriptorI cd);
	public boolean match(ContentDescriptorI t);
}


