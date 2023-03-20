package fr.gestion_contenu.ports.interfaces;

import fr.gestion_contenu.component.interfaces.IContentRequestFacade;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ContentManagementClientCI extends OfferedCI,RequiredCI, IContentRequestFacade {

}
