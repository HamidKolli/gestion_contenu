package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorContentManagementFacade;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.NodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortContentManagementFacade;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.FacadeContentManagementCI;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes operations de connexion,
 *         deconnexion, match et find liees aux connexions de ContentManagement
 */
public class ContentManagementPlugin extends ConnectionNodePlugin {
	private static final int NBV_REQ = 2;
	private static final long serialVersionUID = 1L;
	private ConcurrentMap<PeerNodeAddressI, OutPortContentManagement> connectNodeContent;
	private final ContentNodeAddressI contentNodeAddress;
	private InPortContentManagement inPortContentManagement;
	private OutPortContentManagementFacade result;
	private final String uriContentManagement;

	/**
	 * Constructeur
	 * 
	 * @param contentNodeAddress   : l'addresse du noeud en question
	 * @param uriContentManagement
	 * @param uriConnection
	 * @param id 
	 */
	public ContentManagementPlugin(ContentNodeAddressI contentNodeAddress, String uriConnection,
			String uriContentManagement, int id) {
		super(contentNodeAddress, uriConnection,id);
		connectNodeContent = new ConcurrentHashMap<>();
		this.contentNodeAddress = contentNodeAddress;
		this.uriContentManagement = uriContentManagement;

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		assert owner instanceof AbstractNodeComponent;
		super.installOn(owner);
		this.addOfferedInterface(ContentManagementCI.class);
		this.addRequiredInterface(ContentManagementCI.class);
		this.addRequiredInterface(FacadeContentManagementCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {

		super.initialise();
		inPortContentManagement = new InPortContentManagement(contentNodeAddress.getContentManagementURI(), getOwner(),
				getPluginURI(), uriContentManagement);
		inPortContentManagement.publishPort();
		result = new OutPortContentManagementFacade(getOwner());
		result.publishPort();

	}

	/**
	 * Methode appelee si le hop du find est a 0 ou si le contenu est trouve : en
	 * mode asynchrone on cree directement un port sortant sur le noeud courant pour
	 * etablir une connexion vers le client et transmettre direct l'info du find
	 * 
	 * @param cd        : le contentDescriptor qui a ete trouve
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */
	private void returnFind(ContentDescriptorI cd, String uriReturn, String requestURI) throws Exception {

		getOwner().doPortConnection(result.getPortURI(), uriReturn,
				ConnectorContentManagementFacade.class.getCanonicalName());
		result.acceptFound(cd, requestURI);
		result.doDisconnection();

	}

	/**
	 * Methode cherchant si le Template passe en parametre correspond a la
	 * Description de contenu du noeud courant
	 * 
	 * @param cd        : le contentTemplate que l'on cherche
	 * @param hops      : le nombre de sauts restant avant de mettre un terme a la
	 *                  recherche
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */

	public void find(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI) throws Exception {
		getOwner().logMessage("find | start find node tamplate = " + cd + "\n");

		assert facade.isFacade();

		List<ContentDescriptorI> contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd);
		if (contentDescriptor.size() > 0) {
			returnFind(contentDescriptor.get(0), ((ApplicationNodeAddressI) facade).getContentManagementURI(),
					requestURI);
		}

		if (hops == 0) {
			returnFind(null, ((ApplicationNodeAddressI) facade).getContentManagementURI(), requestURI);
			return;
		}

		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeContent.values());
		Collections.shuffle(listTmp);
		for (OutPortContentManagement port : listTmp.subList(0, Math.min(listTmp.size(), NBV_REQ))) {
			port.find(cd, hops - 1, facade, requestURI);
		}

	}

	/**
	 * Methode cherchant si le Template passe en parametre match avec la Description
	 * de contenu du noeud courant.
	 * 
	 * @param cd        : le contentTemplate que l'on compare
	 * @param matched   : l'ensemble des ContentDescriptor qui matchent jusqu'a
	 *                  present
	 * @param hops      : le nombre de sauts restant avant de mettre un terme a la
	 *                  recherche
	 * @param uriReturn : l'uri du client ayant fait la requete
	 * @throws Exception
	 */

	public void match(ContentTemplateI cd, int hops, NodeAddressI facade, String requestURI,
			Set<ContentDescriptorI> matched) throws Exception {
		assert facade.isFacade();
		getOwner().logMessage("match | start match node tamplate = "+cd+"\n");

		List<ContentDescriptorI> contentDescriptor = ((AbstractNodeComponent) getOwner()).match(cd);
		if (contentDescriptor.size() > 0 && !matched.containsAll(contentDescriptor)) {
			matched.addAll(contentDescriptor);
		}

		/*
		 * On ne s'arrete que lorsque le hop arrive a 0. Ici en appel asynchrone on va
		 * comme pour le find, creer une connexion entre le noeud courant et le client
		 * ayant fait la requete pour lui transmettre directement le resultat du match
		 */
		if (hops == 0) {
			getOwner().doPortConnection(result.getPortURI(),
					((ApplicationNodeAddressI) facade).getContentManagementURI(),
					ConnectorContentManagementFacade.class.getCanonicalName());
			result.acceptMatched(matched, requestURI);
			result.doDisconnection();

			return;
		}

		List<OutPortContentManagement> listTmp = new ArrayList<>(connectNodeContent.values());
		Collections.shuffle(listTmp);
		for (OutPortContentManagement op : listTmp.subList(0, Math.min(listTmp.size(), NBV_REQ))) {
			op.match(cd, hops - 1, facade, requestURI, matched);
		}

	}

	/**
	 * @see ConnectionNodePlugin#connect(PeerNodeAddressI)
	 */

	public synchronized void connect(PeerNodeAddressI peer) throws Exception {
		if (connectNodeContent.containsKey(peer))
			return;
		super.connectNode(peer);
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		getOwner().traceMessage("connect reussi " + peer.getNodeIdentifier() + "\n");

		connectNodeContent.put(peer, portContent);
	}

	/**
	 * Methode effectuant la deconnexion du noeud courant au noeud passe en
	 * parametre
	 * 
	 * @param peer : noeud pair auquel on se deconnecte
	 * @throws Exception
	 */
	public synchronized void disconnect(PeerNodeAddressI peer) throws Exception {
		if (!connectNodeContent.containsKey(peer))
			return;
		super.disconnect(peer);

		OutPortContentManagement portContent = connectNodeContent.remove(peer);
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();

	}

	/**
	 * @see ConnectionNodePlugin#connectBack(PeerNodeAddressI)
	 */

	public synchronized OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		if (connectNodeContent.containsKey(peer))
			return null;
		super.connectBackNode(peer);
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());

		connectNodeContent.put(peer, portContent);
		return portContent;
	}

	/**
	 * @see ConnectionNodePlugin#disconnectBack(PeerNodeAddressI)
	 */
	@Override
	public synchronized void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if (!connectNodeContent.containsKey(peer))
			return;
		super.disconnectBack(peer);
		OutPortContentManagement portContent = connectNodeContent.remove(peer);
		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();
	}

	/**
	 * Methode effectuant la deconnexion avec tous les ports de ContentManagement
	 * auxquels on est connecte
	 * 
	 * @throws Exception
	 */
	public synchronized void leave() throws Exception {
		getOwner().logMessage("leave \n");
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> entry : connectNodeContent.entrySet()) {
			disconnect(entry.getKey());
		}
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
		result.unpublishPort();
		result.destroyPort();
		for (Map.Entry<PeerNodeAddressI, OutPortContentManagement> port : connectNodeContent.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		super.uninstall();
	}

	public void acceptNeighbours(Set<PeerNodeAddressI> neighbours) {
		((AbstractNodeComponent) getOwner()).acceptNeighbours(neighbours);
	}

	public void acceptConnected(PeerNodeAddressI neighbour) {
		getOwner().logMessage("acceptConnected | Accept connect " + neighbour.getNodeIdentifier() + "\n");
	}

}
