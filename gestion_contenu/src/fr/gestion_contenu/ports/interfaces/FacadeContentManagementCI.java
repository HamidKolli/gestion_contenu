package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;

public interface FacadeContentManagementCI extends ContentManagementClientCI{
	public void acceptFound(ContentDescriptorI found,String requestURI) throws Exception;
	public void acceptMatched(Set<ContentDescriptorI> matched,String requestURI) throws Exception;
}
