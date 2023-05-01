package fr.gestion_contenu.cvm;

import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {

	public static final String JVMFACADE1 = "facade1";
	public static final String JVMFACADE2 = "facade2";

	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void instantiateAndPublish() throws Exception {

//		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
//				+ TimeUnit.SECONDS.toNanos(5);
//
//		Instant startInstant = Instant.parse("2023-02-06T08:00:00Z");
//
//		double accelerationFactor = 100.0;
//		String horlogeURI = AbstractPort.generatePortURI();
//		AbstractComponent.createComponent(ClocksServer.class.getCanonicalName(),
//				new Object[] { horlogeURI, unixEpochStartTimeInNanos, startInstant, accelerationFactor });
//
//		ApplicationNodeAddressI ana = new ApplicationNodeAddress(AbstractPort.generatePortURI(),
//				AbstractPort.generatePortURI(), AbstractPort.generatePortURI());
//
//		ApplicationNodeAddressI ana2 = new ApplicationNodeAddress(AbstractPort.generatePortURI(),
//				AbstractPort.generatePortURI(), AbstractPort.generatePortURI());
//
//		List<ContentDescriptorI> descriptors = new ArrayList<>(TestReadHashMap.readDescriptors());
//
//		String uri;
//
//		List<ContentTemplateI> templates = new ArrayList<>(TestReadHashMap.readTemplate());
//
//		if (thisJVMURI.equals(JVMFACADE1)) {
//			uri = AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(),
//					new Object[] { horlogeURI, ana, 2, 20, 20, ana2 });
//
//			this.toggleTracing(uri);
//			descriptors = descriptors.subList(0, descriptors.size() / 2);
//
//			for (ContentDescriptorI contentDescriptorI : descriptors) {
//				uri = AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
//						new Object[] { horlogeURI, contentDescriptorI, ana.getNodeManagementURI(), 20, 20 });
//				this.toggleTracing(uri);
//			}
//
//			uri = AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
//					new Object[] { horlogeURI, ana.getContentManagementURI(), templates.get(0) });
//			this.toggleTracing(uri);
//
//		} else if (thisJVMURI.equals(JVMFACADE2)) {
//			uri = AbstractComponent.createComponent(FacadeComponent.class.getCanonicalName(),
//					new Object[] { horlogeURI, ana2, 2, 20, 20, ana });
//
//			this.toggleTracing(uri);
//
//			descriptors = descriptors.subList(descriptors.size() / 2, descriptors.size());
//
//			for (ContentDescriptorI contentDescriptorI : descriptors) {
//				uri = AbstractComponent.createComponent(NodeComponent.class.getCanonicalName(),
//						new Object[] { horlogeURI, contentDescriptorI, ana2.getNodeManagementURI(), 20, 20 });
//				this.toggleTracing(uri);
//			}
//			uri = AbstractComponent.createComponent(ClientComponent.class.getCanonicalName(),
//					new Object[] { horlogeURI, ana2.getContentManagementURI(), templates.get(1) });
//			this.toggleTracing(uri);
//
//		} else {
//			throw new Exception("Unknown JVM URI: " + thisJVMURI);
//		}
//
//		super.instantiateAndPublish();
	}
	
	
	public static void		main(String[] args)
	{
		try {
			DistributedCVM cvm = new DistributedCVM(args);
			cvm.startStandardLifeCycle(60000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
