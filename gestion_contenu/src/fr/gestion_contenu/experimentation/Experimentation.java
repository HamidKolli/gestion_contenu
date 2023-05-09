package fr.gestion_contenu.experimentation;

import java.io.File;
import java.io.FileWriter;

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

	public Experimentation(int nbThreadNodeManagementFacade, int nbThreadNodeManagementNode,
			int nbThreadContentManagement, int nbThreadContentManagementFacade, String filename) {
		super();
		Experimentation.nbThreadNodeManagementFacade = nbThreadNodeManagementFacade;
		Experimentation.nbThreadNodeManagementNode = nbThreadNodeManagementNode;
		Experimentation.nbThreadContentManagementNode = nbThreadContentManagement;
		Experimentation.filename = filename;
		Experimentation.nbThreadContentManagementFacade = nbThreadContentManagementFacade;
	}

	public static int getNbVoisinMax() {
		return nbVoisinMax;
	}

	public static void setNbVoisinMax(int nbVoisinMax) {
		Experimentation.nbVoisinMax = nbVoisinMax;
	}

	public static int getNbVoisinMin() {
		return nbVoisinMin;
	}

	public static void setNbVoisinMin(int nbVoisinMin) {
		Experimentation.nbVoisinMin = nbVoisinMin;
	}

	public static double getMoyNbVoisin() {
		return moyNbVoisin;
	}

	public static void setMoyNbVoisin(double moyNbVoisin) {
		Experimentation.moyNbVoisin = moyNbVoisin;
	}

	public static int getNbReqFailed() {
		return nbReqFailed;
	}

	public static void setNbReqFailed(int nbReqFaile) {
		nbReqFailed = nbReqFaile;
	}

	public static int getNbReqFindSuccess() {
		return nbReqFindSuccess;
	}

	public static void setNbReqFindSuccess(int nbReqFindSuccess) {
		Experimentation.nbReqFindSuccess = nbReqFindSuccess;
	}

	public static long getTimeMaxReq() {
		return timeMaxReq;
	}

	public static void setTimeMaxReq(long timeMaxReq) {
		Experimentation.timeMaxReq = timeMaxReq;
	}

	public static long getTimeMinReq() {
		return timeMinReq;
	}

	public static void setTimeMinReq(long timeMinReq) {
		Experimentation.timeMinReq = timeMinReq;
	}

	public static double getTimeMoyReq() {
		return timeMoyReq;
	}

	public static void setTimeMoyReq(double timeMoyReq) {
		Experimentation.timeMoyReq = timeMoyReq;
	}

	public static int getNbThreadNodeManagementFacade() {
		return nbThreadNodeManagementFacade;
	}

	public static int getNbThreadNodeManagementNode() {
		return nbThreadNodeManagementNode;
	}

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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static int getNbThreadContentManagementFacade() {
		return nbThreadContentManagementFacade;
	}

}
