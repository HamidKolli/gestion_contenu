package fr.gestion_contenu.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes operations de connexion et deconnexion 
 *         entre noeuds pairs
 */
public class ConnectionNodePlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	private PeerNodeAddressI nodeAddresses;
	private ConcurrentMap<PeerNodeAddressI, OutPortNode> connectOutPort;
	private InPortNode connectInPort;

	/**
	 * Constructeur
	 * @param nodeAddresses : l'addresse du noeud pair concerne
	 */
	public ConnectionNodePlugin(PeerNodeAddressI nodeAddresses) {
		super();
		this.nodeAddresses = nodeAddresses;
		connectOutPort = new ConcurrentHashMap<>();
	}

	/**
	 * 
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 *
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		assert owner instanceof AbstractNodeComponent;
		super.installOn(owner);
		this.addRequiredInterface(NodeCI.class);
		this.addOfferedInterface(NodeCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		super.initialise();
		this.connectInPort = new InPortNode(nodeAddresses.getNodeURI(), getOwner(), getPluginURI());

		this.connectInPort.publishPort();

	}

	/**
	 * Methode effectuant la connexion au noeud pair passe en parametre
	 * @param peer : le noeud pair auquel se connecter
	 * @return OutPortContentManagement : le port sortant cree pour la connexion
	 * @throws Exception
	 */
	protected OutPortContentManagement connect(PeerNodeAddressI peer) throws Exception {
		getOwner().traceMessage("connect to " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.connect(nodeAddresses);
		connectOutPort.put(peer, port);

		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());
		getOwner().traceMessage("connect reussi " + peer.getNodeIdentifier() + "\n");
		return portContent;
	}

	/**
	 * Methode effectuant la deconnexion au noeud pair passe en parametre
	 * @param peer : le noeud pair auquel se deconnecter
	 * @param portContent : le port sortant du noeud pair qui se deconnecte
	 * @throws Exception
	 */
	protected void disconnect(PeerNodeAddressI peer, OutPortContentManagement portContent) throws Exception {
		if(!connectOutPort.containsKey(peer))
			return;
		OutPortNode port = connectOutPort.remove(peer);
		getOwner().traceMessage("disconnect from " + peer.getNodeIdentifier() + "\n");
		port.disconnect(nodeAddresses);
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();

		portContent.doDisconnection();
		portContent.unpublishPort();
		portContent.destroyPort();

		getOwner().traceMessage("fin disconnect " + peer.getNodeIdentifier() + "\n");

	}

	/**
	 * Methode appelee suite a un connect afin d'effectuer la connexion dans l'autre sens
	 * @param peer : le noeud pair auquel se conneecter en retour
	 * @return OutPortContentManagement : le port sortant cree pour la connexion
	 * @throws Exception
	 */
	protected OutPortContentManagement connectBack(PeerNodeAddressI peer) throws Exception {
		if(connectOutPort.containsKey(peer))
			return null;
		getOwner().traceMessage("connect back to " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		connectOutPort.put(peer, port);
		OutPortContentManagement portContent = new OutPortContentManagement(getOwner());
		portContent.publishPort();
		getOwner().doPortConnection(portContent.getPortURI(), ((ContentNodeAddressI) peer).getContentManagementURI(),
				ConnectorContentManagement.class.getCanonicalName());

		getOwner().traceMessage("fin connect back to " + peer.getNodeIdentifier() + "\n");
		return portContent;

	}

	/**
	 * Methode appelee suite a un disconnect afin d'effectuer la deconnexion dans l'autre sens
	 * @param peer : le noeud pair auquel se deconnecter
	 * @throws Exception
	 */
	protected void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if(!connectOutPort.containsKey(peer))
			return;
		getOwner().traceMessage("disconnect back from " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = connectOutPort.remove(peer);
		
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();
		getOwner().traceMessage("fin disconnect back from " + peer.getNodeIdentifier() + "\n");

	}

	
	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++)
			ports.get(i).getValue().doDisconnection();
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		List<Map.Entry<PeerNodeAddressI, OutPortNode>> ports = new ArrayList<>(connectOutPort.entrySet());
		for (int i = 0; i < ports.size(); i++) {
			ports.get(i).getValue().unpublishPort();
			ports.get(i).getValue().destroyPort();
		}
		connectInPort.unpublishPort();
		connectInPort.destroyPort();
		super.uninstall();
	}

}
