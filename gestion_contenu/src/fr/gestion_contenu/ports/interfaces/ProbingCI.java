package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 *
 */
public interface ProbingCI {
	
	/**
	 * Methode Probe : effectue le sondage
	 * 
	 * @param remaingHops : nb de sauts du probe
	 * @param facade : Adresse de la Façade effectuant le probe
	 * @param request : uri du noeud concerné par la requete
	 * @param nbVoisin : nb de voisins du noeud
	 * @param addressNode : adresse du noeud pair
	 * @throws Exception
	 */
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin,PeerNodeAddressI addressNode) throws Exception;
}
