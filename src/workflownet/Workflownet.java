package workflownet;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import pnml.PNMLWriter;

import java.io.File;
import java.util.*;

/**
 * Diese Klasse repraesentiert ein Workflownetz, welches aus Knoten besteht die ueber Kanten miteinander verbunden sind.
 */
public class Workflownet implements IWorkflownet {
    /**
     * Adjazenliste des Workflownetz Graphen. Der Schluessel ist die id des Knotens. Der Knoten ist der wert.
     */
    private HashMap<Integer, Node> _nodeSet = new HashMap<>();
    /**
     * Legt fest ob es sich um ein Workflownetz handelt.
     */
    private SimpleBooleanProperty _isWorkflownet = new SimpleBooleanProperty(false);
    /**
     * Referenz auf die Startstelle des Workflownetzes. Wenn keine Startstelle vorhanden ist der Wert null.
     */
    private Place _startPlace = null;
    /**
     * Referenz auf die Startstelle des Workflownetzes. Wenn keine Startstelle vorhanden ist der Wert null.
     * */
    private Place _endPlace = null;

    /**
     * Legt den Skalierungfaktor fest der nicht überschritten werden kann.
     */
    private final double maxScale = 1.8;
    /**
     * Legt den Skalierungsfaktor fest der nicht unterschritten werden darf.
     */
    private final double minScale = 0.5;
    /**
     * Schrittgröße mit den die Netzelemente skaliert werden können.
     */
    private final double scaleTick = 0.1;

    /**
     * Listener der seine Abonnenten informiert wenn die reguläre Endmarkierung erreicht wurde
     */
    private ArrayList<Listener> _endPlaceReachedListener = new ArrayList<>();
    /**Listener der seine Abonnenten informiert wenn die reguläre Endmarkierung erreicht wurde
     * @param l Listener der informiert wird wenn Ereignis eintritt
     */
    public void registerEndPlaceReached(Listener l){
        _endPlaceReachedListener.add(l);
    }

    /**
     *Listener der seine Abonnenten informiert wenn ein Deadlock innerhalb des Workflownetzes aufgetreten ist
     */
    private ArrayList<Listener> _deadLockListener = new ArrayList<>();
    /**Listener der seine Abonnenten informiert wenn ein Deadlock innerhalb des Workflownetzes aufgetreten ist
     * @param l Listener der informiert wird wenn Ereignis eintritt
     */
    public void registerDeadLockOccured(Listener l){
        _deadLockListener.add(l);
    }

    /**
     * Enthält die letzte Aktion die im Workflownetz ausgeführt wurde. Wird aktuell nicht benutzt.
     */
    private SimpleStringProperty _actionLog = new SimpleStringProperty();
    /**
     * Enthält Informationen warum das Workflownetz kein Workflownetz ist.
     */
    private SimpleStringProperty _isWorkflonetMessage = new SimpleStringProperty();
    /**
     * Enthält die letzte Aktion die im Workflownetz ausgeführt wurde. Wird aktuell nicht benutzt.
     */
    public SimpleStringProperty actionLog() {
        return _actionLog;
    }

    /**
     * @return gibt den Grund zurueck warum es sich um kein Workflownetz handelt. Falls es sich um ein
     * Workflownetz handelt ist der String leer.
     */
    public SimpleStringProperty isWorkflowNetMessage() {
        return _isWorkflonetMessage;
    }

    /**
     * @return gibt true zurueck wenn es sich um ein Workflownetz handelt
     *         sonst false
     */
    public SimpleBooleanProperty isWorkflownetProperty() {
        return _isWorkflownet;
    }

    /**Speichert das Workflownetz in eine pnml Datei auf den Rechner
     * @param file Datei in die das Workflownetz geschrieben werden soll
     */
    public void safe(File file) {
        PNMLWriter pnmlWriter = new PNMLWriter(file);
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

    /**Ladet ein in eine PNML Datei gespeichertes Workflownetz und gibt es zurueck
     * @param file PNML File aus den ein Workflownetz erzeugt werden soll
     * @return gibt das Workflow Objekt das aus der pnml Datei erzeugt wurde zurück
     */
    public static Workflownet open(File file){
        pnml.MyParser p = new pnml.MyParser(file);
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
        //Durchlaufe erst alle Knoten damit keine Kanten zurückgegeben werden, obwohl Kante von Knoten überdeckt wird.

        ArrayList<Node> buffer = getAllNodes();

        //Die Liste muss rückwärts durchlaufen werden damit beim klicken auf ein Netzelement immer das zuletzt gezeichnete
        //Netzelement ausgewählt wird.
        for (int i = buffer.size() - 1; i >= 0; --i) {
            if (buffer.get(i).PointLiesOnNetElement(p)) return buffer.get(i);
        }

        //Überprüfe ob auf Kante geklickt wurde
        ArrayList<Edge> edges = getAllEdges();
        for (int i = edges.size() - 1; i >= 0; --i) {
            if (edges.get(i).PointLiesOnNetElement(p)) return edges.get(i);
        }

        //Es wurde kein Netzelement angeklickt
        return null;
    }

    @Override
    public void triggerNetElement(int id) {
        for (NetElement e : getAllNetElements()) {
            if (e.getId() == id) {
                e.setSelected(!e.isSelected());
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
        getAllNetElements().forEach(e -> e.setSelected(false));
    }

    @Override
    public void deleteAllSelectedNetElements() {
        getAllEdges().forEach(n -> {
            if(n.isSelected()) delete(n.getId());
        });
        getAllNodes().forEach(n-> {
            if(n.isSelected()) delete(n.getId());
        });
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
        getAllEdges().forEach(e -> e.draw(canvas));
        getAllNodes().forEach(n -> n.draw(canvas));
    }

    /**
     * Vergroeßert alle Netzelemente um das 0,1fache
     */
    public void scalePositive(){
        if(maxScale <= NetElement.Scale) return;
        NetElement.Scale+=scaleTick;
    }

    /**
     * Verkleinert alle Netzelement um das 0,1fache
     */
    public void scaleNegative(){
        if(minScale >= NetElement.Scale) return;
        NetElement.Scale-=scaleTick;
    }

    /**
     * Löscht alle Netzelemente die auf die Zeichenflaeche gezeichnet wurden
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
     * Gibt eine Liste von allen Netzelementen zurueck, die Teil des Workflownetzes sind
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
     * Gibt eine Liste von allen Knoten zurueck, die Teil des Workflownetzes sind
     *
     * @return Eine Liste von Knoten die Teil des Workflownetzes sind
     */
    private ArrayList<Node> getAllNodes(){
        ArrayList<Node> buffer = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            if(n.getType() == NetElementType.Transition || n.getType() == NetElementType.Place){
                buffer.add(n);
            }
        });
        return buffer;
    }
    /**
     * Gibt eine Liste von allen Kanten zurueck, die Teil des Workflownetzes sind
     *
     * @return Eine Liste von Kanten die Teil des Workflownetzes sind
     */
    private ArrayList<Edge> getAllEdges(){
        ArrayList<Edge> buffer = new ArrayList<>();
        getAllNetElements().forEach(n ->{
            if(n.getType() == NetElementType.Edge) buffer.add((Edge) n);
        });
        return buffer;
    }
    /**
     * Gibt eine Liste von allen Transitionen zurueck, die Teil des Workflownetzes sind
     *
     * @return Eine Liste von Transitinen die Teil des Workflownetzes sind
     */
    private ArrayList<Transition> getAllTransitions(){
        ArrayList<Transition> buffer = new ArrayList<>();
        getAllNodes().forEach(n ->{
            if(n.getType() == NetElementType.Transition) buffer.add((Transition)n);
        });
        return buffer;
    }
    /**
     * Gibt eine Liste von allen Stellen zurueck, die Teil des Workflownetzes sind
     *
     * @return Eine Liste von Stellen die Teil des Workflownetzes sind
     */
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
            if (e.isSelected()) buffer.add(e);
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
    /**
     * Prüft ob es sich um ein Sicheres Workflownetz handelt. Falls nicht benachritige alle Listener.
     * Prüft ob die reguläre Endmarkierung erreicht wurde. Falls ja werden alle Listener benachrichtigt.
     */
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
    /**
     * Entfernt alle Markierungen von den Stellen und setzt alle Transitionen auf den Zustand nicht schaltbar.
     */
    private void setNoWorkflowNetPropertys(){
        _nodeSet.values().forEach(n -> {
            if(n instanceof Place){
                Place p = (Place)n;
                p.setStartPlace(false);
                p.setEndPlace(false);
                p.setToken(false);
            }
            else if(n instanceof Transition){
                Transition t = (Transition)n;
                t.setActive(false);
            }
        });
    }

    /**Uberprüft ob das potenzielle Workflownetz genau eine Endstelle besitzt
     * @return gibt die Endstelle zurueck falls eine existiert
     *         sonst null
     */
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
    /**Uberprüft ob das potenzielle Workflownetz genau eine Anfangsstelle besitzt
     * @return gibt die Anfangsstelle zurueck falls eine existiert
     *         sonst null
     */
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

    /**Ueberprueft ob ein Pfad von der Anfangstelle zur Endstelle im Workflownetz existiert.
     * Vorrausetzung ist dass das Workflownetz eine Anfangs- und Endstelle besitzt.
     * @return gibt true zurueck wenn ein Pfad existiert,
     *         sonst false
     */
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

    /**
     * @return gibt true zurueck wenn die regulaere Endmarkierung erreicht wurde
     */
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

    /**Ueberprueft ob jeder Knoten von der Starstelle aus erreichbar ist. Die Suche erfolgt rekursiv ueber Tiefensuche.
     * @param n aktuell besuchter Knoten
     * @param notVisitedNodes Enthält alle Knoten die noch nicht besucht wurden.
     * @return gibt true zurueck alle Knoten des Workflownetz von der Startstelle aus erreichbar ist
     *         sonst false
     */
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

    /**Ueberprueft ob der Knoten dest vom Startpunkt n aus erreichbar ist
     * @param n Starknoten von den aus versucht werden soll Knoten dest zu erreichen
     * @param dest Zielknoten der versucht werden soll zu erreichen
     * @param visitedNodes Liste mit Knoten die bereits besucht wurden
     * @return gibt true zurueck wenn Knoten dest auf einem Pfad von Knoten n liegt
     *         false sonst
     */
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