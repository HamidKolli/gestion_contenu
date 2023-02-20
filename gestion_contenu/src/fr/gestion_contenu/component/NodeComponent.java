package fr.gestion_contenu.component;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.plugins.ContentManagementPlugin;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { NodeManagementCI.class})

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe conctrete qui gere les composant de noeud 
 *
 */
public class NodeComponent extends AbstractNodeComponent {
	
	private ContentDescriptorI contentDescriptorI;
	private NodePortNodeManagement portNodeManagement;
	private ContentManagementPlugin plugin ;
	private String portFacadeManagementURI;

	/**
	 * 
	 * Constructeur NodeComponent 
	 * @param contentDescriptorI : le descripteur du noeud
	 * @param portFacadeManagementURI : l'uri du port entrant d'une facade (pour demander de joindre le reseau)
	 * @throws Exception
	 */
	protected NodeComponent(ContentDescriptorI contentDescriptorI, String portFacadeManagementURI) throws Exception {
		super(1, 0);
		this.contentDescriptorI = contentDescriptorI;
		this.portFacadeManagementURI = portFacadeManagementURI;
	}
	
	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {			
			this.portNodeManagement = new NodePortNodeManagement(this);
			this.portNodeManagement.publishPort();
			plugin = new ContentManagementPlugin(contentDescriptorI.getContentNodeAddress());
			plugin.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(plugin);
			super.start();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#join()
	*
	 */
	@Override
	public Set<PeerNodeAddressI> join() throws Exception {
		doPortConnection(this.portNodeManagement.getPortURI(), this.portFacadeManagementURI,
				ConnectorNodeManagement.class.getCanonicalName());
		Set<PeerNodeAddressI> peersVoisins = portNodeManagement.join(contentDescriptorI.getContentNodeAddress());
		return peersVoisins;
	}
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	*
	 */
	@Override
	public ContentDescriptorI match(ContentTemplateI template) {
		return (contentDescriptorI.match(template) ? contentDescriptorI : null);
	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#execute()
	*
	 */
	@Override
	public void execute() throws Exception {
		
		Set<PeerNodeAddressI> peersVoisins = join();
		System.out.println("re " + contentDescriptorI.getContentNodeAddress().getNodeIdentifier());
		for (PeerNodeAddressI peerNodeAddressI : peersVoisins) {
			if (!peerNodeAddressI.equals(contentDescriptorI.getContentNodeAddress())) {
				plugin.connect(peerNodeAddressI);
			}

		}
		super.execute();
	}
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#leave()
	*
	 */
	@Override
	public void leave() throws Exception {
		portNodeManagement.leave(contentDescriptorI.getContentNodeAddress());
		plugin.finalise();
		plugin.uninstall();
	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#finalise()
	*
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.portNodeManagement.doDisconnection();
		super.finalise();
	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	*
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			portNodeManagement.unpublishPort();
			portNodeManagement.destroyPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	

}
