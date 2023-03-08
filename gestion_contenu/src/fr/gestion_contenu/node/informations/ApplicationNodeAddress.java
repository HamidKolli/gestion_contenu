package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *        Classe aui gere les addresses des ports entrants pour une facade
 */
public class ApplicationNodeAddress implements ApplicationNodeAddressI{
	private String nodeManagementURI;
	private String nodeIdentifier;
	private String contentManagementURI;
	private boolean isFacade;
	private boolean isPeer;
	

	/**
	 * Constructeur
	 * 
	 * @param managementURI
	 * @param nodeIdentifier
	 * @param contentManagementURI
	 * @param isFacade
	 * @param isNode
	 */
	public ApplicationNodeAddress(String managementURI, String nodeIdentifier, String contentManagementURI,
			boolean isFacade, boolean isNode) {
		super();
		this.nodeManagementURI = managementURI;
		this.nodeIdentifier = nodeIdentifier;
		this.contentManagementURI = contentManagementURI;
		this.isFacade = isFacade;
		this.isPeer = isNode;
	}

	/**
	 *@see fr.gestion_contenu.node.interfaces.FacadeNodeAddressI#getNodeManagementURI()
	 */
	@Override
	public String getNodeManagementURI() {
		return nodeManagementURI;
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

}
