package fr.gestion_contenu.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.gestion_contenu.content.classes.ContentDescriptor;
import fr.gestion_contenu.content.classes.ContentTemplate;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.informations.ContentNodeAdress;
import fr.sorbonne_u.components.AbstractPort;

public class TestReadHashMap {

	public static Set<ContentDescriptorI> readDescriptors() {

		Set<ContentDescriptorI> descriptors = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			try (FileInputStream f = new FileInputStream("data/descriptors" + i + ".dat")) {
				try (ObjectInputStream of = new ObjectInputStream(f)) {
					HashMap<String, Object> data = (HashMap<String, Object>) (of.readObject());

					descriptors.add(new ContentDescriptor(((String) data.get("title")),
							(String) data.get("album-title"), 
							new HashSet<>((List) data.get("interpreters")),
							new HashSet<>((List) data.get("composers")),
							new ContentNodeAdress(AbstractPort.generatePortURI(), AbstractPort.generatePortURI(),
									AbstractPort.generatePortURI(), false, true),
							(Long)data.get("size")));
				} catch (IOException | ClassNotFoundException e) {

					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return descriptors;
	}
	
	public static Set<ContentTemplateI> readTemplate() {

		Set<ContentTemplateI> descriptors = new HashSet<>();
		for (int i = 0; i < 2; i++) {
			try (FileInputStream f = new FileInputStream("data/templates" + i + ".dat")) {
				try (ObjectInputStream of = new ObjectInputStream(f)) {
					HashMap<String, Object> data = (HashMap<String, Object>) (of.readObject());
					
					List<String> interpreters = (List<String>) data.get("interpreters");
					List<String> composers = (List<String>) data.get("composers");
					
					descriptors.add(new ContentTemplate(((String) data.get("title")),
							(String) data.get("album-title"), 
							interpreters == null? null : new HashSet<>(interpreters),
							composers == null? null : new HashSet<>(composers)));
				} catch (IOException | ClassNotFoundException e) {

					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return descriptors;
	}
}
