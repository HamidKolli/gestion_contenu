package fr.gestion_contenu.ports;

import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Classe qui represente le port sortant d'un noeud pour les connexions
 *         entre noeuds
 */
public class OutPortNode extends AbstractOutboundPort implements NodeCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur OutPortNode.java
	 * 
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public OutPortNode(ComponentI owner) throws Exception {
		super(NodeCI.class, owner);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectNodeRequest#connect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void connect(PeerNodeAddressI a) throws Exception {
		((ConnectorNode) getConnector()).connect(a);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectNodeRequest#disconnect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		((ConnectorNode) getConnector()).disconnect(a);
	}

}
