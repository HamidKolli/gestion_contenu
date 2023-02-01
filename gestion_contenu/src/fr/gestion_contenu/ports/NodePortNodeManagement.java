package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NodePortNodeManagement extends AbstractOutboundPort implements NodeManagementCI {

	public NodePortNodeManagement( ComponentI owner)
			throws Exception {
		super(NodeManagementCI.class , owner);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {

		return ((ConnectorNodeManagement) getConnector()).join(a);
	}

	@Override
	public void leave(PeerNodeAddressI a) throws Exception {
		((ConnectorNodeManagement) getConnector()).leave(a);
	}

}
