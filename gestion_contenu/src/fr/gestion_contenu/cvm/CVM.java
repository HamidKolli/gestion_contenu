package fr.gestion_contenu.cvm;

import java.util.Set;

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

public class CVM extends AbstractCVM {
	
	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		
		Set<ContentDescriptorI> descriptors = TestReadHashMap.readDescriptors();
		
		
		ApplicationNodeAddress ana = new ApplicationNodeAddress(AbstractPort.generatePortURI(),AbstractPort.generatePortURI(),
				AbstractPort.generatePortURI(), true, false);
		AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(), new Object[] { ana, 2 });
		
		
		
		for (ContentDescriptorI contentDescriptorI : descriptors) {
			AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
					new Object[] { contentDescriptorI, ana.getNodeManagementURI() });
		}
		
		Set<ContentTemplateI> c = TestReadHashMap.readTemplate();
		for (ContentTemplateI contentTemplateI : c) {
			AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
					new Object[] { ana.getContentManagementURI() ,contentTemplateI});
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