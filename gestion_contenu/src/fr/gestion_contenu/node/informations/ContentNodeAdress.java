package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *        Classe aui gere les addresses des ports pour un noeud
 */
public class ContentNodeAdress implements ContentNodeAddressI {

	private String nodeURI;
	private String nodeIdentifier;
	private String contentManagementURI;
	private boolean isFacade;
	private boolean isPeer;

	/**
	 * 
	 * @param nodeURI
	 * @param nodeIdentifier
	 * @param contentManagementURI
	 * @param isFacade
	 * @param isPeer
	 */
	public ContentNodeAdress(String nodeURI, String nodeIdentifier, String contentManagementURI, boolean isFacade,
			boolean isPeer) {
		super();
		this.nodeURI = nodeURI;
		this.nodeIdentifier = nodeIdentifier;
		this.contentManagementURI = contentManagementURI;
		this.isFacade = isFacade;
		this.isPeer = isPeer;
	}

	
	/**
	 *getter
	 *@return String
	 */
	@Override
	public String getNodeURI() {
		return nodeURI;
	}

	
	/**
	 *getter
	 *@return String
	 */
	@Override
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	/**
	 *getter
	 *@return boolean
	 */
	@Override
	public boolean isFacade() {
		return isFacade;
	}

	/**
	 *getter
	 *@return boolean
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

}
