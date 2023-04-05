package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.plugins.ContentManagementPlugin;
import fr.gestion_contenu.ports.interfaces.NodeCI;
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
	 * @param uriConnection 
	 * @throws Exception
	 */
	public InPortNode(String uri, ComponentI owner, String pluginURI, String uriConnection) throws Exception {
		super(uri, NodeCI.class, owner, pluginURI, uriConnection);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IConnectNodeRequest#connect(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	 *
	 */
	@Override
	public void connect(PeerNodeAddressI a) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),(q) -> {
			try {
				((ContentManagementPlugin) getOwnerPlugin(pluginURI)).connectBack(a);
			} catch (Exception e) {
				e.printStackTrace();
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
		getOwner().runTask(getExecutorServiceIndex(),(q) -> {
			try {
				((ContentManagementPlugin) getOwnerPlugin(pluginURI)).disconnectBack(a);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),(q) -> {
			try {
				((ContentManagementPlugin) getOwnerPlugin(pluginURI)).acceptNeighbours(neighbours);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}

	@Override
	public void acceptConnected(PeerNodeAddressI neighbour) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),(q) -> {
			try {
				((ContentManagementPlugin) getOwnerPlugin(pluginURI)).acceptConnected(neighbour);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}

	@Override
	public void probe(int remaingHops, FacadeNodeAddressI facade, String request) throws Exception {
		getOwner().runTask((q) -> {
			try {
				((ContentManagementPlugin) getOwnerPlugin(pluginURI)).probe( remaingHops,  facade,  request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}

}
