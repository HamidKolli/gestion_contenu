package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IReturnResult;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Classe qui represente le port sortant d'un noeud pour renvoyer
 *         les resultats des requetes find et match au client ayant effectue la requete
 */
public class ReturnResultOutPort extends AbstractOutboundPort implements ReturnResultCI {

	/**
	 * serialVersionUID : de type long gere
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 * 
	 * @param owner : Le composant possedant le port
	 * @throws Exception
	 */
	public ReturnResultOutPort(ComponentI owner) throws Exception {
		super(ReturnResultCI.class, owner);

	}

	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnFind(ContentDescriptorI)
	 */
	@Override
	public void returnFind(ContentDescriptorI cd) throws Exception {
		((IReturnResult) getConnector()).returnFind(cd);

	}
	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnMatch(Set)
	 */
	@Override
	public void returnMatch(Set<ContentDescriptorI> cd) throws Exception {
		((IReturnResult) getConnector()).returnMatch(cd);
	}

}
