package pnml;

import javafx.geometry.Point2D;
import workflownet.Node;
import workflownet.Place;
import workflownet.Transition;
import workflownet.Workflownet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * PNML Parser das aus PNML Dateien Workflownet Objekte erzeugt
 */
public class MyParser extends PNMLParser{
    /**
     * Dieser Konstruktor erstellt einen neuen Parser für PNML Dateien,
     * dem die PNML Datei als Java {@link File} übergeben wird.
     *
     * @param pnml Java {@link File} Objekt der PNML Datei
     */
    public MyParser(File pnml) {
        super(pnml);
    }

    /**
     * Diese Methode kann überschrieben werden, um die Markierung der geladenen
     * Elemente zu aktualisieren.
     *
     * @param id      Identifikationstext des Elements
     * @param marking
     */
    @Override
    public void setMarking(String id, String marking) {
        if(marking.matches("1")){
            _tokenId.add(_idMap.get(id));
        }
    }

    /**
     * Diese Methode kann überschrieben werden, um den Beschriftungstext der geladenen
     * Elemente zu aktualisieren.
     *
     * @param id   Identifikationstext des Elements
     * @param name
     */
    @Override
    public void setName(String id, String name) {
        ((Node)_workflowNet.get(_idMap.get(id))).setLabel(name);
    }

    /**
     * Diese Methode kann überschrieben werden, um die Positionen der geladenen
     * Elemente zu aktualisieren.
     *
     * @param id Identifikationstext des Elements
     * @param x  x Position des Elements
     * @param y
     */
    @Override
    public void setPosition(String id, String x, String y) {
        ((Node)_workflowNet.get(_idMap.get(id))).setPoint(new Point2D(Double.parseDouble(x), Double.parseDouble(y)));
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Stellen zu erstellen.
     *
     * @param id Identifikationstext der Stelle
     */
    @Override
    public void newPlace(String id) {
        _idMap.put(id, _workflowNet.add(new Place("")));
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Kanten zu erstellen.
     *
     * @param id     Identifikationstext der Edge
     * @param source Identifikationstext des Startelements der Edge
     * @param target
     */
    @Override
    public void newArc(String id, String source, String target) {
        _workflowNet.connect(_idMap.get(source), _idMap.get(target));
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Transitionen zu erstellen.
     *
     * @param id Identifikationstext der Transition
     */
    @Override
    public void newTransition(String id) {
        _idMap.put(id, _workflowNet.add(new Transition("")));
    }

    /**
     * @return gibt alle id der Stellen zurueck die markiert sind
     */
    public ArrayList<Integer> getTokens(){
        return _tokenId;
    }

    /**
     * @return Gibt das geparste Workflownetz zurück
     */
    public Workflownet CreateWorkflow(){
        _workflowNet = new Workflownet();
        _idMap = new HashMap<>();
        _tokenId = new ArrayList<>();
        this.initParser();
        this.parse();
        return _workflowNet;
    }

    /**
     * Workflownetz das durch den Parser erzeugt und zurückgegeben wird.
     */
    private Workflownet _workflowNet;
    /**
     * Mapped die Id in der Workflownetz Datei mit der Id des ersellten Workflownetzes.
     */
    private HashMap<String, Integer> _idMap;
    /**
     * Speichert alle id der Stellen die markiert sind
     */
    private ArrayList<Integer> _tokenId;
}
