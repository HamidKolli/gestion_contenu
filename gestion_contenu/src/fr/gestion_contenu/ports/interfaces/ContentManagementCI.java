package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.component.interfaces.IContentRequest;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;


/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *			Contrat implante par les ports de ContentManagement entrants et sortants
 */
public interface ContentManagementCI extends OfferedCI,RequiredCI,IContentRequest{

}
