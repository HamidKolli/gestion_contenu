package fr.gestion_contenu.cvm;

import java.util.HashSet;

import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.content.classes.ContentDescriptor;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.node.informations.ContentNodeAdress;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		ApplicationNodeAddress ana = new ApplicationNodeAddress(AbstractPort.generatePortURI(), "facadeVald",
				AbstractPort.generatePortURI(), true, false);
		AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(), new Object[] { ana, 2 });
		
		ContentNodeAddressI peer = new ContentNodeAdress(AbstractPort.generatePortURI(), "nodeVALD",
				AbstractPort.generatePortURI(), true, false);
		ContentDescriptorI c = new ContentDescriptor("V.A.L.S.E", "NQNTMQMB", new HashSet<>(), new HashSet<>(), peer,
				300);
		
		AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
				new Object[] { c, ana.getNodeManagementURI() });

		ContentNodeAddressI peer2 = new ContentNodeAdress(AbstractPort.generatePortURI(), "nodeVALD2",
				AbstractPort.generatePortURI(), true, false);
		ContentDescriptorI c2 = new ContentDescriptor("Rappel", "NQNTMQMB", new HashSet<>(), new HashSet<>(), peer2,
				250);
		
		AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
				new Object[] { c2, ana.getNodeManagementURI() });
		
		

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(20000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

};