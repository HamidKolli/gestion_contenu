package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Classe qui represente le port entrant d'un noeud/facade pour les
 *         requettes sur les contenus
 */
public class InPortContentManagement extends AbstractInboundPort implements ContentManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Constructeur InPortContentManagement
	 * 
	 * @param uri   : l'URI du port
	 * @param owner : le composant qui le possede
	 * @throws Exception
	 */
	public InPortContentManagement(String uri, ComponentI owner, String pluginURI) throws Exception {
		super(uri, ContentManagementCI.class, owner, pluginURI, null);
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      int)
	 *
	 */
	@Override
	public void find(ContentTemplateI cd, int hops, String uriReturn) throws Exception {
		getOwner().runTask(new AbstractComponent.AbstractTask(pluginURI) {

			@Override
			public void run() {
				try {
					((IContentRequest) getTaskProviderReference()).find(cd, hops, uriReturn);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI,
	 *      java.util.Set, int)
	 *
	 */
	@Override
	public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String uriReturn)
			throws Exception {
		getOwner().runTask(new AbstractComponent.AbstractTask(pluginURI) {

			@Override
			public void run() {
				try {
					((IContentRequest) getTaskProviderReference()).match(cd, matched, hops, uriReturn);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

}
