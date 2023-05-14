package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
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
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Classe concrete qui gere les composant de noeud
 *
 */
public class NodeComponent extends AbstractNodeComponent {

	
	
	
	public static final String DIR_LOGGER_NAME = "loggers/nodes/";
	public static final String FILE_LOGGER_NAME = "node";
	public static final int NB_THREAD_CONTENT_MANAGEMENT = 5;
	public static final int NB_THREAD_NODE_MANAGEMENT = 5;
	public static final int TIME_IN_NETWORK = 30;
	public static final int MIN_TIME_TO_JOIN = 1;
	public static final int MAX_TIME_TO_JOIN = 5;
	private Set<ContentDescriptorI> contentDescriptor;
	private ContentNodeAddressI nodeAddress;
	private NodePortNodeManagement portNodeManagement;
	private ContentManagementPlugin plugin;
	private String portFacadeManagementURI;
	private ClockPlugin pluginClock;
	private String clockURI;
	private List<PeerNodeAddressI> neighbours;

	private Semaphore sem = new Semaphore(0);


	/**
	 * 
	 * Constructeur NodeComponent
	 * 
	 * @param clockURI				  : l'URI de l'horloge
	 * @param contentDescriptorI      : le descripteur du noeud
	 * @param portFacadeManagementURI : l'uri du port entrant d'une facade (pour
	 *                                demander de rejoindre le reseau)
	 * @throws Exception
	 */
	protected NodeComponent(String clockURI, Set<ContentDescriptorI> contentDescriptorI, String portFacadeManagementURI,
			ContentNodeAddressI nodeAddress) throws Exception {
		super(10, 1);
		this.contentDescriptor = contentDescriptorI;
		this.nodeAddress = nodeAddress;
		this.portFacadeManagementURI = portFacadeManagementURI;
		this.clockURI = clockURI;
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

			createNewExecutorService(uriConnection, NB_THREAD_NODE_MANAGEMENT, false);
			createNewExecutorService(uriContentManagement, NB_THREAD_CONTENT_MANAGEMENT, false);

			plugin = new ContentManagementPlugin(nodeAddress, uriConnection, uriContentManagement);
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
		logMessage("Join \n");
		assert !this.portNodeManagement.connected();
		doPortConnection(this.portNodeManagement.getPortURI(), this.portFacadeManagementURI,
				ConnectorNodeManagement.class.getCanonicalName());
		portNodeManagement.join(nodeAddress);

	}

	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#match(fr.gestion_contenu.content.interfaces.ContentTemplateI)
	 *
	 */
	@Override
	public List<ContentDescriptorI> match(ContentTemplateI template) {
		List<ContentDescriptorI> result = new ArrayList<>();
		
		for (ContentDescriptorI cd : contentDescriptor) {
			if(cd.match(template))
				result.add(cd);
		}
		
		return result;
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 *
	 */
	@Override
	public void execute() throws Exception {

		AcceleratedClock clock = pluginClock.getClock();
		clock.waitUntilStart();

		Random r = new Random();
		int timeToStart = MIN_TIME_TO_JOIN + r.nextInt(MAX_TIME_TO_JOIN);
		long delay = TimeUnit.SECONDS.toNanos(timeToStart);
		
		this.scheduleTask(o -> {
			try {
				join();
				sem.acquire();
				for (PeerNodeAddressI peerAddress : neighbours) {
					plugin.connect(peerAddress);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, delay, TimeUnit.NANOSECONDS);
		
		
		
		delay = TimeUnit.SECONDS.toNanos(timeToStart + TIME_IN_NETWORK);
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
		portNodeManagement.leave(nodeAddress);

		
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
	
	
	/**
	 * 
	 * @see fr.gestion_contenu.component.interfaces.AbstractNodeComponent#acceptNeighbours()
	 *
	 */
	@Override
	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) {
		this.neighbours = new ArrayList<>(neighbours) ;
		sem.release();

	}

}
