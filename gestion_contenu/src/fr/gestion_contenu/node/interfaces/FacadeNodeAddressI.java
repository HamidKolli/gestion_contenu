package fr.gestion_contenu.node.interfaces;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Contrat pour un FacadeNodeAddress
 */
public interface FacadeNodeAddressI extends NodeAddressI{
	/**
	 * getter
	 * @return String : l'uri du port entrant
	 */
	public String getNodeManagementURI();
}
