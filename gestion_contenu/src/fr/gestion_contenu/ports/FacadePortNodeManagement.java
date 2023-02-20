package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD 
 * Classe qui represente le port entrant
 * d'une facade pour une connexion au reseau
 */
public class FacadePortNodeManagement extends AbstractInboundPort implements NodeManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur FacadePortNodeManagement
	 * 
	 * @param uri   : l'URI du port
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public FacadePortNodeManagement(String uri, ComponentI owner) throws Exception {
		super(uri, NodeManagementCI.class, owner);

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {

		return getOwner().handleRequest(new AbstractComponent.AbstractService<Set<PeerNodeAddressI>>() {
			@Override
			public Set<PeerNodeAddressI> call() throws Exception {
				return ((FacadeComponent) getOwner()).join(a);
			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception {
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((FacadeComponent) getOwner()).leave(a);
				return null;
			}
		});
	}

}
