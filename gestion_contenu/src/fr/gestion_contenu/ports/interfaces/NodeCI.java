package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *			Contrat implante par les ports entrants et sortants qui s'occupent des 
 *			connexions entre les noeuds du reseau
 */
public interface NodeCI extends OfferedCI, RequiredCI, ProbingCI{
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
	
	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws Exception;
	public void acceptConnected(PeerNodeAddressI neighbour) throws Exception;
	
}
