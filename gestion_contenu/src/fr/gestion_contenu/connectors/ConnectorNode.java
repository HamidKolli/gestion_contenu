package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 * Connecteur pour la gestion des connections entre les noeuds
 */
public class ConnectorNode extends AbstractConnector implements NodeCI{

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeCI#connect(PeerNodeAddressI)
	 *
	 */
	@Override
	public void connect(PeerNodeAddressI a) throws Exception {
		
		((NodeCI)this.offering).connect(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeCI#disconnect(PeerNodeAddressI)
	 *
	 */
	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		((NodeCI)this.offering).disconnect(a);
		
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeCI#acceptNeighbours(Set)
	 *
	 */
	@Override
	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws Exception {
		((NodeCI)this.offering).acceptNeighbours(neighbours);
		
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeCI#acceptConnected(PeerNodeAddressI)
	 *
	 */
	@Override
	public void acceptConnected(PeerNodeAddressI neighbour) throws Exception {
		((NodeCI)this.offering).acceptConnected(neighbour);
		
	}

	/**
	 * @see fr.gestion_contenu.component.FacadeComponent#probe(int, FacadeNodeAddressI, String, int, PeerNodeAddressI)
	 */
	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin, PeerNodeAddressI addressNode) throws Exception {
		((NodeCI)this.offering).probe(remaingHops, facade, request,nbVoisin,addressNode);
		
	}

}
