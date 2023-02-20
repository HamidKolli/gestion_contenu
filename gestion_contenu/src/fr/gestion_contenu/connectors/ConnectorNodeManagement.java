package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 * Connecteur qui gere la connection entre un noeud et une facade (reseau)
 */
public class ConnectorNodeManagement extends AbstractConnector implements NodeManagementCI{

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception{
		return ((FacadePortNodeManagement)this.offering).join(a);
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception{
		((FacadePortNodeManagement)this.offering).leave(a);
	}

}
