package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.component.interfaces.IConnectFacadeRequest;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *			Contrat implante par les ports s'occupant de la connexion entre les noeuds (ports sortants)
 *			et les interfaces (ports entrants) de la facade
 */
public interface NodeManagementCI extends OfferedCI, RequiredCI ,IConnectFacadeRequest{
	
}
