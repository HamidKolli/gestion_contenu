package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ContentManagementClientCI extends OfferedCI,RequiredCI {
	public ContentDescriptorI find(ContentTemplateI template,int hops) throws Exception;
	public Set<ContentDescriptorI> match(ContentTemplateI template,int hops,Set<ContentDescriptorI> matched)throws Exception;

}
