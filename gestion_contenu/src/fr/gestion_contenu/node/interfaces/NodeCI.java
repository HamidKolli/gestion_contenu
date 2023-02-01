package fr.gestion_contenu.node.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface NodeCI extends OfferedCI, RequiredCI{
	public PeerNodeAddressI connect(PeerNodeAddressI a) throws Exception;
	public void disconnect(PeerNodeAddressI a) throws Exception;
}
