package fr.gestion_contenu.ports;

import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class InPortNode extends AbstractInboundPort implements NodeCI {

	private static final long serialVersionUID = 1L;

	public InPortNode(String uri,ComponentI owner) throws Exception {
		super(uri,NodeCI.class, owner);
	}

	@Override
	public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception {
		return getOwner().handleRequest(new AbstractComponent.AbstractService<PeerNodeAddressI>() {
			@Override
			public PeerNodeAddressI call() throws Exception {
				return ((NodeComponent) getOwner()).connectBack(a);
			}
		});
	}

	@Override
	public void disconnect(PeerNodeAddressI a) throws Exception {
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((NodeComponent) getOwner()).disconnectBack(a);
				return null;
			}
		});

	}

}
