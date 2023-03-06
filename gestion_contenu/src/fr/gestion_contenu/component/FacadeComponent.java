package fr.gestion_contenu.component;

import fr.gestion_contenu.component.interfaces.AbstractFacadeComponent;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.plugins.FacadeContentManagementPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD Classe conctrete qui gere les facades
 *
 */
public class FacadeComponent extends AbstractFacadeComponent {
	private final int numberRootNode;

	private ApplicationNodeAddress applicationNodeAddress;
	private FacadeContentManagementPlugin plugin;


	/**
	 * 
	 * Constructeur FacadeComponent
	 * 
	 * @param applicationNodeAddress : les addresses des ports entrants de la facade
	 * @param nbrRacine              : le nombre de noeuds racines de la facade
	 * @throws Exception
	 */
	protected FacadeComponent(String clockURI, ApplicationNodeAddress applicationNodeAddress, int nbrRacine)
			throws Exception {
		super(10, 0);
		this.applicationNodeAddress = applicationNodeAddress;
		this.numberRootNode = nbrRacine;
		this.getTracer().setTitle("Facade");
		
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 *
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			plugin = new FacadeContentManagementPlugin(applicationNodeAddress, numberRootNode);
			plugin.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(plugin);

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	@Override
	public synchronized void finalise() throws Exception {
		super.finalise();
	}

}
