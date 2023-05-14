package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Classe concrete qui gere les addresses des ports entrants pour une facade
 */
public class ApplicationNodeAddress extends AbstractAddress implements ApplicationNodeAddressI {
	private String nodeManagementURI;

	/**
	 * Constructeur
	 * 
	 * @param managementURI : l'uri du port entrant de node management
	 * @param nodeIdentifier : identifiant du noeud
	 * @param contentManagementURI : l'uri du port entrant de content management
	 */
	public ApplicationNodeAddress(String managementURI, String nodeIdentifier, String contentManagementURI) {
		super(nodeIdentifier, contentManagementURI, true);
		assert managementURI != null;
		this.nodeManagementURI = managementURI;

	}

	/**
	 * @see fr.gestion_contenu.node.interfaces.FacadeNodeAddressI#getNodeManagementURI()
	 */
	@Override
	public String getNodeManagementURI() {
		return nodeManagementURI;
	}


	/**
	 *methode equals
	 *
	 *@param obj : l'objet a comparer
	 *@return boolean : resultat de l'equivalence
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ApplicationNodeAddress))
			return false;
		ApplicationNodeAddress other = (ApplicationNodeAddress) obj;
		if (nodeManagementURI == null) {
			if (other.nodeManagementURI != null)
				return false;
		} else if (!nodeManagementURI.equals(other.nodeManagementURI))
			return false;
		return true;
	}
	
	
	

}
