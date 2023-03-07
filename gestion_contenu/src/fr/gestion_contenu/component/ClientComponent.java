package fr.gestion_contenu.component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractClientComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.plugins.ClockPlugin;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.ReturnResultInPort;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

@RequiredInterfaces(required = { ContentManagementCI.class })
@OfferedInterfaces(offered = { ReturnResultCI.class })

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
	private ReturnResultInPort portReturn;
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
			this.portContentManagement = new OutPortContentManagement(this);
			this.portContentManagement.publishPort();
			this.portReturn = new ReturnResultInPort(this);
			portReturn.publishPort();

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
	public void find(ContentTemplateI template, String uriReturn) throws Exception {
		this.portContentManagement.find(template, 2, uriReturn);

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public void match(ContentTemplateI template, String uriReturn) throws Exception {
		this.portContentManagement.match(template, new HashSet<>(), 2, uriReturn);

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
				ConnectorContentManagement.class.getCanonicalName());

		AcceleratedClock clock = pluginClock.getClock();

		clock.waitUntilStart();

		long delay = TimeUnit.SECONDS.toNanos(3);

		this.scheduleTask(o -> {
			try {
				((ClientComponent) o).traceMessage("find " + template + "\n");
				find(template, portReturn.getPortURI());
				((ClientComponent) o).traceMessage("match " + template + "\n");
				match(template, portReturn.getPortURI());

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
			this.portReturn.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnMatch(Set)
	 */
	@Override
	public void returnMatch(Set<ContentDescriptorI> descriptors) throws Exception {
		if (descriptors == null || descriptors.isEmpty())
			return;
		traceMessage("match start" + template + "\n");
		for (ContentDescriptorI contentDescriptorI : descriptors) {
			traceMessage("match : " + contentDescriptorI + "\n");
		}

	}

	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnFind(ContentDescriptorI)
	 */
	@Override
	public void returnFind(ContentDescriptorI descriptor) throws Exception {
		if (descriptor == null)
			return;
		traceMessage("client find template = " + template + " result = " + descriptor + "\n");

	}

}
