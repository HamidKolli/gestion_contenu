package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.plugins.FacadeContentManagementPlugin;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Classe qui represente le port entrant d'un client pour recuperer les
 *         resultats des requetes find et match
 */
public class InPortContentManagementFacade extends AbstractInboundPort implements FacadeContentManagementCI {



	private static final long serialVersionUID = 1L;

	
	/**
	 * constructeur
	 * 
	 * @param uri : l'uri du port
	 * @param owner : le composant qui le possede
	 * @param pluginURI : l'uri du plugin
	 * @param contentManagementURI : l'uri du service executor
	 * @throws Exception
	 */
	public InPortContentManagementFacade( String uri,ComponentI owner,
			String pluginURI, String contentManagementURI) throws Exception {
		super(uri,FacadeContentManagementCI.class, owner, pluginURI, contentManagementURI);
	}


	
	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptFound(ContentDescriptorI, String)
	 *
	 */
	@Override
	public void acceptFound(ContentDescriptorI found, String requestURI) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask(getPluginURI()) {

			@Override
			public void run() {
				try {
					((FacadeContentManagementPlugin) getTaskProviderReference()).acceptFound(found, requestURI);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI#acceptMatched(Set, String)
	 *
	 */
	@Override
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) throws Exception {
		getOwner().runTask(getExecutorServiceIndex(),new AbstractComponent.AbstractTask(getPluginURI()) {

			@Override
			public void run() {
				try {
					((FacadeContentManagementPlugin) getTaskProviderReference()).acceptMatched(matched, requestURI);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}


	
	/**
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#find(ContentTemplateI, int)
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI template, int hops) throws Exception {
		return ((FacadeContentManagementPlugin) getOwnerPlugin(getPluginURI())).find(template, hops);
	}


	/**
	 * @see fr.gestion_contenu.ports.interfaces.ContentManagementClientCI#match(ContentTemplateI, int, Set)
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template, int hops, Set<ContentDescriptorI> matched)
			throws Exception {
		return ((FacadeContentManagementPlugin) getOwnerPlugin(getPluginURI())).match(template, hops, matched);
	}

}
