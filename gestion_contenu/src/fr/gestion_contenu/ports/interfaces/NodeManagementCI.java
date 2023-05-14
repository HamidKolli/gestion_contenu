package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Contrat implante par les ports s'occupant de la connexion entre les
 *         noeuds (ports sortants) et les interfaces (ports entrants) de la
 *         facade
 */
public interface NodeManagementCI extends OfferedCI, RequiredCI,ProbingCI {
	/**
	 * 
	 * Methode offerte pour les noeuds, elle est appelle quand un noeud veut joindre le reseau 
	 * @param a : l'adresse du port entrant du noeud
	 * @throws Exception
	 */
	public abstract void join(PeerNodeAddressI a) throws Exception;

	/**
	 * 
	 * Methode offerte aux noeuds, elle permet de quitter le reseau
	 * @param a : l'adresse du port entrant du noeud
	 * @throws Exception
	 */
	public abstract void leave(PeerNodeAddressI a) throws Exception;
	
	
	/**
	 * Methode acceptProbed : Retour de resultat d'un probe
	 * 
	 * @param peer : le noeud pair
	 * @param requestURI : l'URI du noeud pair associé à la requete
	 */
	public void acceptProbed(PeerNodeAddressI peer, String requestURI) throws Exception;

}
