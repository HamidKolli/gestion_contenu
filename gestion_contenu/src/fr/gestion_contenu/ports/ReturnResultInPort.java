package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.ClientComponent;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;


/**
 * 
 * @author Hamid KOLLI && Yanis ALAYOUD
 *
 *         Classe qui represente le port entrant d'un client pour recuperer
 *         les resultats des requetes find et match
 */
public class ReturnResultInPort extends AbstractInboundPort implements ReturnResultCI {

	/**
	 * serialVersionUID : de type long gere
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 * 
	 * @param owner : le Composant qui possede le port
	 * @throws Exception
	 */
	public ReturnResultInPort( ComponentI owner) throws Exception {
		super(ReturnResultCI.class, owner);
	}

	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnFind(ContentDescriptorI)
	 */
	@Override
	public void returnFind(ContentDescriptorI cd) throws Exception {
		getOwner().runTask(a -> {
			try {
				((ClientComponent)getOwner()).returnFind(cd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}
	
	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnMatch(Set)
	 */
	@Override
	public void returnMatch(Set<ContentDescriptorI> matched)
			throws Exception {
		getOwner().runTask(a -> {
			try {
				((ClientComponent)getOwner()).returnMatch(matched);
			} catch (Exception e) {
			}
		});

	}

}
