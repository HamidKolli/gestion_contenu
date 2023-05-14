package fr.gestion_contenu.node.interfaces;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Contrat pour un NodeAddress
 */
public interface NodeAddressI {
	/**
	 * getter
	 * @return String
	 */
	public String getNodeIdentifier();
	
	/**
	 *getter
	 *@return boolean
	 */
	public boolean isFacade();
	
	/**
	 *getter
	 *@return boolean
	 */
	public boolean isPeer();
}
