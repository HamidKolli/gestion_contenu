package fr.gestion_contenu.component.interfaces;

import java.util.Set;

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
public abstract class AbstractNodeComponent extends AbstractComponent implements IContentRequest,IConnectNodeRequest {

	protected AbstractNodeComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}


	
	/**
	 * Methode qui permet de ce connecter a en retour
	 * @param peer : les addresses des ports entrants du noeud qui veut ce connecter
	 * @throws Exception
	 */
	public abstract void connectBack(PeerNodeAddressI peer) throws Exception;

	
	
	/**
	 * Methode qui permet de ce deconnecter a en retour
	 * @param peer : les addresses des ports entrants du noeud qui veut ce deconnecter
	 * @throws Exception
	 */
	public abstract void disconnectBack(PeerNodeAddressI peer) throws Exception;

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
	public abstract Set<PeerNodeAddressI> join() throws Exception;
}
