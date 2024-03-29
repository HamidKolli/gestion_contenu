package fr.gestion_contenu.ports;

import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Classe qui represente le port entrant
 * d'une facade pour une connexion au reseau
 */
public class FacadePortNodeManagement extends AbstractInboundPort implements NodeManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur FacadePortNodeManagement.java
	 * 
	 * @param uri : l'URI du port
	 * @param owner : le composant qui possede le port
	 * @param contentManagementURI : l'uri du service executor
	 * @throws Exception
	 */
	public FacadePortNodeManagement(String uri, ComponentI owner, String contentManagementURI) throws Exception {
		super(uri, NodeManagementCI.class, owner, null, contentManagementURI);

	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#join(PeerNodeAddressI)
	 *
	 */
	@Override
	public void join(PeerNodeAddressI a) throws Exception {
		 getOwner()
				.runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						try {
							((FacadeComponent) getTaskOwner()).join(a);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#leave(PeerNodeAddressI)
	 *
	 */
	@Override
	public void leave(PeerNodeAddressI a) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((FacadeComponent) getTaskOwner()).leave(a);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.NodeManagementCI#acceptProbed(PeerNodeAddressI, String)
	 *
	 */
	@Override
	public void acceptProbed(PeerNodeAddressI peer, String requestURI) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((FacadeComponent) getTaskOwner()).acceptProbed(peer, requestURI);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	
	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ProbingCI#probe(int, FacadeNodeAddressI, String, int, PeerNodeAddressI)
	 *
	 */
	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request,int nbVoisin,PeerNodeAddressI addressNode) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((FacadeComponent) getTaskOwner()).probe(remaingHops,facade,request,nbVoisin,addressNode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
