package fr.gestion_contenu.connectors;

import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortNode;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ConnectorNode extends AbstractConnector implements NodeCI{

	@Override
	public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
		
		return ((InPortNode)this.offering).connect(a);
	}

	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		((InPortNode)this.offering).disconnect(a);
		
	}

}
