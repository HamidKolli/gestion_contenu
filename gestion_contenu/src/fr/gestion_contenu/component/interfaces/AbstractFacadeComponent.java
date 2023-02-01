package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.node.interfaces.NodeCI;
import fr.gestion_contenu.node.interfaces.NodeManagementCI;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = {NodeCI.class})
@OfferedInterfaces(offered = {NodeManagementCI.class,ContentManagementCI.class})
public abstract class AbstractFacadeComponent extends AbstractComponent implements IContentRequest{

	
	protected AbstractFacadeComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}


	
	public abstract Set<PeerNodeAddressI> join(PeerNodeAddressI a) throws Exception;
	public abstract void leave(PeerNodeAddressI a)  throws Exception;
}
