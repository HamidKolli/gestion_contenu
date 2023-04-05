package fr.gestion_contenu.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.plugins.ClockPlugin;
import fr.gestion_contenu.plugins.ContentManagementPlugin;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

@RequiredInterfaces(required = { NodeManagementCI.class })

/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD Classe conctrete qui gere les composant
 *         de noeud
 *
 */
public class NodeComponent extends AbstractNodeComponent {

	private ContentDescriptorI contentDescriptorI;
	private NodePortNodeManagement portNodeManagement;
	private ContentManagementPlugin plugin;
	private String portFacadeManagementURI;
	private ClockPlugin pluginClock;
	private String clockURI;
	private Set<PeerNodeAddressI> neighbours;
	private static int cptX = 0;
	private static int cptY = 0;
	private static int cptID;
	private final int id;

	private final int nombreThreadNodeManagement;
	private final int nombreThreadContentManagement;
	private Semaphore sem = new Semaphore(0);

	/**
	 * 
	 * Constructeur NodeComponent
	 * 
	 * @param contentDescriptorI      : le descripteur du noeud
	 * @param portFacadeManagementURI : l'uri du port entrant d'une facade (pour
	 *                                demander de joindre le reseau)
	 * @throws Exception
	 */
	protected NodeComponent(String clockURI, ContentDescriptorI contentDescriptorI, String portFacadeManagementURI,
			int nombreThreadNodeManagement, int nombreThreadContentManagement) throws Exception {
		super(3, 1);

		id = ++cptID;
		this.contentDescriptorI = contentDescriptorI;
		this.portFacadeManagementURI = portFacadeManagementURI;
		this.clockURI = clockURI;
		this.nombreThreadNodeManagement = nombreThreadNodeManagement;

		this.nombreThreadContentManagement = nombreThreadContentManagement;
		this.getTracer().setTitle("Node "+ id);
		if (cptX % 4 == 0) {
			cptY++;
		}

		this.getTracer().setOrigin(480 * (((cptX++) + 1) % 4), 250 * (cptY % 4));

	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.portNodeManagement = new NodePortNodeManagement(this);
			this.portNodeManagement.publishPort();
			String uriConnection = AbstractPort.generatePortURI();
			String uriContentManagement = AbstractPort.generatePortURI();

			createNewExecutorService(uriConnection, nombreThreadNodeManagement, false);
			createNewExecutorService(uriContentManagement, nombreThreadContentManagement, false);

			plugin = new ContentManagementPlugin(contentDescriptorI.getContentNodeAddress(), uriConnection,
					uriContentManagement);
			plugin.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(plugin);

			pluginClock = new ClockPlugin(clockURI);
			pluginClock.setPluginURI(AbstractPort.generatePortURI());
			this.installPlugin(pluginClock);

			super.start();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#join()
	 *
	 */
	@Override
	public synchronized void join() throws Exception {
		traceMessage("Join \n");
		assert !this.portNodeManagement.connected();
		doPortConnection(this.portNodeManagement.getPortURI(), this.portFacadeManagementURI,
				ConnectorNodeManagement.class.getCanonicalName());
		portNodeManagement.join(contentDescriptorI.getContentNodeAddress());

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public ContentDescriptorI match(ContentTemplateI template) {
		return (contentDescriptorI.match(template) ? contentDescriptorI : null);
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

		
		join();
		sem.acquire();
		List<PeerNodeAddressI> peerNodeAddress = new ArrayList<>(neighbours);
		for (int i = 0;i< peerNodeAddress.size();i++) {
			if (!peerNodeAddress.get(i).equals(contentDescriptorI.getContentNodeAddress())) {
				plugin.connect(peerNodeAddress.get(i));
			}
		}
		long delay = clock.nanoDelayUntilAcceleratedInstant(instant.plusSeconds(1000 + id * 100));

		this.scheduleTask(o -> {
			try {
				((NodeComponent) o).leave();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delay, TimeUnit.NANOSECONDS);

		super.execute();
	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#leave()
	 *
	 */
	@Override
	public void leave() throws Exception {

		plugin.leave();
		portNodeManagement.leave(contentDescriptorI.getContentNodeAddress());

		traceMessage("Leave \n");
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 *
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.portNodeManagement.doDisconnection();
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
			portNodeManagement.unpublishPort();
			portNodeManagement.destroyPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) {
		this.neighbours = neighbours;
		sem.release();
	}

}
