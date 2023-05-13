package fr.gestion_contenu.component.interfaces;

import java.util.List;
import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;


@RequiredInterfaces(required = { NodeCI.class, ContentManagementCI.class, NodeManagementCI.class })
@OfferedInterfaces(offered = { ContentManagementCI.class, NodeCI.class })
/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe abstraite qui gere les composant de noeud 
 *
 */
public abstract class AbstractNodeComponent extends AbstractComponent  {

	protected AbstractNodeComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}

	/**
	 * 
	 * Methode qui fait un match entre le template et le descripteur du noeud
	 * @param template 
	 * @return le descripteur si ca match ,null sinon
	 */
	public abstract List<ContentDescriptorI> match(ContentTemplateI template);
	

	/**
	 * Methode qui permet de quitter le reseau
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#leave()
	* @throws Exception
	 */
	public abstract void leave() throws Exception;
	
		
	/**
	* Methode qui permet de joindre un reseau
	* @throws Exception
	*/
	public abstract void join() throws Exception;

	/**
	 * Methode de retour de resultat du join 
	 * @param neighbours
	 */
	public abstract void acceptNeighbours(Set<PeerNodeAddressI> neighbours);
}
