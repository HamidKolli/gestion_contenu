package fr.gestion_contenu.ports;

import java.util.Set;

import fr.gestion_contenu.component.ClientComponent;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.ports.interfaces.ReturnResultCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ReturnResultInPort extends AbstractInboundPort implements ReturnResultCI {

	/**
	 * serialVersionUID : de type long gere
	 */
	private static final long serialVersionUID = 1L;

	public ReturnResultInPort( ComponentI owner) throws Exception {
		super(ReturnResultCI.class, owner);
	}

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
