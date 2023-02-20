package fr.gestion_contenu.component.interfaces;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Contrat de connexion entre les noeud
 */
public interface IConnectNodeRequest {
	/**
	 * Methode qui permet de ce connecter a un noeud
	 * @param peer : les addresses des ports entrants du noeud
	 * @throws Exception
	 */
	public void connect(PeerNodeAddressI peer) throws Exception;
	
	/**
	 * Methode qui permet de ce deconnecter a un noeud
	 * @param peer : les addresses des ports entrants du noeud 
	 * @throws Exception
	 */
	public void disconnect(PeerNodeAddressI peer) throws Exception;
}
