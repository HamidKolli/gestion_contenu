package fr.gestion_contenu.ports;

import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Classe qui represente le port entrant d'un noeud pour les connexions
 *         entre noeuds
 */
public class InPortNode extends AbstractInboundPort implements NodeCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur InPortNode.java
	 * 
	 * @param uri   : l'URI du port
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public InPortNode(String uri, ComponentI owner) throws Exception {
		super(uri, NodeCI.class, owner);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectNodeRequest#connect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void connect(PeerNodeAddressI a) throws Exception {
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((NodeComponent) getOwner()).connectBack(a);
				return null;
			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectNodeRequest#disconnect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((NodeComponent) getOwner()).disconnectBack(a);
				return null;
			}
		});

	}

}
