package fr.gestion_contenu.component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractClientComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.plugins.ClockPlugin;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

@RequiredInterfaces(required = { ContentManagementCI.class })

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Un exemple de classe de client
 */
public class ClientComponent extends AbstractClientComponent {
	private ContentTemplateI template;
	private String uriContentManagementFacade;
	private OutPortContentManagement portContentManagement;
	private ClockPlugin pluginClock;
	private String clockURI;

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
		this.getTracer().setOrigin(500, 500);
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 *
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.portContentManagement = new OutPortContentManagement(this);
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
	public ContentDescriptorI find(ContentTemplateI template) throws Exception {
		return this.portContentManagement.find(template, 1);

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template) throws Exception {
		return this.portContentManagement.match(template, new HashSet<>(), 1);

	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 *
	 */
	@Override
	public void execute() throws Exception {

		AcceleratedClock clock = pluginClock.getClock();

		Instant instant = clock.getStartInstant();
		clock.waitUntilStart();
		System.out.println("client after wait");
		long delay = TimeUnit.SECONDS.toNanos(3);
		System.out.println("debut client");

		this.scheduleTask(o -> {
			((ClientComponent) o).traceMessage("debut client");
			try {
				doPortConnection(this.portContentManagement.getPortURI(), this.uriContentManagementFacade,
						ConnectorContentManagement.class.getCanonicalName());

				ContentDescriptorI descriptor = find(template);
				((ClientComponent) o)
						.traceMessage("client find template = " + template + " result = " + descriptor + "\n");
				Set<ContentDescriptorI> descriptors = match(template);
				((ClientComponent) o).traceMessage("match start" + template + "\n");
				for (ContentDescriptorI contentDescriptorI : descriptors) {
					((ClientComponent) o).traceMessage("match : " + contentDescriptorI + "\n");
				}

				((ClientComponent) o).traceMessage("fin client");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delay, TimeUnit.NANOSECONDS);
		System.out.println("Fin execute client");

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
