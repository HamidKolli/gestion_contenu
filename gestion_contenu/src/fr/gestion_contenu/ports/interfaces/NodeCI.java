package fr.gestion_contenu.ports.interfaces;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IConnectNodeRequest;
import fr.gestion_contenu.node.interfaces.PeerNodeAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *			Contrat implante par les ports entrants et sortants qui s'occupent des 
 *			connexions entre les noeuds du reseau
 */
public interface NodeCI extends OfferedCI, RequiredCI,IConnectNodeRequest, ProbingCI{
	
}
