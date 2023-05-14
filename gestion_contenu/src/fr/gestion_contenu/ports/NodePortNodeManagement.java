package fr.gestion_contenu.ports;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * 		   Classe qui represente le port sorant
 *         d'un noeud pour une connexion au reseau
 */
public class NodePortNodeManagement extends AbstractOutboundPort implements NodeManagementCI {

	/**
	 * 
	 * Constructeur NodePortNodeManagement.java
	 * 
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public NodePortNodeManagement(ComponentI owner) throws Exception {
		super(NodeManagementCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#join(PeerNodeAddressI)
	 *
	 */
	@Override
	public void join(PeerNodeAddressI a) throws Exception {
		((NodeManagementCI) getConnector()).join(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#leave(PeerNodeAddressI)
	 *
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception {
		((NodeManagementCI) getConnector()).leave(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#acceptProbed(PeerNodeAddressI, String)
	 *
	 */
	@Override
	public void acceptProbed(PeerNodeAddressI peer, String requestURI) throws Exception {
		((NodeManagementCI) getConnector()).acceptProbed(peer, requestURI);
		
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ProbingCI#probe(int, FacadeNodeAddressI, String, int, PeerNodeAddressI)
	 *
	 */
	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin, PeerNodeAddressI addressNode) throws Exception {
		((NodeManagementCI) getConnector()).probe(remaingHops, facade, request,nbVoisin,addressNode);
		
	}

}
