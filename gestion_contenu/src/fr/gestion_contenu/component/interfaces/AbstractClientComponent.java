package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.sorbonne_u.components.AbstractComponent;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
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
	 * @return une description du contenu
	 * @throws Exception
	 */
	public abstract void find(ContentTemplateI template,String uriReturn) throws Exception;
	
	/**
	 * 
	 * Methode qui permet a un client de lancer une requete match
	 * @param template : le template du contenu rechercher 
	 * @return l'essemble des descriptions du contenu
	 * @throws Exception
	 */
	public abstract void match(ContentTemplateI template,String uriReturn) throws Exception;
	
	/**
	 * 
	 * Methode permettant d'afficher les differents descriptors qui correspondent au match.
	 * @param descriptors : les descriptors retournés par le match
	 * @throws Exception
	 */
	public abstract void returnMatch(Set<ContentDescriptorI> descriptors )  throws Exception;
	
	/**
	 * 
	 * Methode permettant d'afficher le descriptor retourné par le find.
	 * @param descriptors : le descriptor retourné par le find
	 * @throws Exception
	 */
	public abstract void returnFind(ContentDescriptorI descriptors )  throws Exception;
}
