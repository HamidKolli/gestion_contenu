package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;

public interface ProbingCI {
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin,PeerNodeAddressI addressNode) throws Exception;
}
