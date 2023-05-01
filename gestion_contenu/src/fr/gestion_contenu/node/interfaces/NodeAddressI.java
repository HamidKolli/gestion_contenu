package fr.gestion_contenu.node.interfaces;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Super Interface de FacadeNodeAddressI et PeerNodeAddressI
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
