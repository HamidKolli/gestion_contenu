package fr.gestion_contenu.ports.interfaces;


import fr.gestion_contenu.component.interfaces.IReturnResult;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *			Contrat implante par les ports s'occupant du retour de resultat des requetes
 *			vers le client ayant effectue la requete
 */
public interface ReturnResultCI extends RequiredCI,OfferedCI,IReturnResult{
	

}
