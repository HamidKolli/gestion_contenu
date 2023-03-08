package fr.gestion_contenu.plugins;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.gestion_contenu.connectors.ConnectorResult;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.ReturnResultOutPort;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes operations de connexion, deconnexion, match et find
 *         liees aux connexions de ContentManagement
 */
public class ContentManagementPlugin extends ConnectionNodePlugin implements IContentRequest {

	private static final long serialVersionUID = 1L;
	private ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private ContentNodeAddressI contentNodeAddress;
	private InPortContentManagement inPortContentManagement;

	/**
	 * Constructeur
	 * @param contentNodeAddress : l'addresse du noeud en question
	 */
	public ContentManagementPlugin(ContentNodeAddressI contentNodeAddress) {
		super(contentNodeAddress);
		connectNodeContent = new ConcurrentHashMap<>();
		this.contentNodeAddress = contentNodeAddress;
	}

	
	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
		this.addRequiredInterface(ReturnResultCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {

		super.initialise();
		inPortContentManagement = new InPortContentManagement(contentNodeAddress.getContentManagementURI(), getOwner(),
				getPluginURI());
		inPortContentManagement.publishPort();

	}

	
	/**
	 * Methode appelee si le hop du find est a 0 ou si le contenu est trouve : 
	 * en mode asynchrone on cree directement un port sortant sur le noeud courant pour etablir une connexion vers le client
	 * et transmettre direct l'info du find
	 * 
	 * @param cd : le contentDescriptor qui a ete trouve
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */
	private void returnFind(ContentDescriptorI cd, String uriReturn) throws Exception {
		getOwner().traceMessage("fin find node" + cd + "\n");
		ReturnResultOutPort result = new ReturnResultOutPort(getOwner());
		result.publishPort();
		getOwner().doPortConnection(result.getPortURI(), uriReturn, ConnectorResult.class.getCanonicalName());
		result.returnFind(null);
		result.doDisconnection();
		result.unpublishPort();
		result.destroyPort();
	}

	/**
	 * Methode cherchant si le Template passe en parametre correspond a la Description
	 * de contenu du noeud courant
	 * 
	 * @param cd : le contentTemplate que l'on cherche
	 * @param hops : le nombre de sauts restant avant de mettre un terme a la recherche
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */
	@Override
	public void find(ContentTemplateI cd, int hops, String uriReturn) throws Exception {
		getOwner().traceMessage("start find node" + cd + "\n");

		if (hops == 0) {
			returnFind(null, uriReturn);
			return;
		}
		ContentDescriptorI contentDescriptor;
		if ((contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd)) != null) {
			returnFind(contentDescriptor, uriReturn);
		}
		for (OutPortContentManagement port : connectNodeContent.values()) {
			port.find(cd, hops-1, uriReturn);
		}
		getOwner().traceMessage("fin find node" + cd + "\n");

	}


	/**
	 * Methode cherchant si le Template passe en parametre match avec la Description
	 * de contenu du noeud courant.
	 * 
	 * @param cd : le contentTemplate que l'on compare
	 * @param matched : l'ensemble des ContentDescriptor qui matchent jusqu'a present
	 * @param hops : le nombre de sauts restant avant de mettre un terme a la recherche
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */
	@Override
	public void match(ContentTemplateI cd, Set<ContentDescriptorI> matched, int hops, String uriReturn)
			throws Exception {
		getOwner().traceMessage("start match node" + cd + "\n");

		/*
		 * On ne s'arrete que lorsque le hop arrive a 0. Ici en appel asynchrone on va comme pour le find,
		 * creer une connexion entre le noeud courant et le client ayant fait la requete pour lui transmettre directement
		 * le resultat du match
		 */
		if (hops == 0) {
			ReturnResultOutPort result = new ReturnResultOutPort(getOwner());
			result.publishPort();
			getOwner().doPortConnection(result.getPortURI(), uriReturn, ConnectorResult.class.getCanonicalName());
			result.returnMatch(matched);
			result.doDisconnection();
			result.unpublishPort();
			result.destroyPort();
			return;
		}

		ContentDescriptorI contentDescriptor;
		if ((contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd)) != null
				&& !matched.contains(contentDescriptor)) {
			matched.add(contentDescriptor);
		}
		for (OutPortContentManagement op : connectNodeContent.values()) {
			op.match(cd, matched, hops-1, uriReturn);
		}
		getOwner().traceMessage("fin match node" + cd + "\n");

	}
	
	
	/**
	 * @see ConnectionNodePlugin#connect(PeerNodeAddressI)
	 */
	@Override
	public synchronized OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		if(connectNodeContent.containsKey(peer))
			return null;
		OutPortContentManagement c = super.connect(peer);
		connectNodeContent.put(peer, c);
		return c;
	}
	

	/**
	 * Methode effectuant la deconnexion du noeud courant au noeud passe en parametre
	 * @param peer : noeud pair auquel on se deconnecte
	 * @throws Exception
	 */
	public synchronized void disconnect(PeerNodeAddressI peer) throws Exception {
		if(!connectNodeContent.containsKey(peer))
			return;
		super.disconnect(peer, connectNodeContent.remove(peer));
	}

	/**
	 * @see ConnectionNodePlugin#connectBack(PeerNodeAddressI)
	 */
	@Override
	public synchronized OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		if(connectNodeContent.containsKey(peer))
			return null;
		OutPortContentManagement c = super.connectBack(peer);
		connectNodeContent.put(peer, c);
		return c;
	}

	
	/**
	 * @see ConnectionNodePlugin#disconnectBack(PeerNodeAddressI)
	 */
	@Override
	public synchronized void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if(!connectNodeContent.containsKey(peer))
			return;
		super.disconnectBack(peer);
		OutPortContentManagement portContent = connectNodeContent.remove(peer);
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();
	}
	
	/**
	 * Methode effectuant la deconnexion avec tous les ports de ContentManagement auxquels on est connecte
	 * @throws Exception
	 */
	public synchronized void leave() throws Exception {
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement>  entry : connectNodeContent.entrySet()) {
			disconnect(entry.getKey());
		}
		getOwner().traceMessage("Fin leave \n");
	} 

	/**
	 * @see ConnectionNodePlugin#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().doDisconnection();
		}
		super.finalise();
	}

	/**
	 * @see ConnectionNodePlugin#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		inPortContentManagement.unpublishPort();
		inPortContentManagement.destroyPort();
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		super.uninstall();
	}

}
