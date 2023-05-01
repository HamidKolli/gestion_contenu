package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *        Classe aui gere les addresses des ports pour un noeud
 */
public class ContentNodeAddress extends AbstractAddress implements ContentNodeAddressI {

	private String nodeURI;


	/**
	 * 
	 * @param nodeURI
	 * @param nodeIdentifier
	 * @param contentManagementURI
	 */
	public ContentNodeAddress(String nodeURI, String nodeIdentifier, String contentManagementURI) {
		super(nodeIdentifier,contentManagementURI,false);
		assert nodeURI != null;
		this.nodeURI = nodeURI;

	}

	/**
	 *getter
	 *@return String
	 */
	@Override
	public String getNodeURI() {
		return nodeURI;
	}

	
	@Override
	public String toString() {
		return "ContentNodeAdress [nodeURI=" + getNodeURI() + ", nodeIdentifier=" + getNodeIdentifier()
				+ ", contentManagementURI=" + getContentManagementURI() + "]";
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ContentNodeAddress))
			return false;
		ContentNodeAddress other = (ContentNodeAddress) obj;
		if (nodeURI == null) {
			if (other.nodeURI != null)
				return false;
		} else if (!nodeURI.equals(other.nodeURI))
			return false;
		return true;
	}
	
	
	
	
	

}