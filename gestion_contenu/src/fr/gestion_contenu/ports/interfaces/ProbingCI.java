package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;

public interface ProbingCI {
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request) throws Exception;
}
