package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = { NodeCI.class, ContentManagementCI.class, NodeManagementCI.class })
@OfferedInterfaces(offered = { ContentManagementCI.class, NodeCI.class })
public abstract class AbstractNodeComponent extends AbstractComponent implements IContentRequest {

	protected AbstractNodeComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}

	public abstract void connect(PeerNodeAddressI peer) throws Exception;
	public abstract PeerNodeAddressI connectBack(PeerNodeAddressI peer) throws Exception;

	public abstract void disconnect(PeerNodeAddressI peer) throws Exception;
	public abstract void disconnectBack(PeerNodeAddressI peer) throws Exception;

	public abstract void leave() throws Exception;

	public abstract Set<PeerNodeAddressI> join() throws Exception;
}
