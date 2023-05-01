package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Classe aui gere les addresses des ports entrants pour une facade
 */
public class ApplicationNodeAddress extends AbstractAddress implements ApplicationNodeAddressI {
	private String nodeManagementURI;

	/**
	 * Constructeur
	 * 
	 * @param managementURI
	 * @param nodeIdentifier
	 * @param contentManagementURI
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
