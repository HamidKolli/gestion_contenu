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
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *			implantation distribuee de la CVM
 */
public class DistributedCVM extends AbstractDistributedCVM {

	public static final int NB_NODES = 50;
	public static final int NB_CLIENTS = 22;
	public static final String FILE_NAME = "experimentations";
	public static final Experimentation EXPERIMENTATION = new Experimentation(
			FacadeComponent.NB_THREAD_NODE_MANAGEMENT_FACADE, NodeComponent.NB_THREAD_NODE_MANAGEMENT,
			NodeComponent.NB_THREAD_CONTENT_MANAGEMENT, FacadeComponent.NB_THREAD_CONTENT_MANAGEMENT_FACADE, FILE_NAME);

	public static final String[] JVMFACADES = new String[] { "facade1", "facade2", "facade3", "facade4", "facade5" };
	private String horlogeURI;
	private List<ApplicationNodeAddressI> addressFacade;
	private List<ContentTemplateI> tamplates;

	public DistributedCVM(String[] args) throws Exception {
		super(args);
		addressFacade = new ArrayList<>();
		horlogeURI = AbstractPort.generatePortURI();
		tamplates = TestReadHashMap.readTemplate(0);
	}

	public void deployNodes(int idNode, int idFacade) throws Exception {
		String uri = "";
		ContentNodeAddressI addressNode = new ContentNodeAddress(AbstractPort.generatePortURI(), idNode + "",
				AbstractPort.generatePortURI());
		Set<ContentDescriptorI> contentDescriptors = TestReadHashMap.readDescriptors(addressNode, idNode);
		uri = AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(), new Object[] { horlogeURI,
				contentDescriptors, addressFacade.get(idFacade).getNodeManagementURI(), addressNode });
		this.toggleLogging(uri);
	}

	public void deployClient(int idFacade, int idClient) throws Exception {
		String uri = "";
		uri = AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
				new Object[] { horlogeURI, addressFacade.get(idFacade).getContentManagementURI(),
						tamplates.subList(idClient * (tamplates.size() / NB_CLIENTS),
								(idClient + 1) + (tamplates.size() / NB_CLIENTS)) });
		this.toggleLogging(uri);
	}

	@Override
	public void instantiateAndPublish() throws Exception {

		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
				+ TimeUnit.SECONDS.toNanos(5);

		Instant startInstant = Instant.parse("2023-02-06T08:00:00Z");

		double accelerationFactor = 60.0;

		AbstractComponent.createComponent(ClocksServer.class.getCanonicalName(),
				new Object[] { horlogeURI, unixEpochStartTimeInNanos, startInstant, accelerationFactor });

		for (int i = 0; i < JVMFACADES.length; i++) {
			addressFacade.add(
					new ApplicationNodeAddress(AbstractPort.generatePortURI(), i + "", AbstractPort.generatePortURI()));
		}

		String uri;

		ContentDataManager.DATA_DIR_NAME = "data/";

		int nbNodesParFacade = NB_NODES / JVMFACADES.length;

		int nbClientParFacade = NB_CLIENTS / JVMFACADES.length;

		for (int i = 0; i < JVMFACADES.length; i++) {
			if (super.thisJVMURI.equals(JVMFACADES[i])) {
				
				assert horlogeURI != null;
				assert addressFacade.get(i) != null;
				assert addressFacade.get((i + 1) % JVMFACADES.length) != null;

				uri = AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(), new Object[] {
						 addressFacade.get(i), addressFacade.get((i + 1) % JVMFACADES.length) });
				this.toggleLogging(uri);

				int endLoopNode = nbNodesParFacade * (i + 1);

				for (int j = i * nbNodesParFacade; j < endLoopNode; j++) {
					deployNodes(j, i);
				}
				int endLoopClient = nbClientParFacade * (i + 1);
				for (int j = i * nbClientParFacade; j < endLoopClient; j++) {
					deployClient(i, j);
				}

			}
		}

		if (NB_NODES % JVMFACADES.length > 0 && super.thisJVMURI.equals(JVMFACADES[0])) {
			for (int j = NB_NODES - (NB_NODES % JVMFACADES.length); j < NB_NODES; j++) {
				deployNodes(j, 1);
			}
		}
		if (NB_CLIENTS % JVMFACADES.length > 0 && super.thisJVMURI.equals(JVMFACADES[0])) {
			for (int j = NB_CLIENTS - (NB_CLIENTS % JVMFACADES.length); j < NB_CLIENTS; j++) {
				deployClient(0, j);
			}
		}

		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			ContentDataManager.DATA_DIR_NAME = "../data/";
			DistributedCVM cvm = new DistributedCVM(args);
			cvm.startStandardLifeCycle(60000L);
			Thread.sleep(70000L);
			Experimentation.writeExperimentations();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
