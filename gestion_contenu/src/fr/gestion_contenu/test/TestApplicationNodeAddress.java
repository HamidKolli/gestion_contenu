package fr.gestion_contenu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fr.gestion_contenu.node.informations.ApplicationNodeAddress;

public class TestApplicationNodeAddress {
	
	private ApplicationNodeAddress address;
	
	@Before
	public void begin() {
		address = new ApplicationNodeAddress("A", "B", "C");
	}
	
	@Test
	public void test() {
		assertEquals(address.getNodeManagementURI(), "A");
		assertEquals(address.getNodeIdentifier(), "B");
		assertEquals(address.getContentManagementURI(), "C");
		assertTrue(address.isFacade());
		assertFalse(address.isPeer());
	}
}
