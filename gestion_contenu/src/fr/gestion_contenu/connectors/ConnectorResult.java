package fr.gestion_contenu.connectors;

import java.util.Set;

import fr.gestion_contenu.component.interfaces.IReturnResult;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ConnectorResult extends AbstractConnector implements IReturnResult {

	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnFind(ContentDescriptorI)
	 */
	
	@Override
	public void returnFind(ContentDescriptorI cd) throws Exception {
		((IReturnResult) this.offering).returnFind(cd);

	}

	
	
	/**
	 * @see fr.gestion_contenu.component.interfaces.AbstractClientComponent#returnMatch(Set)
	 */
	@Override
	public void returnMatch(Set<ContentDescriptorI> cd) throws Exception {
		((IReturnResult) this.offering).returnMatch(cd);

	}

}