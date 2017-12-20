package workflownet;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.HashMap;

public class Workflownet implements IWorkflownet {
    private HashMap<Integer, Node> _nodeSet = new HashMap<>();
    private boolean _isWorkflownet = false;
    private SimpleStringProperty _actionLog = new SimpleStringProperty();
    private SimpleStringProperty _isWorkflonetMessage = new SimpleStringProperty();

    public SimpleStringProperty actionLog() {
        return _actionLog;
    }

    public SimpleStringProperty isWorkflowNetMessage() {
        return _isWorkflonetMessage;
    }

    @Override
    public int add(Node n) {
        _nodeSet.put(n.getId(), n);
        checkIfWorkflownet();
        return n.getId();
    }

    @Override
    public void delete(int id) throws IllegalArgumentException {
        NetElement buffer = get(id);
        switch (buffer.getType()) {
            case Edge:
                deleteEdge(id);
                break;
            case Place:
            case Transition:
                deleteNode(id);
                break;
        }
        checkIfWorkflownet();
    }

    @Override
    public NetElement get(int id) throws IllegalArgumentException {
        for (NetElement n : getAllNetElements()) {
            if (n.getId() == id) return n;
        }
        throw new IllegalArgumentException("Netzelement konnte nicht gelöscht werden.\n" +
                "Es existiert kein Netzelement mit der id " + id + " im Workflownetz.");
    }

    @Override
    public void connect(int srcId, int destId) throws IllegalArgumentException {
        NetElement e1 = get(srcId);
        NetElement e2 = get(destId);

        String errorMsg;
        if (e1.getType() == NetElementType.Edge || e2.getType() == NetElementType.Edge) {
            errorMsg = "Die Netzelemente können nicht miteinander verbunden werden, " +
                    "da mindestens eines der Elemente eine Kante ist.";
            _actionLog.setValue(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        } else if (e1.getType() == e2.getType()) {
            errorMsg = "Die Netzelemente können nicht miteinander verbunden werden, " +
                    "da die beiden Knoten vom selben Typ sind.";
            _actionLog.setValue(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        ((Node) e1)._outgoingEdges.forEach(edge -> {
            if (edge.getDestination() != null) {
                if (edge.getDestination().getId() == e2.getId()) {
                    _actionLog.setValue("Es besteht bereits eine Verbindung zu diesem Knoten");
                    throw new IllegalArgumentException("Es besteht bereits eine Verbindung zu diesem Knoten");
                }
            }
        });

        //Knoten können miteinander verbunden werden.
        _actionLog.setValue("Knoten mit id " + srcId + " wurde mit Knoten mit id " + destId + " verbunden.");
        ((Node) e1).connectNodeTo((Node) e2);
        checkIfWorkflownet();
    }

    @Override
    public NetElement get(Point2D p) {
        ArrayList<NetElement> buffer = getAllNetElements();

        //Die Liste muss rückwärts durchlaufen werden damit beim klicken auf ein Netzelement immer das zuletzt gezeichnete
        //Netzelement ausgewählt wird.
        for (int i = buffer.size() - 1; i >= 0; --i) {
            if (buffer.get(i).PointLiesOnNetElement(p)) return buffer.get(i);
        }

        return null;
    }

    @Override
    public void triggerNetElement(int id) {
        for (NetElement e : getAllNetElements()) {
            if (e.getId() == id) {
                e.Selected = !e.Selected;
                return;
            }
        }
        throw new IllegalArgumentException("Netzelement konnte nicht selektiert werden. Das Netzelement mit der id "
                + id + " ist nicht Teil des Workflownetzes.");
    }

    @Override
    public void fireTransistion(Point2D p) {
        NetElement netElement = get(p);
        if(netElement instanceof Transition){
            Transition t = (Transition) netElement;
            if(t.isActive()){
               //Entferne alle Token von vorherigen Stellen
               t._incomingEdges.forEach(e -> ((Place)e.getSource()).setToken(false));
               t._outgoingEdges.forEach(e -> ((Place)e.getDestination()).setToken(true));
            }
        }
        return;
    }

    @Override
    public void unselectAllNetElement() {
        getAllNetElements().forEach(e -> e.Selected = false);
    }

    @Override
    public void deleteAllSelectedNetElements() {
        getAllSelectedNetElements().forEach(e -> delete(e.getId()));
    }

    @Override
    public void moveAllSelectedElementsBy(Point2D distance) {
        getAllSelectedNetElements().forEach(e -> {
            if (e.getType() != NetElementType.Edge) {
                Node buffer = (Node) e;
                buffer.setPoint(buffer.getPoint().subtract(distance));
            }
        });
    }

    @Override
    public boolean isWorkflowNet() {
        return _isWorkflownet;
    }

    @Override
    public void reset() {
        _nodeSet.values().forEach(n -> {
            if(n instanceof Place){
                ((Place)n).setToken(false);
                ((Place)n).setStartPlace(false);
                ((Place)n).setEndPlace(false);
            }
            else{
                ((Transition)n).setActive(false);
            }
        });
        checkIfWorkflownet();
    }

    @Override
    public void draw(Canvas canvas) {
        clear(canvas);
        _nodeSet.values().forEach(n -> {
            n.getOutgoingEdges().forEach(e -> e.draw(canvas));
            n.draw(canvas);
        });
    }

    /**
     * Löscht alle Shapes die auf die Canvas gezeichnet wurden
     */
    private void clear(Canvas canvas) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Lösche die Kante mit der id aus dem Workflownetz
     *
     * @param id des Netzelements das gelöscht werden soll
     */
    private void deleteEdge(int id) {
        NetElement netElement = get(id);
        if(netElement.getType() != NetElementType.Edge) return;
        Edge edge = (Edge) netElement;
        edge.getSource()._outgoingEdges.removeIf(e -> e.getDestination() == e.getDestination());
        edge.getDestination()._incomingEdges.removeIf(e -> e.getSource() == edge.getSource());
        /*_nodeSet.values().forEach(n -> {

            n._outgoingEdges.removeIf(e -> e.getId() == id);
        };*/
        checkIfWorkflownet();
    }

    /**
     * Lösche den Knoten mit der id aus dem Workflownetz
     *
     * @param id des Netzelements das gelöscht werden soll
     */
    private void deleteNode(int id) {
        _nodeSet.values().forEach(n -> {
            n._incomingEdges.removeIf(e -> e.getSource().getId() == id);
            n._outgoingEdges.removeIf(e -> e.getDestination().getId() == id);
        });
        _nodeSet.remove(id);
    }

    /**
     * Gibt eine Liste von allen Netzelementen zurück
     *
     * @return Eine Liste von Netzelementen die Teil des Workflownetzes sind
     */
    private ArrayList<NetElement> getAllNetElements() {
        ArrayList<NetElement> netElements = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            netElements.add(n);
            n.getOutgoingEdges().forEach(e -> netElements.add(e));
        });
        return netElements;
    }

    /**
     * Gibt alle selektierten Netzelemente zurück
     *
     * @return Liste von selektierten Netzelementen
     */
    private ArrayList<NetElement> getAllSelectedNetElements() {
        ArrayList<NetElement> buffer = new ArrayList<>();
        getAllNetElements().forEach(e -> {
            if (e.Selected) buffer.add(e);
        });
        return buffer;
    }

    /**
     * Prüft ob es sich um ein Workflownetz handelt.
     * Falls ja setze Anfangsstelle, Endstelle und Anfangstoken.
     * Sonst lösche alle Markierungen.
     */
    private void checkIfWorkflownet() {
        _isWorkflonetMessage.setValue("");

        Place endPlace = checkIfEndPlaceExists();
        Place startPlace = checkIfStartPlaceExists();
        boolean directedPath = checkIfDirectedPathExists();

        if(endPlace != null && startPlace != null && directedPath){
            //Petrinetz ist ein Workflownetz. Setze Workflowspezifische Eigenschaften.
            endPlace.setEndPlace(true);
            startPlace.setStartPlace(true);
            startPlace.setToken(true);
            _isWorkflownet = true;
        }
        else{
            //Workflownnetz besitzt nicht die Eigenschaften eines Workflownetzes
            setNoWorkflowNetPropertys();
            _isWorkflownet = false;
        }
    }
    private void setNoWorkflowNetPropertys(){
        _nodeSet.values().forEach(n -> {
            if(n instanceof Place){
                Place p = (Place)n;
                p._endPlace = false;
                p._startPlace = false;
                p.setToken(false);
            }
            else if(n instanceof Transition){
                Transition t = (Transition)n;
                t.setActive(false);
            }
        });
    }

    private Place checkIfEndPlaceExists(){
        ArrayList<Place> buffer = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            if(n instanceof Place){
                if(n._outgoingEdges.size() == 0) buffer.add((Place)n);
            }
        });
        if(buffer.size() == 1) return buffer.get(0);
        else{
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "\nPetrinetz ist kein Workflownetz: Keine Endstelle vorhanden.");
            return null;
        }
    }
    private Place checkIfStartPlaceExists(){
        ArrayList<Place> buffer = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            if(n instanceof Place){
                if(n._incomingEdges.size() == 0) buffer.add((Place)n);
            }
        });
        if(buffer.size() == 1) return buffer.get(0);
        else{
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "\nPetrinetz ist kein Workflownetz: Keine Anfangstelle vorhanden.");
            return null;
        }
    }
    private boolean checkIfDirectedPathExists(){
        return true;
    }
}