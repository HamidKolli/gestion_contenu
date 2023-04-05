package fr.gestion_contenu.component;

import fr.gestion_contenu.component.interfaces.AbstractFacadeComponent;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.plugins.FacadeContentManagementPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD Classe concrete qui gere les facades
 *
 */
public class FacadeComponent extends AbstractFacadeComponent {
	private final int numberRootNode;

	private final int nombreThreadNodeManagement;
	private final int nombreThreadContentManagement;

	private ApplicationNodeAddressI applicationNodeAddress;
	private FacadeContentManagementPlugin plugin;

	private ApplicationNodeAddressI uriFacadeSuivante;

	/**
	 * 
	 * Constructeur FacadeComponent
	 * 
	 * @param applicationNodeAddress : les addresses des ports entrants de la facade
	 * @param nbrRacine              : le nombre de noeuds racines de la facade
	 * @throws Exception
	 */
	protected FacadeComponent(String clockURI, ApplicationNodeAddressI applicationNodeAddress, int nbrRacine,
			int nombreThreadNodeManagement, int nombreThreadContentManagement,ApplicationNodeAddressI uriFacadeSuivante) throws Exception {
		super(10, 0);
		this.applicationNodeAddress = applicationNodeAddress;
		this.numberRootNode = nbrRacine;
		this.nombreThreadNodeManagement = nombreThreadNodeManagement;
		this.nombreThreadContentManagement = nombreThreadContentManagement;
		this.getTracer().setTitle("Facade");
		this.uriFacadeSuivante = uriFacadeSuivante;

	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 *
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {

			String uriContentManagement = AbstractPort.generatePortURI();
			String uriNodeManagement = AbstractPort.generatePortURI();
			createNewExecutorService(uriContentManagement, nombreThreadContentManagement, false);
			createNewExecutorService(uriNodeManagement, nombreThreadNodeManagement, false);
			plugin = new FacadeContentManagementPlugin(applicationNodeAddress, numberRootNode, uriNodeManagement,
					uriContentManagement,uriFacadeSuivante);
			plugin.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(plugin);

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

}
