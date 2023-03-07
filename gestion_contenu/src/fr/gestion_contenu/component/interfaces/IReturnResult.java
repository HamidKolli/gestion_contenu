package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;

public interface IReturnResult {
	public void returnFind(ContentDescriptorI cd) throws Exception;
	public void returnMatch(Set<ContentDescriptorI> cd) throws Exception;
}
