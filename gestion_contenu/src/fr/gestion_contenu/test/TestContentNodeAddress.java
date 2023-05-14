package fr.gestion_contenu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fr.gestion_contenu.node.informations.ContentNodeAddress;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;


/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Classe de tests Unitaires JUNIT
 *
 */
public class TestContentNodeAddress {

	
	private ContentNodeAddressI contentNodeAddress;
	
	@Before
	public void begin() {
		contentNodeAddress = new ContentNodeAddress("A", "B", "C");
	}
	
	@Test
	public void test() {
		assertEquals(contentNodeAddress.getNodeURI(), "A");
		assertEquals(contentNodeAddress.getNodeIdentifier(), "B");
		assertEquals(contentNodeAddress.getContentManagementURI(), "C");
		assertFalse(contentNodeAddress.isFacade());
		assertTrue(contentNodeAddress.isPeer());
	}
}
