package fr.gestion_contenu.cvm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.gestion_contenu.component.ClientComponent;
import fr.gestion_contenu.component.FacadeComponent;
import fr.gestion_contenu.component.NodeComponent;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.experimentation.Experimentation;
import fr.gestion_contenu.node.informations.ApplicationNodeAddress;
import fr.gestion_contenu.node.informations.ContentNodeAddress;
import fr.gestion_contenu.node.interfaces.ApplicationNodeAddressI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.gestion_contenu.tools.TestReadHashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class CVM extends AbstractCVM {

	public static final int NB_NODES = 50;
	public static final int NB_FACADES = 5;
	public static final int NB_CLIENTS = 22;
	public static final String FILE_NAME = "experimentations";
	public static final Experimentation EXPERIMENTATION = new Experimentation(
			FacadeComponent.NB_THREAD_NODE_MANAGEMENT_FACADE, NodeComponent.NB_THREAD_NODE_MANAGEMENT,
			NodeComponent.NB_THREAD_CONTENT_MANAGEMENT, FacadeComponent.NB_THREAD_CONTENT_MANAGEMENT_FACADE, FILE_NAME);

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {

		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ TimeUnit.SECONDS.toNanos(5);

		Instant startInstant = Instant.parse("2023-02-06T08:00:00Z");

		double accelerationFactor = 60.0;
		String horlogeURI = AbstractPort.generatePortURI();
		AbstractComponent.createComponent(ClocksServer.class.getCanonicalName(),
				new Object[] { horlogeURI, unixEpochStartTimeInNanos, startInstant, accelerationFactor });

		List<ApplicationNodeAddressI> addressFacade = new ArrayList<>();
		for (int i = 0; i < NB_FACADES; i++) {
			addressFacade.add(
					new ApplicationNodeAddress(AbstractPort.generatePortURI(), i + "", AbstractPort.generatePortURI()));
		}

		String uri;

		for (int i = 0; i < NB_FACADES; i++) {
			uri = AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(),
					new Object[] { horlogeURI, addressFacade.get(i), addressFacade.get((i + 1) % NB_FACADES) });
			this.toggleTracing(uri);
			this.toggleLogging(uri);
		}

		ContentDataManager.DATA_DIR_NAME = "data/";
		for (int i = 0; i < NB_NODES; i++) {

			ContentNodeAddressI addressNode = new ContentNodeAddress(AbstractPort.generatePortURI(), i + "",
					AbstractPort.generatePortURI());
			Set<ContentDescriptorI> contentDescriptors = TestReadHashMap.readDescriptors(addressNode, i);
			uri = AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(), new Object[] { horlogeURI,
					contentDescriptors, addressFacade.get(i % NB_FACADES).getNodeManagementURI(), addressNode });
			this.toggleLogging(uri);
		}

		List<ContentTemplateI> tamplates = TestReadHashMap.readTemplate(0);
		int nbRequestClient = tamplates.size() / NB_CLIENTS;
		for (int i = 0; i < NB_CLIENTS; i++) {
			uri = AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
					new Object[] { horlogeURI, addressFacade.get(i % NB_FACADES).getContentManagementURI(),
							tamplates.subList(i * nbRequestClient, (i + 1) * nbRequestClient) });
			this.toggleLogging(uri);
		}

		super.deploy();

	}

	public static void main(String[] args) {

		try {

			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(50000L);
			Thread.sleep(10000L);
			EXPERIMENTATION.writeExperimentations();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

};