package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import fr.gestion_contenu.component.interfaces.IContentRequestFacade;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagementFacade;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 * 
 *         Plugin s'occupant des differentes operations de match et find liees
 *         aux connexions de ContentManagement au niveau de la facade
 */
public class FacadeContentManagementPlugin extends FacadeNodeManagementPlugin implements IContentRequestFacade {

	private static final long serialVersionUID = 1L;
	private final int NB_ROOT_REQ;
	private ApplicationNodeAddressI application;
	private InPortContentManagementFacade portContentManagement;
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
	 * @param uriNodeManagement
	 */
	public FacadeContentManagementPlugin(ApplicationNodeAddressI applicationNodeAddress, int nbRoot, String uriNodeManagement,
			String uriContentManagement,ApplicationNodeAddressI uriFacadeSuivante) {
		super(applicationNodeAddress, nbRoot, uriNodeManagement, uriFacadeSuivante);
		requestClient = new ConcurrentHashMap<>();
		resultFind = new ConcurrentHashMap<>();
		resultMatch = new ConcurrentHashMap<>();
		this.application = applicationNodeAddress;
		this.contentManagementURI = uriContentManagement;
		NB_ROOT_REQ = nbRoot / 2;
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
		getOwner().traceMessage("start find facade" + cd + "\n");
		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeRoot.values());
		Collections.shuffle(listTmp);
		String requestURI = AbstractPort.generatePortURI();
		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);
		
		if(listTmp.size()> NB_ROOT_REQ) {
			listTmp =  listTmp.subList(0, NB_ROOT_REQ);
		}
			
		
		for (OutPortContentManagement op :listTmp) {
			op.find(cd, hops, application, requestURI);
		}

		sem.acquire();
		getOwner().traceMessage("fin find facade" + cd + "\n");
		return resultFind.remove(requestURI);
	}

	public Set<ContentDescriptorI> match(ContentTemplateI cd, int hops, Set<ContentDescriptorI> matched)
			throws Exception {

		getOwner().traceMessage("start match facade" + cd + "\n");
		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeRoot.values());
		Collections.shuffle(listTmp);

		String requestURI = AbstractPort.generatePortURI();
		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);
		for (OutPortContentManagement op : listTmp.subList(0, NB_ROOT_REQ)) {
			getOwner().traceMessage("loop match facade");
			op.match(cd, hops, application, requestURI, matched);
		}
		sem.acquire();
		getOwner().traceMessage("fin match facade" + cd + "\n");
		return resultMatch.remove(requestURI);
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
		assert requestURI != null;
		if (found != null)
			resultFind.put(requestURI, found);
		requestClient.get(requestURI).release();

	}

	@SuppressWarnings("null")
	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) {
		assert requestURI != null;
		if (matched != null || !(matched.isEmpty()))
			resultMatch.put(requestURI, matched);
		requestClient.get(requestURI).release();
	}

}
