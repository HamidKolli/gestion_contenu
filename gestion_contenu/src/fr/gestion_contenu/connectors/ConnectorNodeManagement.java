package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ConnectorNodeManagement extends AbstractConnector implements NodeManagementCI{

	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception{
		return ((FacadePortNodeManagement)this.offering).join(a);
	}

	@Override
	public void leave(PeerNodeAddressI a) throws Exception{
		((FacadePortNodeManagement)this.offering).leave(a);
	}

}
