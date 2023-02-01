package fr.gestion_contenu.node.informations;

import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;

public class ApplicationNodeAddress implements ApplicationNodeAddressI{
	private String nodeManagementURI;
	private String nodeIdentifier;
	private String contentManagementURI;
	private boolean isFacade;
	private boolean isPeer;
	

	public ApplicationNodeAddress(String managementURI, String nodeIdentifier, String contentManagementURI,
			boolean isFacade, boolean isNode) {
		super();
		this.nodeManagementURI = managementURI;
		this.nodeIdentifier = nodeIdentifier;
		this.contentManagementURI = contentManagementURI;
		this.isFacade = isFacade;
		this.isPeer = isNode;
	}

	@Override
	public String getNodeManagementURI() {
		// TODO Auto-generated method stub
		return nodeManagementURI;
	}

	@Override
	public String getNodeIdentifier() {
		// TODO Auto-generated method stub
		return nodeIdentifier;
	}

	@Override
	public boolean isFacade() {
		// TODO Auto-generated method stub
		return isFacade;
	}

	@Override
	public boolean isPeer() {
		// TODO Auto-generated method stub
		return isPeer;
	}

	@Override
	public String getContentManagementURI() {
		// TODO Auto-generated method stub
		return contentManagementURI;
	}

}
