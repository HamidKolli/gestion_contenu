package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class FacadePortNodeManagement extends AbstractInboundPort implements NodeManagementCI{

	private static final long serialVersionUID = 1L;

	public FacadePortNodeManagement(String uri, ComponentI owner)
			throws Exception {
		super(uri,NodeManagementCI.class, owner);
		
	}

	@Override
	public Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception {
		
		return getOwner().handleRequest(new AbstractComponent.AbstractService<Set<PeerNodeAddressI>>() {
			@Override
			public Set<PeerNodeAddressI> call() throws Exception {
				return ((FacadeComponent) getOwner()).join(a);
			}
		});
	}

	@Override
	public void leave(PeerNodeAddressI a) throws Exception{
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((FacadeComponent) getOwner()).leave(a);
				return null;
			}
		});
	}

}
