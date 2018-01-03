package workflownet;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import pnml.PNMLWriter;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

/**
 * test123
 */
public class Workflownet implements IWorkflownet {
    private HashMap<Integer, Node> _nodeSet = new HashMap<>();
    private SimpleBooleanProperty _isWorkflownet = new SimpleBooleanProperty(false);
    private Place _startPlace = null;
    private Place _endPlace = null;

    private final double maxScale = 1.8;
    private final double minScale = 0.5;
    private final double scaleTick = 0.1;

    private ArrayList<Listener> _endPlaceReachedListener = new ArrayList<>();
    public void registerEndPlaceReached(Listener l){
        _endPlaceReachedListener.add(l);
    }
    private ArrayList<Listener> _deadLockListener = new ArrayList<>();
    public void registerDeadLockOccured(Listener l){
        _deadLockListener.add(l);
    }

    private SimpleStringProperty _actionLog = new SimpleStringProperty();
    private SimpleStringProperty _isWorkflonetMessage = new SimpleStringProperty();
    public SimpleStringProperty actionLog() {
        return _actionLog;
    }

    public SimpleStringProperty isWorkflowNetMessage() {
        return _isWorkflonetMessage;
    }

    public void safe(File pnmlDatei) {
        PNMLWriter pnmlWriter = new PNMLWriter(pnmlDatei);
        pnmlWriter.startXMLDocument();

        for (Node n : getAllNodes()) {
            switch (n.getType()) {
                case Transition:
                    Transition t = (Transition) n;
                    pnmlWriter.addTransition(String.valueOf(t.getId()), t.getLabel(),
                            String.valueOf(Math.round(t.getPoint().getX())), String.valueOf(Math.round(t.getPoint().getY())));
                    break;
                case Place:
                    Place p = (Place) n;
                    String tokenString = p.hasToken() ? "1" : "0";
                    pnmlWriter.addPlace(String.valueOf(p.getId()), p.getLabel(),
                            String.valueOf(Math.round(p.getPoint().getX())), String.valueOf(Math.round(p.getPoint().getY())),
                            tokenString);
                    break;
            }



        }
        for (Edge n : getAllEdges()) {
            pnmlWriter.addArc(String.valueOf(n.getId()),
                    String.valueOf(n.getSource().getId()),
                    String.valueOf(n.getDestination().getId()));
        }
        pnmlWriter.finishXMLDocument();
    }
    public static Workflownet open(File path){
        pnml.MyParser p = new pnml.MyParser(path);
        Workflownet w = p.CreateWorkflow();
        if(w.checkIfWorkflownet()){
            ArrayList<Integer> tokenIds = p.getTokens();
            if(tokenIds.size() > 0) {
                w.getAllPlaces().forEach(n -> n.setToken(false));
                p.getTokens().forEach(t -> {
                    w.getAllPlaces().forEach(n -> {
                        if (n.getId() == t) {
                            n.setToken(true);
                        }
                    });
                });
            }
        }
        return w;
    }

    @Override
    public int add(Node n) {
        _actionLog.setValue("Knoten mit id " + n.getId() + " wurde hinzgefügt.");
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
        _actionLog.setValue("Knoten mit der id " + id + " wurde entfernt.");
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
            if(t.fireTransition()) checkIfSafeWorkflownet();
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
                if(buffer.getPoint().getX() < 0) buffer.setPoint(new Point2D(0, buffer.getPoint().getY()));
                if(buffer.getPoint().getY() < 0) buffer.setPoint(new Point2D(buffer.getPoint().getX(), 0));
            }
        });
    }

    @Override
    public boolean isWorkflowNet() {
        return _isWorkflownet.getValue();
    }

    public SimpleBooleanProperty isWorkflownetProperty() {
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
                ((Transition)n).checkForContact();
            }
        });
        checkIfWorkflownet();
    }

    @Override
    public void draw(Pane canvas) {
        clear(canvas);
        _nodeSet.values().forEach(n -> {
            n.getOutgoingEdges().forEach(e -> e.draw(canvas));
            n.draw(canvas);
        });
    }

    public void scalePositive(){
        if(maxScale <= NetElement.Scale) return;
        NetElement.Scale+=scaleTick;
    }

    public void scaleNegative(){
        if(minScale >= NetElement.Scale) return;
        NetElement.Scale-=scaleTick;
    }

    /**
     * Löscht alle Shapes die auf die Canvas gezeichnet wurden
     */
    private void clear(Pane canvas) {
        canvas.getChildren().clear();
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
    private ArrayList<Node> getAllNodes(){
        ArrayList<Node> buffer = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            if(n.getType() == NetElementType.Transition || n.getType() == NetElementType.Place){
                buffer.add(n);
            }
        });
        return buffer;
    }
    private ArrayList<Edge> getAllEdges(){
        ArrayList<Edge> buffer = new ArrayList<>();
        getAllNetElements().forEach(n ->{
            if(n.getType() == NetElementType.Edge) buffer.add((Edge) n);
        });
        return buffer;
    }
    private ArrayList<Transition> getAllTransitions(){
        ArrayList<Transition> buffer = new ArrayList<>();
        getAllNodes().forEach(n ->{
            if(n.getType() == NetElementType.Transition) buffer.add((Transition)n);
        });
        return buffer;
    }
    private ArrayList<Place> getAllPlaces(){
        ArrayList<Place> buffer = new ArrayList<>();
        getAllNodes().forEach(n ->{
            if(n.getType() == NetElementType.Place) buffer.add((Place)n);
        });
        return buffer;
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
    private boolean checkIfWorkflownet() {
        _isWorkflonetMessage.setValue("");
        _endPlace = null;
        _startPlace = null;

        _endPlace = checkIfEndPlaceExists();
        _startPlace = checkIfStartPlaceExists();

        //Prüfe ob jeder Knoten von der Eingangsstelle aus erreichbar ist.
        //Prüfe ob jeder Knoten auf einem Pfad zur Endstelle liegt.
        boolean directedPath = checkIfDirectedPathExists();

        if(_endPlace != null && _startPlace != null && directedPath){
            //Petrinetz ist ein Workflownetz. Setze Workflowspezifische Eigenschaften.
            _endPlace.setEndPlace(true);
            _startPlace.setStartPlace(true);
            _startPlace.setToken(true);
            _isWorkflownet.setValue(true);
            return true;
        }
        else{
            //Workflownnetz besitzt nicht die Eigenschaften eines Workflownetzes
            setNoWorkflowNetPropertys();
            _isWorkflownet.setValue(false);
            return false;
        }
    }
    public void checkIfSafeWorkflownet(){
        if(!isWorkflowNet()) return;
        ArrayList<Transition> transitions = getAllTransitions();
        transitions.forEach(n -> n.checkForContact());

        if(checkIfEndMarkIsReached()){
            _endPlaceReachedListener.forEach(n -> n.handle());
        }

        else{
            boolean isDeadlock = true;
            for(Transition t : transitions){
                if(t.canFire()){
                    isDeadlock = false;
                    break;
                }
            }
            if(isDeadlock){
                _deadLockListener.forEach(n -> n.handle());
            }
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
        else if(buffer.size() == 0){
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Petrinetz ist kein Workflownetz: Keine Endstelle vorhanden.\n");
            return null;
        }
        else{
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Petrinetz ist kein Workflownetz: Mehr als eine Endstelle vorhanden.\n");
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
        else if(buffer.size() == 0){
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Petrinetz ist kein Workflownetz: Keine Anfangstelle vorhanden.\n");
            return null;
        }
        else{
            _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Petrinetz ist kein Workflownetz: Mehr als eine Anfangstelle vorhanden.\n");
            return null;
        }
    }
    private boolean checkIfDirectedPathExists(){
        if(_startPlace == null || _endPlace == null) return false;
        ArrayList<Integer> buffer = new ArrayList<>();
        getAllNodes().forEach(n -> buffer.add(n.getId()));
        boolean isConnectedGraph = checkIfAllNodesGetVisited(_startPlace, buffer);
        boolean everyNodeIsOnPathToEndPlace = true;

        if(_endPlace != null && _startPlace != null){
            for (Node n: getAllNodes()) {
                HashSet<Integer> s = new HashSet<>();
                if(!checkIfNodeIsReachable(n, _endPlace, s)){
                    everyNodeIsOnPathToEndPlace = false;
                    break;
                }
            }
        }
        else everyNodeIsOnPathToEndPlace = false;

        if(isConnectedGraph && everyNodeIsOnPathToEndPlace) return true;
        if(!isConnectedGraph) _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Nicht alle Knoten sind von der Anfangsstelle erreichbar.\n");
        if(!everyNodeIsOnPathToEndPlace) _isWorkflonetMessage.setValue(_isWorkflonetMessage.getValue() + "Nicht jeder Knoten liegt auf einem Pfad zur Endstelle.\n");
        return false;
    }
    private boolean checkIfEndMarkIsReached(){
        for(Place p : getAllPlaces()){
            if(p == _endPlace){
                if(!p.hasToken()) return false;
            }
            else{
                if(p.hasToken()) return false;
            }
        }
        return true;
    }

    private boolean checkIfAllNodesGetVisited(Node n, ArrayList<Integer> notVisitedNodes){
        if(!notVisitedNodes.contains(n.getId())) return true;
        notVisitedNodes.removeIf(e -> e == n.getId());
        if(notVisitedNodes.size() == 0) return true;
        Stack<Node> stack = new Stack<>();
        n._outgoingEdges.forEach(e -> stack.push(e.getDestination()));
        while((!stack.empty())){
            checkIfAllNodesGetVisited(stack.pop(), notVisitedNodes);
        }
        if(notVisitedNodes.size() == 0) return true;
        return false;
    }
    private boolean checkIfNodeIsReachable(Node n, Node dest, HashSet<Integer> visitedNodes){
        if(visitedNodes.contains(n.getId())) return false;
        else visitedNodes.add(n.getId());

        if(n.getId() == dest.getId()) return true;
        Stack<Node> stack = new Stack<>();
        n._outgoingEdges.forEach(e -> stack.push(e.getDestination()));
        while(!stack.empty()){
            if(checkIfNodeIsReachable(stack.pop(), dest, visitedNodes)) return true;
        }
        return false;
    }
}