package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ContentManagementNodeAddressI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;


/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Classe Abstraite gerant les adresses des ports
 */
public abstract class AbstractAddress implements NodeAddressI,ContentManagementNodeAddressI{
	private String nodeIdentifier;
	private String contentManagementURI;
	private boolean isFacade;
	private boolean isPeer;
	
	/**
	 * constructeur
	 * 
	 * @param nodeIdentifier : l'identifiant du noeud
	 * @param contentManagementURI : l'identifiant du port entrant de gestion des contenus
	 * @param isFacade : pour avoir si c'est une facade ou pas
	 */
	public AbstractAddress(String nodeIdentifier, String contentManagementURI, boolean isFacade) {
		super();
		assert nodeIdentifier != null;
		assert contentManagementURI != null;
		this.nodeIdentifier = nodeIdentifier;
		this.contentManagementURI = contentManagementURI;
		this.isFacade = isFacade;
		//Pour ne pas le calculer a chaque isPeer()
		this.isPeer = !isFacade;
	}
	
	
	/**
	 *@see fr.gestion_contenu.node.interfaces.NodeAddressI#getNodeIdentifier()
	 */
	@Override
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	/**
	 *@see fr.gestion_contenu.node.interfaces.NodeAddressI#isFacade()
	 */
	@Override
	public boolean isFacade() {
		return isFacade;
	}

	/**
	 *getter
	 *@see fr.gestion_contenu.node.interfaces.NodeAddressI#isPeer()
	 */
	@Override
	public boolean isPeer() {
		return isPeer;
	}

	/**
	 *getter
	 *@return String
	 */
	@Override
	public String getContentManagementURI() {
		return contentManagementURI;
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
		if (!(obj instanceof AbstractAddress))
			return false;
		AbstractAddress other = (AbstractAddress) obj;
		if (contentManagementURI == null) {
			if (other.contentManagementURI != null)
				return false;
		} else if (!contentManagementURI.equals(other.contentManagementURI))
			return false;
		if (isFacade != other.isFacade)
			return false;
		if (isPeer != other.isPeer)
			return false;
		if (nodeIdentifier == null) {
			if (other.nodeIdentifier != null)
				return false;
		} else if (!nodeIdentifier.equals(other.nodeIdentifier))
			return false;
		return true;
	}
	
	
	
}
