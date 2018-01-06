package workflownet;

import javafx.geometry.Point2D;

import java.util.Observer;

/**
 * Dieses Interface deklariert grundlegende Operationen die ein Workflownetz bereitstellen muss
 */
public interface IWorkflownet extends IDrawable {

    /**
     * Der Knoten n wir dem Workflownetz hinzugefuegt. Falls der Knoten bereits existiert,
     * wird der Knoten überschrieben.
     * @param n Knoten der den Workflownetz hinzugefuegt werden soll
     * @return gibt die id des hinzugefuegten Netzelements zurück
     */
    int add(Node n);

    /**Loescht das Netzelement mit der id aus dem Workflownetz
     * @param id Netelement das aus dem Workflownetz geloescht werden soll
     */
    void delete(int id);

    /**
     * @param id Netzelement das zurueckgegeben werden soll.
     * @return wenn ein Netzelement mit id gefunden wurde gibt das Netzelement zurueck,
     *         sonst null
     */
    NetElement get(int id) throws IllegalArgumentException;

    /**
     * Uberprüft ob der Punkt p auf einen Netzelement liegt
     * @param p Punkt ueberprueft werden soll
     * @return Falls p auf einem Netzelement liegt wird das erste Netzelement zurueckgegeben
     *         sonst null
     */
    NetElement get(Point2D p);

    /**
     * Verbindet die zwei Netzelement miteinander. Vorraussetzung ist das die Netzelemente Knoten einen unterschiedlichen Subtype haben
     * @param srcId Id des Quellen Netzelements
     * @param destId Id des Ziel Netzelements
     */
    void connect(int srcId, int destId) throws IllegalArgumentException;

    /**Triggert den Selektierzustand des Netzelements.
     * Wenn es bereits selektiert war, wird es unselektiert.
     * @param id Netzelement dessen Selektierzustand getriggert werden soll.
     */
    void triggerNetElement(int id);

    /**Versucht das Netzelement an den Positionsdaten p zu schalten. Handelt es sich dabei um keine schaltbare Transition
     * werden keine Aenderungen vorgenommen
     * @param p Netzelement mit Positionsdaten p die geschalten werden soll
     */
    void fireTransistion(Point2D p);

    /**
     * Diese Methode deselektiert alle Netzelemente des Workflownetzes
     */
    void unselectAllNetElement();

    /**
     * Diese Methode entfernt alle Netzelemente aus dem Workflownetz die selektiert sind
     */
    void deleteAllSelectedNetElements();

    /**Diese Methode verschiebt alle selektierten Netzelemente um eine Distanz.
     * @param distance Die Distanz um den alle selektierten Netzelemente verschoben werden sollen.
     */
    void moveAllSelectedElementsBy(Point2D distance);

    /**
     * @return true wenn es sich um ein Workflownetz handelt,
     *         sonst false
     */
    boolean isWorkflowNet();

    /**
     * Setzt das Workflownetz in einen sicheren Anfangszustand.
     * Wenn es sich dabei um ein Workflownetz handelt sollen folgende Operationen ausgeführt werden:
     * -Makiere Anfangsstelle
     * -Makiere Endstelle
     * -Setze Start Token
     */
    void reset();
}
