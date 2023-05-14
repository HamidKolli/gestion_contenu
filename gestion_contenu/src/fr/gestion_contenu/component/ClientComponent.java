package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractClientComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagementFacade;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
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
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Un exemple de classe de client
 */
public class ClientComponent extends AbstractClientComponent {
	public static final String DIR_LOGGER_NAME = "loggers/clients/";
	public static final String FILE_LOGGER_NAME = "client";
	public static final int MIN_TIME_REQUEST = 5;
	public static final int MAX_TIME_REQUEST = 10;
	public static final int HOPS = 5;

	private List<ContentTemplateI> templates;
	private String uriContentManagementFacade;
	private OutPortContentManagementFacade portContentManagement;
	private ClockPlugin pluginClock;
	private String clockURI;
	private static int cpt = 0;
	private final int id;

	/**
	 * 
	 * Constructeur ClientComponent
	 * 
	 * @param clockURI					 : l'URI de l'horloge
	 * @param uriContentManagementFacade : l'URI du port entrant d'une facade
	 * @param template                   : la template du client
	 * @throws Exception
	 */
	protected ClientComponent(String clockURI, String uriContentManagementFacade, List<ContentTemplateI> template)
			throws Exception {
		super(1, 1);
		this.uriContentManagementFacade = uriContentManagementFacade;
		this.templates = template;
		this.clockURI = clockURI;
		this.getTracer().setTitle("Client");
		id = ++cpt;
//		this.getTracer().setOrigin(500 * (++cpt), 0);
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
		ContentDescriptorI descriptor = portContentManagement.find(template, HOPS);
		traceMessage(String.format("resultat du find %s : %s\n", template, descriptor));
		logMessage(String.format("@@@@@@@@@@@@@@@@@@@@@@@@@@@@\nresultat du find %s : %s\n", template, descriptor));

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public void match(ContentTemplateI template) throws Exception {
		List<ContentDescriptorI> descriptors = new ArrayList<>(portContentManagement.match(template, HOPS, new HashSet<>()));
		traceMessage(String.format("resultat du find %s : %s\n", template, descriptors));
		logMessage(String.format("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\nresultat du match %s : %s\n", template, descriptors));
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

		Random rand = new Random();
		long delay;
		for (ContentTemplateI template : templates) {

			delay = TimeUnit.SECONDS.toNanos(MIN_TIME_REQUEST + rand.nextInt(MAX_TIME_REQUEST));

			this.scheduleTask(o -> {
				try {
					if (((double) rand.nextInt()) / Integer.MAX_VALUE < 0.5) {
						traceMessage("find " + template + "\n");
						logMessage("##############################\nfind " + template + "\n");
						find(template);
					} else {
						traceMessage("match " + template + "\n");
						logMessage("##############################\nmatch " + template + "\n");
						match(template);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, delay, TimeUnit.NANOSECONDS);
		}
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
		printExecutionLogOnFile(DIR_LOGGER_NAME + FILE_LOGGER_NAME + id);
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
