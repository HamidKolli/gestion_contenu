package fr.gestion_contenu.cvm;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.ClientComponent;
import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.tools.TestReadHashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class CVM extends AbstractCVM {
	
	public CVM() throws Exception {
		super();
	}
	

	@Override
	public void deploy() throws Exception {
		
		long unixEpochStartTimeInNanos =
				TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ TimeUnit.SECONDS.toNanos(5);
		
		Instant	startInstant = Instant.parse("2023-02-06T08:00:00Z");
		
		double accelerationFactor = 100.0;
		String horlogeURI = AbstractPort.generatePortURI();
		AbstractComponent.createComponent(ClocksServer.class.getCanonicalName(), new Object[] {horlogeURI,unixEpochStartTimeInNanos,startInstant,accelerationFactor});
		
		
		ApplicationNodeAddress ana = new ApplicationNodeAddress(AbstractPort.generatePortURI(),AbstractPort.generatePortURI(),
				AbstractPort.generatePortURI(), true, false);
		String uri = AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(), new Object[] { horlogeURI,ana, 2 });
		this.toggleTracing(uri);
		
		Set<ContentDescriptorI> descriptors = TestReadHashMap.readDescriptors();
		
		for (ContentDescriptorI contentDescriptorI : descriptors) {
			uri = AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
					new Object[] { horlogeURI,contentDescriptorI, ana.getNodeManagementURI() });
			this.toggleTracing(uri);
		}
		
		Set<ContentTemplateI> c = TestReadHashMap.readTemplate();
		for (ContentTemplateI contentTemplateI : c) {
			uri = AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
					new Object[] { horlogeURI,ana.getContentManagementURI() ,contentTemplateI});
			this.toggleTracing(uri);
		}
		

		super.deploy();
		
	}

	public static void main(String[] args) {
		
		try {
			TestReadHashMap.readDescriptors();
			
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(20000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

};