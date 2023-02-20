package fr.gestion_contenu.component.interfaces;

import java.util.Set;

import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.gestion_contenu.ports.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.interfaces.NodeCI;
import fr.gestion_contenu.ports.interfaces.NodeManagementCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = { NodeCI.class })
@OfferedInterfaces(offered = { NodeManagementCI.class, ContentManagementCI.class })
/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 * Classe abstraite qui gere les facades
 */
public abstract class AbstractFacadeComponent extends AbstractComponent {

	protected AbstractFacadeComponent(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}
	
}
