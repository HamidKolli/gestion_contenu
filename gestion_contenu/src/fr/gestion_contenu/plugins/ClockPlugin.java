package fr.gestion_contenu.plugins;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

public class ClockPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	private ClocksServerOutboundPort portClock;
	private String clockURI;

	public ClockPlugin(String clockURI) {
		super();
		this.clockURI = clockURI;
	}

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(ClocksServerCI.class);
	}

	@Override
	public void initialise() throws Exception {
		this.portClock = new ClocksServerOutboundPort(getOwner());
		portClock.publishPort();
		getOwner().doPortConnection(portClock.getPortURI(), ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		super.initialise();
	}

	public AcceleratedClock getClock() throws Exception {
		return this.portClock.getClock(clockURI);
	}

	@Override
	public void finalise() throws Exception {
		portClock.doDisconnection();
		super.finalise();
	}

	@Override
	public void uninstall() throws Exception {
		portClock.unpublishPort();
		portClock.destroyPort();
		super.uninstall();
	}

}
