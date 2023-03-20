package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;

public interface IContentRequestFacade {
	public ContentDescriptorI find(ContentTemplateI template,int hops) throws Exception;
	public Set<ContentDescriptorI> match(ContentTemplateI template,int hops,Set<ContentDescriptorI> matched)throws Exception;
}
