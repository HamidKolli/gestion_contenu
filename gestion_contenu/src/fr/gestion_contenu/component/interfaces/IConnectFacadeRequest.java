package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Contrat de connexion a une facade 
 */
public interface IConnectFacadeRequest {

	/**
	 * 
	 * Methode offerte pour les noeuds, elle est appelle quand un noeud veut joindre le reseau 
	 * @param a : les addresses des ports entrants du noeud
	 * @return Set<PeerNodeAddressI> l'essemble des addresses des ports entrants des noeuds proposes par la facade 
	 * @throws Exception
	 */
	public abstract Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception;

	/**
	 * 
	 * Methode offerte aux noeuds, elle permet de quitter le reseau
	 * @param a : les addresses des ports entrants du noeud
	 * @throws Exception
	 */
	public abstract void leave(PeerNodeAddressI a) throws Exception;
}
