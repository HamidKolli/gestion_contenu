package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.experimentation.Experimentation;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagementFacade;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 *         Plugin s'occupant des differentes operations de match et find liees
 *         aux connexions de ContentManagement au niveau de la facade
 */
public class FacadeContentManagementPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;
	private static final long NB_SECOND_RELEASE = 5;
	private final int NB_ROOT_REQ;
	private ApplicationNodeAddressI application;
	private InPortContentManagementFacade portContentManagement;
	private ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private ConcurrentMap<String, Semaphore> requestClient;
	private ConcurrentMap<String, ContentDescriptorI> resultFind;
	private ConcurrentMap<String, Integer> nbAcceptFind;
	private ConcurrentMap<String, Set<ContentDescriptorI>> resultMatch;

	// Experimentation
	private static ConcurrentMap<String, Long> timeRequest = new ConcurrentHashMap<>();
	private static ConcurrentMap<String, Long> timeFound = new ConcurrentHashMap<>();
	private static ConcurrentMap<String, Boolean> isFinishedRequest = new ConcurrentHashMap<>();
	private static ConcurrentMap<String, Boolean> nbSuccessFind = new ConcurrentHashMap<>();

	private String contentManagementURI;
	private static boolean isPrint = true;
	private static Lock printLock = new ReentrantLock();
	private final String uriExecutorReleaseClient;


	/**
	 * Constructeur
	 * 
	 * @param applicationNodeAddress : le adresses de la facade qui instancie le plugin
	 * @param nbRoot : le nombre de noeuds racine que la facade souhaite avoir
	 * @param uriContentManagement : l'uri du service executor qui gere la gestion des contenus
	 * @param uriExecutorReleaseClient : l'uri du service executor qui gere le deblocage des clients
	 * @param uriFacadeSuivante : l'URI de la facade qui recoie les requettes probe
	 * @param connectNodeRoot : l'ensemble des noeuds de la facade (reference partagee)
	 */
	public FacadeContentManagementPlugin(ApplicationNodeAddressI applicationNodeAddress, int nbRoot,
			String uriContentManagement, String uriExecutorReleaseClient, ApplicationNodeAddressI uriFacadeSuivante,
			ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot) {
		super();
		this.uriExecutorReleaseClient = uriExecutorReleaseClient;
		requestClient = new ConcurrentHashMap<>();
		resultFind = new ConcurrentHashMap<>();
		resultMatch = new ConcurrentHashMap<>();
		nbAcceptFind = new ConcurrentHashMap<>();
		this.application = applicationNodeAddress;
		this.contentManagementURI = uriContentManagement;
		NB_ROOT_REQ = nbRoot / 2;
		// On garde la ref pour pouvoir partager les donnees entre le plugin et le
		// composant
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

	/**
	 * Methode qui permet de rechercher un contenu
	 * 
	 * @param cd   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * 
	 * @return le contenu trouvee
	 * @throws Exception
	 */
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		getOwner().traceMessage("find " + cd + "\n");
		getOwner().logMessage("find | debut  cd = " + cd + "\n");

		String requestURI = AbstractPort.generatePortURI();

		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);
		nbAcceptFind.put(requestURI, 0);
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

		// Experimentation
		timeRequest.put(requestURI, System.currentTimeMillis());
		nbSuccessFind.put(requestURI, false);

		for (OutPortContentManagement op : listTmp2) {
			op.find(cd, hops, application, requestURI);
		}

		long delay = TimeUnit.SECONDS.toNanos(NB_SECOND_RELEASE);

		this.scheduleTaskOnComponent(uriExecutorReleaseClient, new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				sem.release();
			}
		}, delay, null);

		resultFind.put(requestURI, null);
		sem.acquire();
		getOwner().logMessage("find | fin cd = " + cd + "\n");
		return resultFind.remove(requestURI);
	}

	
	/**
	 * Methode qui permet de rechercher des contenus similaires a un template
	 * 
	 * @param cd   : le template du contenu qu'on recherche
	 * @param hops : le nombre de pas pour eviter les appels infinis
	 * @param matched : les descriptors qui matchent jusqu'a present
	 * 
	 * @return Set(ContentDescriptorI) l'ensemble des contenus qui matchent
	 * 
	 * @throws Exception
	 */
	

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

		// Experimentation
		timeRequest.put(requestURI, System.currentTimeMillis());

		for (OutPortContentManagement op : listTmp2) {
			op.match(cd, hops, application, requestURI, matched);
		}
		sem.acquire();

		getOwner().logMessage("match | fin  cd = " + cd + "\n");
		return resultMatch.remove(requestURI);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */
	@Override
	public void finalise() throws Exception {

		printLock.lock();
		if (isPrint) {
			Long max = 0L;
			Long min = Long.MAX_VALUE;
			Long moy = 0L;

			for (Map.Entry<String, Long> timeReq : timeFound.entrySet()) {
				if (isFinishedRequest.containsKey(timeReq.getKey())) {
					if (timeReq.getValue() > max)
						max = timeReq.getValue();
					if (timeReq.getValue() < min)
						min = timeReq.getValue();
					moy += timeReq.getValue();
				}

			}
			System.out.println("le nombre de requettes qui n'ont pas eu de resultat: "
					+ (timeRequest.size() - isFinishedRequest.size()));
			System.out.println("le temps max (Ms): " + max);
			System.out.println("le temps min (Ms): " + min);

			System.out.println("le temps moyen (Ms): " + (moy / isFinishedRequest.size()));
			int nbSuccess = 0;
			for (Boolean suc : nbSuccessFind.values()) {
				if (suc) {
					nbSuccess++;
				}
			}
			System.out.println("Nombre requettes find : " + nbSuccessFind.size() + " succes  : " + nbSuccess);
			isPrint = false;
			Experimentation.setNbReqFailed((timeRequest.size() - isFinishedRequest.size()));
			Experimentation.setNbReqFindSuccess(nbSuccess);
			Experimentation.setTimeMaxReq(max);
			Experimentation.setTimeMinReq(min);
			Experimentation.setTimeMoyReq(moy / isFinishedRequest.size());
		}
		printLock.unlock();


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

	/**
	 * 
	 * Methode qui est appellee quand la requette find trouve le contenu qui match avec le patron
	 * @param found : le descripteur retournee
	 * @param requestURI : l'identifiant de la requette
	 */
	public void acceptFound(ContentDescriptorI found, String requestURI) {
		getOwner().logMessage("acceptFound |  content = " + found + " request = " + requestURI + "\n");

		assert requestURI != null;

		// Experimentation
		long time = System.currentTimeMillis() - timeRequest.get(requestURI);
		timeFound.put(requestURI, time);
		isFinishedRequest.put(requestURI, true);
		nbSuccessFind.put(requestURI, true);

		requestClient.get(requestURI).release();

	}

	/**
	 * Methode de retour du match
	 * 
	 * @param matched : l'ensemble des descriptors trouves par le match
	 * @param requestURI : l'uri associe a la requete emise
	 * 
	 */
	@SuppressWarnings("null")
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) {
		getOwner().logMessage("acceptMatched | request = " + requestURI + "\n");

		// Experimentation
		long time = System.currentTimeMillis() - timeRequest.get(requestURI);
		timeFound.put(requestURI, time);
		isFinishedRequest.put(requestURI, true);

		assert requestURI != null;
		if (matched != null || !(matched.isEmpty()))
			resultMatch.put(requestURI, matched);
		requestClient.get(requestURI).release();
	}

}
