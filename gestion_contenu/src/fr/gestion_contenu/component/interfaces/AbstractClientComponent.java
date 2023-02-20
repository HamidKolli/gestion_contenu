package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.AbstractComponent;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Classe abstraite qu'un client doit heriter pour effectuer des requettes
 */
public abstract class AbstractClientComponent extends AbstractComponent{

	
	/**
	 * 
	 * Constructeur AbstractClientComponent
	 * @param nbThreads 
	 * @param nbSchedulableThreads
	 */
	protected AbstractClientComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}
	
	/**
	 * 
	 * Methode qui permet a un client de lancer une requette find
	 * @param template : le template du contenu rechercher 
	 * @return une description du contenu
	 * @throws Exception
	 */
	public abstract ContentDescriptorI find(ContentTemplateI template) throws Exception;
	
	/**
	 * 
	 * Methode qui permet a un client de lancer une requette match
	 * @param template : le template du contenu rechercher 
	 * @return l'essemble des descriptions du contenu
	 * @throws Exception
	 */
	public abstract Set<ContentDescriptorI> match(ContentTemplateI template) throws Exception;

}
