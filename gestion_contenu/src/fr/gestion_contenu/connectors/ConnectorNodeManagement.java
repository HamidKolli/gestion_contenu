package fr.gestion_contenu.connectors;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
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
	public void join(PeerNodeAddressI a) throws Exception{
		 ((NodeManagementCI)this.offering).join(a);
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception{
		((NodeManagementCI)this.offering).leave(a);
	}

	@Override
	public void acceptProbed(PeerNodeAddressI peer, String requestURI)throws Exception  {
		((NodeManagementCI)this.offering).acceptProbed(peer, requestURI);		
	}

	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request) throws Exception {
		((NodeManagementCI)this.offering).probe(remaingHops, facade, request);	
	}

}
