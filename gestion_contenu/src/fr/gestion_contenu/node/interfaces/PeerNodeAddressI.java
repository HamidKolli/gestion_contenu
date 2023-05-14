package fr.gestion_contenu.node.interfaces;


/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Contrat pour un PeerNodeAdress
 */
public interface PeerNodeAddressI extends NodeAddressI{
	/**
	 * getter
	 * @return String
	 */
	public String getNodeURI();
}
