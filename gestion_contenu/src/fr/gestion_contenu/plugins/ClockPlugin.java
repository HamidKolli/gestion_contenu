package fr.gestion_contenu.plugins;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * 
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes operations de connexion et de deconnexion
 *         necessaires pour l'utilisation du systeme de Clock
 */
public class ClockPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	private ClocksServerOutboundPort portClock;
	private String clockURI;

	/**
	 * Constructeur
	 * @param clockURI : l'Uri de l'horloge concernee
	 */
	public ClockPlugin(String clockURI) {
		super();
		this.clockURI = clockURI;
	}

	
	/**
	 * @see fr.sorbonne_u.components.PluginI#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(ClocksServerCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		this.portClock = new ClocksServerOutboundPort(getOwner());
		portClock.publishPort();
		getOwner().doPortConnection(portClock.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		super.initialise();
	}

	
	/**
	 * @see fr.sorbonne_u.utils.aclocks.ClocksServerCI#getClock(java.lang.String)
	 */
	public AcceleratedClock getClock() throws Exception {
		return this.portClock.getClock(clockURI);
	}

	
	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		portClock.doDisconnection();
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		portClock.unpublishPort();
		portClock.destroyPort();
		super.uninstall();
	}

}
