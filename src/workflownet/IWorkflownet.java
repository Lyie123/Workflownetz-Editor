package workflownet;

import javafx.geometry.Point2D;

public interface IWorkflownet extends IDrawable {

    /**
     * Der Knoten n wir dem Workflownetz hinzugefügt. Falls der Knoten bereits existiert,
     * wird der Knoten überschrieben.
     * @param n Knoten der den Workflownetz hinzugefügt werden soll
     */
    void add(Node n);

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
     * Sucht nach einen Netzelement abhängig von der Position p und gibt es zurück
     * @param p Position die überprüft werden soll, ob es auf einen Netzelement liegt
     * @return wenn p auf einen Netzelement liegt, gib dieses Element zurück,
     *         sonst null
     */
    NetElement get(Point2D p);

    /**
     * Verbindet die zwei Netzelement miteinander. Vorraussetzung ist das die Netzelemente Knoten einen unterschiedlichen Subtype haben
     * @param srcId Id des Quellen Netzelements
     * @param destId Id des Ziel Netzelements
     */
    void connect(int srcId, int destId) throws IllegalArgumentException;
}
