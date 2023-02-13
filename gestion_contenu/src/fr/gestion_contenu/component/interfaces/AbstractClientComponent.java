package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.classes.ContentTemplate;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.AbstractComponent;

public abstract class AbstractClientComponent extends AbstractComponent{

	
	
	protected AbstractClientComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}
	
	public abstract ContentDescriptorI find(ContentTemplateI template) throws Exception;
	public abstract Set<ContentDescriptorI> match(ContentTemplateI template) throws Exception;

}
