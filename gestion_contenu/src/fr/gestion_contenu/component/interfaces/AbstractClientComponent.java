package fr.gestion_contenu.component.interfaces;

import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.AbstractComponent;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Classe abstraite qu'un client doit heriter pour effectuer des requetes
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
	 * Methode qui permet a un client de lancer une requete find
	 * @param template : le template du contenu rechercher 
	 * @throws Exception : exceptions liees au publications et connexions des ports
	 */
	public abstract void find(ContentTemplateI template) throws Exception;
	
	/**
	 * 
	 * Methode qui permet a un client de lancer une requete match
	 * @param template : le template du contenu rechercher 
	 * @throws Exception : exceptions liees au publications et connexions des ports
	 */
	public abstract void match(ContentTemplateI template) throws Exception;
	

}
