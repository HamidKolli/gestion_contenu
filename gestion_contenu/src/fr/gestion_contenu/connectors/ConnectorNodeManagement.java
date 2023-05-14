package fr.gestion_contenu.connectors;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Connecteur qui gere la connection entre un noeud et une facade (reseau)
 */
public class ConnectorNodeManagement extends AbstractConnector implements NodeManagementCI{

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#join(PeerNodeAddressI)
	 *
	 */
	@Override
	public void join(PeerNodeAddressI a) throws Exception{
		 ((NodeManagementCI)this.offering).join(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#leave(PeerNodeAddressI)
	 *
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception{
		((NodeManagementCI)this.offering).leave(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#acceptProbed(PeerNodeAddressI, String)
	 *
	 */
	@Override
	public void acceptProbed(PeerNodeAddressI peer, String requestURI)throws Exception  {
		((NodeManagementCI)this.offering).acceptProbed(peer, requestURI);		
	}
	/**
	 * @see fr.gestion_contenu.component.FacadeComponent#probe(int, FacadeNodeAddressI, String, int, PeerNodeAddressI)
	 */
	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin, PeerNodeAddressI addressNode) throws Exception {
		((NodeManagementCI)this.offering).probe(remaingHops, facade, request,nbVoisin,addressNode);	
	}

}
