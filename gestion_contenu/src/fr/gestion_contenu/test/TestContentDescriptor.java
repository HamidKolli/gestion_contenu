package fr.gestion_contenu.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import fr.gestion_contenu.content.classes.ContentDescriptor;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.node.informations.ContentNodeAddress;


/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 * 
 * Classe de tests Unitaires JUNIT
 *
 */
public class TestContentDescriptor {
	private ContentDescriptorI descriptor;

	@Before
	public void begin() {
		
		HashSet<String> comp = new HashSet<>();
		comp.add("f");
		comp.add("g");
		comp.add("b");
		
		HashSet<String> inter = new HashSet<>();
		inter.add("x");
		inter.add("b");
		inter.add("e");
		
		
		
		descriptor = new ContentDescriptor("A", "B", comp, inter, new ContentNodeAddress("A", "B", "C"), 300);
	}

	@Test
	public void test1() {
		HashSet<String> comp = new HashSet<>();
		comp.add("g");
		comp.add("f");
		comp.add("b");
		
		HashSet<String> inter = new HashSet<>();
		inter.add("e");
		inter.add("b");
		inter.add("x");
		
		
		ContentDescriptorI descriptor2 = new ContentDescriptor("A", "B", comp, inter, new ContentNodeAddress("A", "B", "C"), 300);
	
		assertTrue(descriptor.equals(descriptor2));
	}
	
	@Test
	public void test2() {
		HashSet<String> comp = new HashSet<>();
		comp.add("g");

		comp.add("b");
		
		HashSet<String> inter = new HashSet<>();
		inter.add("e");

		
		
		ContentDescriptorI descriptor2 = new ContentDescriptor("A",null, comp, inter, new ContentNodeAddress("A", "B", "C"), 300);
	
		assertTrue(descriptor.match(descriptor2));
	}
	
	
	@Test
	public void test3() {
		HashSet<String> comp = new HashSet<>();
		comp.add("ge");

		comp.add("ba");
		
		HashSet<String> inter = new HashSet<>();


		ContentDescriptorI descriptor2 = new ContentDescriptor("K",null, comp, inter, new ContentNodeAddress("A", "B", "C"), 300);
	
		assertFalse(descriptor.match(descriptor2));
	}
}
