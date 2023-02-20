package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.IConnectFacadeRequest;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.FacadePortNodeManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class FacadeNodeManagementPlugin extends AbstractPlugin implements IConnectFacadeRequest{

	private static final long serialVersionUID = 1L;
	protected Set<PeerNodeAddressI> connectToNode;
	protected Map<PeerNodeAddressI, OutPortContentManagement> connectNodeRoot;
	private FacadePortNodeManagement facadePortNodeManagement;
	private String facadePortNodeManagementURI;
	private final int numberRootNode;
	public static final int NODE_RETURN = 2;
	
	public FacadeNodeManagementPlugin(String facadePortNodeManagementURI,int nbRoot) {
		super();
		this.facadePortNodeManagementURI = facadePortNodeManagementURI;
		connectToNode = new HashSet<>();
		connectNodeRoot = new HashMap<>();
		this.numberRootNode = nbRoot;
	}
	
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(NodeManagementCI.class);
		this.addRequiredInterface(NodeManagementCI.class);
		facadePortNodeManagement = new FacadePortNodeManagement(facadePortNodeManagementURI,owner,getPluginURI());

	}
	
	@Override
	public void initialise() throws Exception {
		facadePortNodeManagement.publishPort();
		super.initialise();
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
			OutPortContentManagement port = new OutPortContentManagement(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), ((ContentNodeAddressI) a).getContentManagementURI(),
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
			OutPortContentManagement port = new OutPortContentManagement(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), ((ContentNodeAddressI) resultList.get(0)).getContentManagementURI(),
					ConnectorContentManagement.class.getCanonicalName());
			
			connectNodeRoot.put(resultList.get(0), port);
		}
		
	}
	
	
	@Override
	public void finalise() throws Exception {

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) 
			port.getValue().doDisconnection();
		super.finalise();
	}
	
	@Override
	public void uninstall() throws Exception {
		this.facadePortNodeManagement.unpublishPort();
		this.facadePortNodeManagement.destroyPort();

		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeRoot.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		super.uninstall();
	}
	
	
}
