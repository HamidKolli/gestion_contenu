package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import fr.gestion_contenu.component.interfaces.IContentRequestFacade;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
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
public class FacadeContentManagementPlugin extends FacadeNodeManagementPlugin implements IContentRequestFacade{

	private static final long serialVersionUID = 1L;
	private final int NB_ROOT_REQ;
	private ApplicationNodeAddress application;
	private InPortContentManagementFacade portContentManagement;
	private Map<String, Semaphore> requestClient;
	private Map<String, ContentDescriptorI> resultFind;
	private Map<String, Set<ContentDescriptorI>> resultMatch;

	/**
	 * Constructeur
	 * 
	 * @param application
	 * @param nbRoot
	 */
	public FacadeContentManagementPlugin(ApplicationNodeAddress application, int nbRoot) {
		super(application, nbRoot);
		requestClient = new HashMap<>();
		resultFind = new HashMap<>();
		resultMatch = new HashMap<>();
		this.application = application;
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
				getPluginURI());
		portContentManagement.publishPort();

	}

	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		getOwner().traceMessage("start find facade" + cd + "\n");
		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeRoot.values());
		Collections.shuffle(listTmp);
		String requestURI = AbstractPort.generatePortURI();
		Semaphore sem = new Semaphore(0);
		requestClient.put(requestURI, sem);
		for (OutPortContentManagement op : listTmp.subList(0, NB_ROOT_REQ)) {

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
		resultFind.put(requestURI, found);
		requestClient.get(requestURI).release();

	}

	public void acceptMatched(Set<ContentDescriptorI> matched, String requestURI) {
		resultMatch.put(requestURI, matched);
		requestClient.get(requestURI).release();
	}

}
