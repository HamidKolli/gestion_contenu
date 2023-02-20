package fr.gestion_contenu.component;

import fr.gestion_contenu.component.interfaces.AbstractFacadeComponent;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.plugins.FacadeContentManagementPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;


/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe conctrete qui gere les facades
 *
 */
public class FacadeComponent extends AbstractFacadeComponent {
	private final int numberRootNode;

	private ApplicationNodeAddress applicationNodeAddress;
	private FacadeContentManagementPlugin plugin;


	/**
	 * 
	 * Constructeur FacadeComponent
	 * @param applicationNodeAddress : les addresses des ports entrants de la facade  
	 * @param nbrRacine : le nombre de noeuds racines de la facade
	 * @throws Exception
	 */
	protected FacadeComponent(ApplicationNodeAddress applicationNodeAddress, int nbrRacine) throws Exception {
		super(1, 0);
		this.applicationNodeAddress = applicationNodeAddress;
		this.numberRootNode = nbrRacine;
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
			plugin.installOn(this);
			plugin.initialise();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}




	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#finalise()
	*
	 */
	@Override
	public synchronized void finalise() throws Exception {
		plugin.finalise();
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
			plugin.uninstall();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
