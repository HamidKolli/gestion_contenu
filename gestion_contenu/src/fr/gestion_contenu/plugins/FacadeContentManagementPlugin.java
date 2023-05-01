package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagementFacade;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Plugin s'occupant des differentes operations de match et find liees
 *         aux connexions de ContentManagement au niveau de la facade
 */
public class FacadeContentManagementPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;
	private final int NB_ROOT_REQ;
	private ApplicationNodeAddressI application;
	private InPortContentManagementFacade portContentManagement;
	private ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private Map<String, Semaphore> requestClient;
	private Map<String, ContentDescriptorI> resultFind;
	private Map<String, Set<ContentDescriptorI>> resultMatch;
	private String contentManagementURI;

	/**
	 * Constructeur
	 * 
	 * @param applicationNodeAddress
	 * @param nbRoot
	 * @param uriContentManagement

	 */
	public FacadeContentManagementPlugin(ApplicationNodeAddressI applicationNodeAddress, int nbRoot, String uriContentManagement, ApplicationNodeAddressI uriFacadeSuivante,
			ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot) {
		super();
		requestClient = new ConcurrentHashMap<>();
		resultFind = new ConcurrentHashMap<>();
		resultMatch = new ConcurrentHashMap<>();
		this.application = applicationNodeAddress;
		this.contentManagementURI = uriContentManagement;
		NB_ROOT_REQ = nbRoot / 2;
		// On garde la ref pour pouvoir partager les donnees entre le plugin et le composant
		this.connectNodeRoot = connectNodeRoot;
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#installOn(ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {

		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
		this.addOfferedInterface(FacadeContentManagementCI.class);

	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		super.initialise();
		portContentManagement = new InPortContentManagementFacade(application.getContentManagementURI(), getOwner(),
				getPluginURI(), contentManagementURI);
		portContentManagement.publishPort();

	}

	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		getOwner().traceMessage("find " + cd + "\n");
		getOwner().logMessage("find | debut  cd = " + cd + "\n");

		String requestURI = AbstractPort.generatePortURI();
		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);

		Random rand = new Random();
		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeRoot.values());

		List<OutPortContentManagement> listTmp2 = listTmp;

		if (listTmp.size() > NB_ROOT_REQ) {
			listTmp2 = new ArrayList<>();
			int firstIndex = rand.nextInt(listTmp.size());
			for (int i = 0; i < NB_ROOT_REQ; i++) {
				listTmp2.add(listTmp.get((i + firstIndex) % listTmp.size()));
			}
		}

		for (OutPortContentManagement op : listTmp2) {
			op.find(cd, hops, application, requestURI);
		}

		sem.acquire();
		getOwner().logMessage("find | fin cd = " + cd + "\n");
		return resultFind.remove(requestURI);
	}

	public Set<ContentDescriptorI> match(ContentTemplateI cd, int hops, Set<ContentDescriptorI> matched)
			throws Exception {

		getOwner().traceMessage("match" + cd + "\n");
		getOwner().logMessage("match | debut  cd = " + cd + "\n");

		String requestURI = AbstractPort.generatePortURI();
		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);

		Random rand = new Random();
		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeRoot.values());
		List<OutPortContentManagement> listTmp2 = listTmp;

		if (listTmp.size() > NB_ROOT_REQ) {
			listTmp2 = new ArrayList<>();
			int firstIndex = rand.nextInt(listTmp.size());

			for (int i = 0; i < NB_ROOT_REQ; i++) {
				listTmp2.add(listTmp.get((i + firstIndex) % listTmp.size()));
			}
		}

		for (OutPortContentManagement op : listTmp2) {
			op.match(cd, hops, application, requestURI, matched);
		}
		sem.acquire();

		getOwner().logMessage("match | fin  cd = " + cd + "\n");
		return resultMatch.remove(requestURI);
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		portContentManagement.unpublishPort();
		portContentManagement.destroyPort();
		super.uninstall();
	}

	public void acceptFound(ContentDescriptorI found, String requestURI) {
		getOwner().logMessage("acceptFound |  cd = " + found + " request = " + requestURI + "\n");

		assert requestURI != null;
		if (found != null)
			resultFind.put(requestURI, found);
		requestClient.get(requestURI).release();

	}

	@SuppressWarnings("null")
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) {
		getOwner().logMessage("acceptMatched | request = " + requestURI + "\n");

		assert requestURI != null;
		if (matched != null || !(matched.isEmpty()))
			resultMatch.put(requestURI, matched);
		requestClient.get(requestURI).release();
	}

}
