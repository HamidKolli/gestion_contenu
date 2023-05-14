package fr.gestion_contenu.experimentation;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Hamid KOLLI
 * @author Yanis ALAYOUD
 *
 *	Classe effectuant les experimentations (sur le nb de threads/voisins etc...) afin de les ecrire dans un tableau 
 */
public class Experimentation {
	private static int nbThreadContentManagementFacade;
	private static int nbThreadNodeManagementFacade;
	private static int nbThreadNodeManagementNode;
	private static int nbThreadContentManagementNode;

	private static int nbVoisinMax;
	private static int nbVoisinMin;
	private static double moyNbVoisin;

	private static int nbReqFailed;
	private static int nbReqFindSuccess;
	private static long timeMaxReq;
	private static long timeMinReq;
	private static double timeMoyReq;

	private static String filename;

	/**
	 * Constructeur
	 * 
	 * @param nbThreadNodeManagementFacade : nombre de threads qui gerent les noeuds cote facade
	 * @param nbThreadNodeManagementNode : nombre de threads qui gerent les noeuds cote noeud
	 * @param nbThreadContentManagement : nombre de threads qui gerent les contenus cote noeud
	 * @param nbThreadContentManagementFacade : nombre de threads qui gerent les contenus cote facade
	 * @param filename : le nom du fichier ou on stock les experimentations
	 */
	public Experimentation(int nbThreadNodeManagementFacade, int nbThreadNodeManagementNode,
			int nbThreadContentManagement, int nbThreadContentManagementFacade, String filename) {
		super();
		Experimentation.nbThreadNodeManagementFacade = nbThreadNodeManagementFacade;
		Experimentation.nbThreadNodeManagementNode = nbThreadNodeManagementNode;
		Experimentation.nbThreadContentManagementNode = nbThreadContentManagement;
		Experimentation.filename = filename;
		Experimentation.nbThreadContentManagementFacade = nbThreadContentManagementFacade;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbVoisinMax() {
		return nbVoisinMax;
	}

	/**
	 * setter
	 * 
	 * @param nbVoisinMax
	 */
	public static void setNbVoisinMax(int nbVoisinMax) {
		Experimentation.nbVoisinMax = nbVoisinMax;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbVoisinMin() {
		return nbVoisinMin;
	}

	/**
	 * setter
	 * 
	 * @param nbVoisinMin
	 */
	public static void setNbVoisinMin(int nbVoisinMin) {
		Experimentation.nbVoisinMin = nbVoisinMin;
	}

	/**
	 * getter
	 * 
	 * @return double
	 */
	public static double getMoyNbVoisin() {
		return moyNbVoisin;
	}

	/**
	 * setter
	 * 
	 * @param moyNbVoisin
	 */
	public static void setMoyNbVoisin(double moyNbVoisin) {
		Experimentation.moyNbVoisin = moyNbVoisin;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbReqFailed() {
		return nbReqFailed;
	}

	/**
	 * setter
	 * 
	 * @param nbReqFaile
	 */
	public static void setNbReqFailed(int nbReqFaile) {
		nbReqFailed = nbReqFaile;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbReqFindSuccess() {
		return nbReqFindSuccess;
	}

	/**
	 * setter
	 * 
	 * @param nbReqFindSuccess
	 */
	public static void setNbReqFindSuccess(int nbReqFindSuccess) {
		Experimentation.nbReqFindSuccess = nbReqFindSuccess;
	}

	/**
	 * getter
	 * 
	 * @return long
	 */
	public static long getTimeMaxReq() {
		return timeMaxReq;
	}

	/**
	 * setter
	 * 
	 * @param timeMaxReq
	 */
	public static void setTimeMaxReq(long timeMaxReq) {
		Experimentation.timeMaxReq = timeMaxReq;
	}

	/**
	 * getter
	 * 
	 * @return long
	 */
	public static long getTimeMinReq() {
		return timeMinReq;
	}

	/**
	 * setter
	 * 
	 * @param timeMinReq
	 */
	public static void setTimeMinReq(long timeMinReq) {
		Experimentation.timeMinReq = timeMinReq;
	}

	/**
	 * getter
	 * 
	 * @return double
	 */
	public static double getTimeMoyReq() {
		return timeMoyReq;
	}

	/**
	 * setter
	 * 
	 * @param timeMoyReq
	 */
	public static void setTimeMoyReq(double timeMoyReq) {
		Experimentation.timeMoyReq = timeMoyReq;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbThreadNodeManagementFacade() {
		return nbThreadNodeManagementFacade;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbThreadNodeManagementNode() {
		return nbThreadNodeManagementNode;
	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbThreadContentManagement() {
		return nbThreadContentManagementNode;
	}

	public static final String DIR_NAME = "experimentation/";
	public static final String EXTENSION = ".md";

	public static final String NB_THREAD_CONTENT_MANAGEMENT_FACADE = "Threads CM Facade";
	public static final String NB_THREAD_CONTENT_MANAGEMENT = "Threads CM Node";
	public static final String NB_THREAD_NODE_MANAGEMENT_FACADE = "Threads NM Facade";
	public static final String NB_THREAD_NODE_MANAGEMENT_NODE = "Threads NM Peer";

	public static final String NB_VOISIN_MAX = "Max Voisin";
	public static final String NB_VOISIN_MIN = "Mis Voisin";
	public static final String VOISIN_MOY = "Moyenne voisins";

	public static final String NB_REQ_FAILED = "Requettes rate";
	public static final String NB_REQ_SUCCESS = "Requette Trouvee (find)";
	public static final String TIME_REQ_MAX = "Temps Req max (ms)";
	public static final String TIME_REQ_MIN = "Temps Req min (ms)";
	public static final String TIME_REQ_MOY = "Temps moyen (ms)";

	/**
	 * methode entete : ecris dans le fichier les intitules de chaque colonnes du tableau
	 * 
	 * @param file
	 */
	private static void entete(File file) {

		try (FileWriter bw = new FileWriter(file, true)) {
			bw.write(String.format(
					"%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s\n:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:\n",
					NB_THREAD_CONTENT_MANAGEMENT_FACADE, NB_THREAD_CONTENT_MANAGEMENT, NB_THREAD_NODE_MANAGEMENT_FACADE,
					NB_THREAD_NODE_MANAGEMENT_NODE, NB_VOISIN_MAX, NB_VOISIN_MIN, VOISIN_MOY, NB_REQ_FAILED,
					NB_REQ_SUCCESS, TIME_REQ_MAX, TIME_REQ_MIN, TIME_REQ_MOY));

		} catch (Exception e) {

		}

	}

	/**
	 * methode writeExperimentations : ecris une nouvelle ligne dans le fichier correspondant aux valeurs testees
	 */
	public static void writeExperimentations() {

		File file = new File(DIR_NAME + filename + EXTENSION);

		if (!file.exists()) {
			entete(file);
		}

		try (FileWriter bw = new FileWriter(file, true)) {

			bw.write(String.format("%d|%d|%d|%d|%d|%d|%f|%d|%d|%d|%d|%f\n", getNbThreadContentManagementFacade(),
					getNbThreadContentManagement(), getNbThreadNodeManagementFacade(), getNbThreadNodeManagementNode(),
					getNbVoisinMax(), getNbVoisinMin(), getMoyNbVoisin(), getNbReqFailed(), getNbReqFindSuccess(),
					getTimeMaxReq(), getTimeMinReq(), getTimeMoyReq()));
			System.out.println("Exp ecrite");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * getter
	 * 
	 * @return int
	 */
	public static int getNbThreadContentManagementFacade() {
		return nbThreadContentManagementFacade;
	}

}
