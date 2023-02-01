package fr.gestion_contenu.ports;

import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OutPortNode extends AbstractOutboundPort implements NodeCI {

	private static final long serialVersionUID = 1L;

	public OutPortNode(ComponentI owner) throws Exception {
		super(NodeCI.class, owner);
	}

	@Override
	public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
		return ((ConnectorNode) getConnector()).connect(a);
	}

	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		((ConnectorNode) getConnector()).disconnect(a);
	}

}
