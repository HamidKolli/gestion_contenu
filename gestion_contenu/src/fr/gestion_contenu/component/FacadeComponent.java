package fr.gestion_contenu.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractFacadeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { NodeCI.class, ContentManagementCI.class })
@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe conctrete qui gere les facades
 *
 */
public class FacadeComponent extends AbstractFacadeComponent {
	private final int numberRootNode;

	private ApplicationNodeAddress applicationNodeAddress;
	private FacadePortNodeManagement facadePortNodeManagement;
	private InPortContentManagement inPortContentManagement;
	private Set<PeerNodeAddressI> connectToNode;
	private Map<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	public static final int NODE_RETURN = 2;

	/**
	 * 
	 * Constructeur FacadeComponent
	 * @param applicationNodeAddress : les addresses des ports entrants de la facade  
	 * @param nbrRacine : le nombre de noeuds racines de la facade
	 * @throws Exception
	 */
	protected FacadeComponent(ApplicationNodeAddress applicationNodeAddress, int nbrRacine) throws Exception {
		super(1, 0);
		this.applicationNodeAddress = applicationNodeAddress;
		this.numberRootNode = nbrRacine;
		this.connectToNode = new HashSet<>();
		this.connectNodeRoot = new HashMap<>();

	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#start()
	*
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.facadePortNodeManagement = new FacadePortNodeManagement(applicationNodeAddress.getNodeManagementURI(),
					this);
			this.inPortContentManagement = new InPortContentManagement(applicationNodeAddress.getContentManagementURI(),
					this);
			this.facadePortNodeManagement.publishPort();
			this.inPortContentManagement.publishPort();
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#join(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
		System.out.println("join " + a.getNodeIdentifier());
		if (connectNodeRoot.size() < numberRootNode) {
			OutPortContentManagement port = new OutPortContentManagement(this);
			port.publishPort();
			doPortConnection(port.getPortURI(), ((ContentNodeAddressI) a).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			connectNodeRoot.put(a, port);
		}
		
		connectToNode.add(a);
		Set<PeerNodeAddressI> result ;
		
		if (connectToNode.size() < NODE_RETURN) {
			result = new HashSet<>(connectToNode);
			System.out.println("fin join " + a.getNodeIdentifier()  + "size result " + result.size() );
			return result;
		}
		
		
		result = new HashSet<>();
		List<PeerNodeAddressI> resultList = new ArrayList<>(connectToNode);

		Collections.shuffle(resultList);

		result.addAll(resultList.subList(0,Math.max(NODE_RETURN, resultList.size())));
		System.out.println("fin join " + a.getNodeIdentifier() + "size result " + result.size() );
		
		return result;
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IConnectFacadeRequest#leave(fr.gestion_contenu.node.interfaces.PeerNodeAddressI)
	*
	 */
	@Override
	public  void leave(PeerNodeAddressI a) throws Exception {
		System.out.println("leave " + a.getNodeIdentifier());
		connectToNode.remove(a);
		if(connectNodeRoot.containsKey(a)) {
			OutPortContentManagement portAncien =  connectNodeRoot.get(a);
			portAncien.doDisconnection();
			portAncien.unpublishPort();
			portAncien.destroyPort();
			
			
			connectNodeRoot.remove(a);
			if(connectToNode.size() == 0) {
				return ;
			}
			
			List<PeerNodeAddressI> resultList = new ArrayList<>(connectToNode);
			Collections.shuffle(resultList);
			OutPortContentManagement port = new OutPortContentManagement(this);
			port.publishPort();
			doPortConnection(port.getPortURI(), ((ContentNodeAddressI) resultList.get(0)).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			
			connectNodeRoot.put(resultList.get(0), port);
		}
		
	}

	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#find(fr.gestion_contenu.content.interfaces.ContentTemplateI, int)
	*
	 */
	@Override
	public ContentDescriptorI find(ContentTemplateI cd, int hops) throws Exception {
		System.out.println("start find facade"+ cd);
		ContentDescriptorI c;
		for (OutPortContentManagement op : connectNodeRoot.values()) {
			if((c = op.find(cd, hops)) != null) {
				System.out.println("fin find facade"+ cd);
				return c;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	* @see fr.gestion_contenu.component.interfaces.IContentRequest#match(fr.gestion_contenu.content.interfaces.ContentTemplateI, java.util.Set, int)
	*
	 */
	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops) throws Exception {
		System.out.println("start match facade"+ cd);
		for (OutPortContentManagement op : connectNodeRoot.values()) {
			System.out.println("loop match facade");
			op.match(cd, matched, hops);
		}
		System.out.println("fin match facade"+ cd);
		
		return matched;
	}

	/**
	 * 
	* @see fr.sorbonne_u.components.AbstractComponent#finalise()
	*
	 */
	@Override
	public synchronized void finalise() throws Exception {
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			doPortDisconnection(port.getValue().getPortURI());
		}
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
			this.facadePortNodeManagement.unpublishPort();
			this.inPortContentManagement.unpublishPort();

			for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
				port.getValue().unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
