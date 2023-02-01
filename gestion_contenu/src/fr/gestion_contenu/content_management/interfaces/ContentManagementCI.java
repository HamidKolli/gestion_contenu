package fr.gestion_contenu.content_management.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ContentManagementCI extends OfferedCI,RequiredCI{
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception;
	public Set<ContentDescriptorI> match(ContentTemplateI cd,Set<ContentDescriptorI> matched,int hops) throws Exception;
}
