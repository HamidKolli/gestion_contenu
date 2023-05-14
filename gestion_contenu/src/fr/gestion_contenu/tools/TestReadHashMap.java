package fr.gestion_contenu.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.gestion_contenu.content.classes.ContentDescriptor;
import fr.gestion_contenu.content.classes.ContentTemplate;
import fr.gestion_contenu.content.interfaces.ContentDescriptorI;
import fr.gestion_contenu.content.interfaces.ContentTemplateI;
import fr.gestion_contenu.node.interfaces.ContentNodeAddressI;
import fr.sorbonne_u.cps.p2Pcm.dataread.ContentDataManager;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *         Classe aui permet de lire les fichiers test et de retourner
 *         l'ensemble des Descriptors et Templates pour ensuite creer les
 *         composants dans la CVM
 */
public class TestReadHashMap {

	/**
	 * Methode statique qui lis les fichiers descriptors test et retourne l'ensemble
	 * des ContentDescriptors trouves
	 * 
	 * @return Set ContentDescriptorI : l'ensemble des Descriptions lues
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static Set<ContentDescriptorI> readDescriptors(ContentNodeAddressI contentNodeAddressI, int number)
			throws ClassNotFoundException, IOException {

		ArrayList<HashMap<String, Object>> descriptorsMaps = ContentDataManager.readDescriptors(number);
		Set<ContentDescriptorI> descriptors = new HashSet<>();
		for (HashMap<String, Object> descM : descriptorsMaps) {
			descriptors.add(new ContentDescriptor(((String) descM.get("title")), (String) descM.get("album-title"),
					new HashSet<>((List<String>) descM.get("interpreters")),
					new HashSet<>((List<String>) descM.get("composers")), contentNodeAddressI,
					(Long) descM.get("size")));
		}
		return descriptors;

	}

	/**
	 * Methode statique qui lis les fichiers templates test et retourne l'ensemble
	 * des ContentTemplates trouves
	 * 
	 * @return Set ContentDescriptorI  : l'ensemble des Templates lus
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static List<ContentTemplateI> readTemplate(int number) throws ClassNotFoundException, IOException {

		ArrayList<HashMap<String, Object>> descriptorsMaps = ContentDataManager.readTemplates(number);
		List<ContentTemplateI> descriptors = new ArrayList<>();
		for (HashMap<String, Object> descM : descriptorsMaps) {
			descriptors.add(new ContentTemplate(((String) descM.get("title")), (String) descM.get("album-title"),
					new HashSet<>((List<String>) descM.get("interpreters")),
					new HashSet<>((List<String>) descM.get("composers"))));
		}
		return descriptors;
	}

}
