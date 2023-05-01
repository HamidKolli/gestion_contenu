package fr.gestion_contenu.node.interfaces;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Super Interface de ApplicationNodeAddressI
 */
public interface FacadeNodeAddressI extends NodeAddressI{
	/**
	 * getter
	 * @return String : l'uri du port entrant
	 */
	public String getNodeManagementURI();
}
