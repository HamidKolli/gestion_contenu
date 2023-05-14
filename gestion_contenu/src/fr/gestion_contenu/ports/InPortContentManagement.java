package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.plugins.ContentManagementPlugin;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 *         Classe qui represente le port entrant d'un noeud/facade pour les
 *         requetes sur les contenus
 */
public class InPortContentManagement extends AbstractInboundPort implements ContentManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur InPortContentManagement
	 * 
	 * @param uri   : l'URI du port
	 * @param owner : le composant qui le possede
	 * @param pluginURI : l'uri du plugin
	 * @param uriContentManagement 
	 * @throws Exception
	 */
	public InPortContentManagement(String uri, ComponentI owner, String pluginURI, String uriContentManagement) throws Exception {
		super(uri, ContentManagementCI.class, owner, pluginURI, uriContentManagement);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#find(ContentTemplateI, int, NodeAddressI, String)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask(pluginURI) {

			@Override
			public void run() {
				try {
					((ContentManagementPlugin) getTaskProviderReference()).find(cd, hops, facade, requestURI);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementCI#match(ContentTemplateI, int, NodeAddressI, String, Set)
	 *
	 */
	@Override
	public void match(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask(pluginURI) {

			@Override
			public void run() {
				try {
					((ContentManagementPlugin) getTaskProviderReference()).match(cd, hops, facade, requestURI, matched);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

}
