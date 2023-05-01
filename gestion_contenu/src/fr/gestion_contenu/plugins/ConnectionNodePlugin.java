package fr.gestion_contenu.plugins;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.component.interfaces.AbstractNodeComponent;
import fr.gestion_contenu.connectors.ConnectorNode;
import fr.gestion_contenu.connectors.ConnectorNodeManagement;
import fr.gestion_contenu.node.interfaces.FacadeNodeAddressI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.InPortNode;
import fr.gestion_contenu.ports.NodePortNodeManagement;
import fr.gestion_contenu.ports.OutPortNode;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Plugin s'occupant des differentes operations de connexion et
 *         deconnexion entre noeuds pairs
 */
public class ConnectionNodePlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	private final PeerNodeAddressI nodeAddresses;
	private final ConcurrentMap<PeerNodeAddressI, OutPortNode> connectOutPort;
	private InPortNode connectInPort;
	private NodePortNodeManagement outPortFacade;
	private final String uriConnection;
	private final int id;

	/**
	 * Constructeur
	 * 
	 * @param nodeAddresses : l'addresse du noeud pair concerne
	 * @param uriConnection
	 * @param id 
	 */
	public ConnectionNodePlugin(PeerNodeAddressI nodeAddresses, String uriConnection, int id) {
		super();
		this.id = id;
		this.nodeAddresses = nodeAddresses;
		connectOutPort = new ConcurrentHashMap<>();
		this.uriConnection = uriConnection;

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
		this.connectInPort = new InPortNode(nodeAddresses.getNodeURI(), getOwner(), getPluginURI(), uriConnection);
		this.outPortFacade = new NodePortNodeManagement(getOwner());
		this.outPortFacade.publishPort();
		this.connectInPort.publishPort();
	}

	/**
	 * Methode effectuant la connexion au noeud pair passe en parametre
	 * 
	 * @param peer : le noeud pair auquel se connecter
	 * @return OutPortContentManagement : le port sortant cree pour la connexion
	 * @throws Exception
	 */
	protected void connectNode(PeerNodeAddressI peer) throws Exception {
		getOwner().logMessage("connectNode |connect to " + peer.getNodeIdentifier() + "\n");
		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.connect(nodeAddresses);
		connectOutPort.put(peer, port);
		getOwner().logMessage("connectNode |fin connect to " + peer.getNodeIdentifier() + "\n");

	}

	/**
	 * Methode effectuant la deconnexion au noeud pair passe en parametre
	 * 
	 * @param peer        : le noeud pair auquel se deconnecter
	 * @param portContent : le port sortant du noeud pair qui se deconnecte
	 * @throws Exception
	 */
	protected void disconnect(PeerNodeAddressI peer) throws Exception {
		if (!connectOutPort.containsKey(peer))
			return;
		OutPortNode port = connectOutPort.remove(peer);
		getOwner().logMessage("disconnect | disconnect from " + peer.getNodeIdentifier() + "\n");

		port.disconnect(nodeAddresses);
		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();
		getOwner().logMessage("disconnect | fin disconnect from " + peer.getNodeIdentifier() + "\n");

	}

	/**
	 * Methode appelee suite a un connect afin d'effectuer la connexion dans l'autre
	 * sens
	 * 
	 * @param peer : le noeud pair auquel se conneecter en retour
	 * @return OutPortContentManagement : le port sortant cree pour la connexion
	 * @throws Exception
	 */
	protected void connectBackNode(PeerNodeAddressI peer) throws Exception {
		if (connectOutPort.containsKey(peer))
			return;
		getOwner().logMessage("connectBackNode | connect back to " + peer.getNodeIdentifier() + "\n");

		OutPortNode port = new OutPortNode(getOwner());
		port.publishPort();
		getOwner().doPortConnection(port.getPortURI(), peer.getNodeURI(), ConnectorNode.class.getCanonicalName());
		port.acceptConnected(nodeAddresses);
		connectOutPort.put(peer, port);

		getOwner().logMessage("connectBackNode | fin connect back to " + peer.getNodeIdentifier() + "\n");

	}

	/**
	 * Methode appelee suite a un disconnect afin d'effectuer la deconnexion dans
	 * l'autre sens
	 * 
	 * @param peer : le noeud pair auquel se deconnecter
	 * @throws Exception
	 */
	protected void disconnectBack(PeerNodeAddressI peer) throws Exception {
		if (!connectOutPort.containsKey(peer))
			return;
		getOwner().logMessage("disconnectBack | disconnect back from " + peer.getNodeIdentifier() + "\n");

		OutPortNode port = connectOutPort.remove(peer);

		port.doDisconnection();
		port.unpublishPort();
		port.destroyPort();

		getOwner().logMessage("disconnectBack | fin disconnect back from " + peer.getNodeIdentifier() + "\n");

	}

	public synchronized void probe(int remaingHops, FacadeNodeAddressI facade, String request, int nbVoisin,
			PeerNodeAddressI addressNode) throws Exception {
		getOwner().logMessage("probe | probe node \n");

		if (remaingHops == 0 || connectOutPort.size() == 0) {
			getOwner().doPortConnection(outPortFacade.getPortURI(), facade.getNodeManagementURI(),
					ConnectorNodeManagement.class.getCanonicalName());
			if(addressNode == null || connectOutPort.size() < nbVoisin) {
				outPortFacade.acceptProbed(nodeAddresses, request);
			}else {
				outPortFacade.acceptProbed(addressNode, request);
			}
			outPortFacade.doDisconnection();

			getOwner().logMessage("probe | fin probe node result = " + nodeAddresses + "\n");
			return;
		}

		int rand = (new Random()).nextInt(connectOutPort.size());
		getOwner().logMessage("probe | random " + rand + " nb voisin = " + connectOutPort.size() + "\n");
		int i = 0;
		for (Map.Entry<PeerNodeAddressI, OutPortNode> port : connectOutPort.entrySet()) {
			if (i == rand) {
				if (nbVoisin > connectOutPort.size()) {
					port.getValue().probe(remaingHops - 1, facade, request, connectOutPort.size(), nodeAddresses);
				} else {
					port.getValue().probe(remaingHops - 1, facade, request, nbVoisin, addressNode);
				}
				return;
			}
			i++;
		}
		getOwner().logMessage("probe | fin probe node result = " + nodeAddresses + "\n");
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */
	@Override
	public void finalise() throws Exception {

		getOwner().printExecutionLogOnFile(
				NodeComponent.DIR_LOGGER_NAME + NodeComponent.FILE_LOGGER_NAME + id);
		for (Map.Entry<PeerNodeAddressI, OutPortNode> port : connectOutPort.entrySet())
			port.getValue().doDisconnection();
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		assert connectOutPort.size() == 0;
		for (Map.Entry<PeerNodeAddressI, OutPortNode> port : connectOutPort.entrySet()) {
			port.getValue().unpublishPort();
			port.getValue().destroyPort();
		}
		outPortFacade.unpublishPort();
		outPortFacade.destroyPort();
		connectInPort.unpublishPort();
		connectInPort.destroyPort();
		super.uninstall();
	}

}
