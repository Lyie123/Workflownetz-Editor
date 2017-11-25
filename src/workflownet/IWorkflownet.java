package workflownet;

import javafx.geometry.Point2D;

public interface IWorkflownet extends IDrawable {

    /**
     * Der Knoten n wir dem Workflownetz hinzugefügt. Falls der Knoten bereits existiert,
     * wird der Knoten überschrieben.
     * @param n Knoten der den Workflownetz hinzugefügt werden soll
     * @return gibt die id des hinzugefügten Netzelements zurück
     */
    int add(Node n);

    /**Löscht das Netzelement mit der id aus dem Workflownetz
     * @param id Netelement das aus dem Workflownetz gelöscht werden soll
     */
    void delete(int id);

    /**
     * @param id Netzelement das zurückgegeben werden soll.
     * @return wenn ein Netzelement mit id gefunden wurde gibt das Netzelement zurück,
     *         sonst null
     */
    NetElement get(int id) throws IllegalArgumentException;

    /**
     * Verbindet die zwei Netzelement miteinander. Vorraussetzung ist das die Netzelemente Knoten einen unterschiedlichen Subtype haben
     * @param srcId Id des Quellen Netzelements
     * @param destId Id des Ziel Netzelements
     */
    void connect(int srcId, int destId) throws IllegalArgumentException;

    /**
     * Überprüft ob der Punkt p auf einen Netzelement liegt
     * @param p Punkt überprüft werden soll
     * @return Falls p auf einem Netzelement liegt wird das erste Netzelement zurückgegeben
     *         sonst null
     */
    NetElement get(Point2D p);

    /**Setzt ein Netzelement auf den Zustand "Selected"
     * @param id Netzelement das ausgewählt werden soll
     */
    void triggerNetElement(int id);

    void deselectAllNetElement();

    void deleteAllSelectedNetElement();
}
