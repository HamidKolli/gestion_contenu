package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD 
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
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {

		return ((ConnectorNodeManagement) getConnector()).join(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception {
		((ConnectorNodeManagement) getConnector()).leave(a);
	}

}
