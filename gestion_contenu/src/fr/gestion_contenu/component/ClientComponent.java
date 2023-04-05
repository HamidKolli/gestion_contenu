package fr.gestion_contenu.component;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractClientComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagementFacade;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.plugins.ClockPlugin;
import fr.gestion_contenu.ports.OutPortContentManagementFacade;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

@RequiredInterfaces(required = { ContentManagementCI.class, FacadeContentManagementCI.class })

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Un exemple de classe de client
 */
public class ClientComponent extends AbstractClientComponent {
	private ContentTemplateI template;
	private String uriContentManagementFacade;
	private OutPortContentManagementFacade portContentManagement;
	private ClockPlugin pluginClock;
	private String clockURI;
	private static int cpt = 0;

	/**
	 * 
	 * Constructeur ClientComponent
	 * 
	 * @param uriContentManagementFacade : l'URI du port entrant d'une facade
	 * @param template                   : la template du client
	 * @throws Exception
	 */
	protected ClientComponent(String clockURI, String uriContentManagementFacade, ContentTemplateI template)
			throws Exception {
		super(1, 1);
		this.uriContentManagementFacade = uriContentManagementFacade;
		this.template = template;
		this.clockURI = clockURI;
		this.getTracer().setTitle("Client");
		this.getTracer().setOrigin(500 * (++cpt), 0);
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 *
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.portContentManagement = new OutPortContentManagementFacade(this);
			this.portContentManagement.publishPort();

			pluginClock = new ClockPlugin(clockURI);
			pluginClock.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(pluginClock);

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#find(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public void find(ContentTemplateI template) throws Exception {
		traceMessage(String.format("resultat du find : %s\n", portContentManagement.find(template, 20)));

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public void match(ContentTemplateI template) throws Exception {

		traceMessage(
				String.format("resultat du match : %s\n", portContentManagement.match(template, 20, new HashSet<>())));

	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 *
	 */
	@Override
	public void execute() throws Exception {
		traceMessage("debut client\n");

		doPortConnection(this.portContentManagement.getPortURI(), this.uriContentManagementFacade,
				ConnectorContentManagementFacade.class.getCanonicalName());

		AcceleratedClock clock = pluginClock.getClock();

		clock.waitUntilStart();

		long delay = TimeUnit.SECONDS.toNanos(5);

		this.scheduleTask(o -> {
			try {
				((ClientComponent) o).traceMessage("find " + template + "\n");
				find(template);
				((ClientComponent) o).traceMessage("match " + template + "\n");
				match(template);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delay, TimeUnit.NANOSECONDS);

		super.execute();
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 *
	 */
	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(this.portContentManagement.getPortURI());
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
			this.portContentManagement.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
