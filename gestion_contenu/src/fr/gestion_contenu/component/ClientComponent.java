package fr.gestion_contenu.component;

import java.util.HashSet;
import java.util.Set;

import fr.gestion_contenu.component.interfaces.AbstractClientComponent;
import fr.gestion_contenu.connectors.ConnectorContentManagement;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.content_management.interfaces.ContentManagementCI;
import fr.gestion_contenu.ports.OutPortContentManagement;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { ContentManagementCI.class })
public class ClientComponent extends AbstractClientComponent {
	private ContentTemplateI template;
	private String uriContentManagementFacade;
	private OutPortContentManagement portContentManagement;

	protected ClientComponent(String uriContentManagementFacade,ContentTemplateI template) throws Exception {
		super(1, 0);
		this.uriContentManagementFacade = uriContentManagementFacade;
		this.template = template;
	}

	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.portContentManagement = new OutPortContentManagement(this);
			this.portContentManagement.publishPort();
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	@Override
	public ContentDescriptorI find(ContentTemplateI template) throws Exception {
		return this.portContentManagement.find(template, 1);

	}

	@Override
	public Set<ContentDescriptorI> match(ContentTemplateI template) throws Exception {
		return this.portContentManagement.match(template,new HashSet<>(), 1);

	}

	@Override
	public void execute() throws Exception {
		
		Thread.sleep(2000L);
		
		doPortConnection(this.portContentManagement.getPortURI(), this.uriContentManagementFacade,
				ConnectorContentManagement.class.getCanonicalName());
		
		
		ContentDescriptorI descriptor = find(template);
		System.out.println("client find template = " + template + " result = " + descriptor);
		Set<ContentDescriptorI> descriptors = match(template);
		System.out.println("match start" + template);
		for (ContentDescriptorI contentDescriptorI : descriptors) {
			System.out.println("match : " + contentDescriptorI);
		}

		System.out.println("fin client");
		super.execute();
	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(this.portContentManagement.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.portContentManagement.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
