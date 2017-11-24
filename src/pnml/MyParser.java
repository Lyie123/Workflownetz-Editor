package pnml;

import javafx.geometry.Point2D;
import workflownet.VisualPlace;
import workflownet.VisualTransition;
import workflownet.VisualWorkflowNet;
import workflownet.WorkflowNet;

import java.io.File;
import java.util.HashMap;

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
        //todo Marke setzen noch nicht implementiert
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
        _workflowNet.getNode(_idMap.get(id)).setLabel(name);
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
        _workflowNet.getNode(_idMap.get(id)).getVisual().setPoint(new Point2D(Double.parseDouble(x), Double.parseDouble(y)));
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Stellen zu erstellen.
     *
     * @param id Identifikationstext der Stelle
     */
    @Override
    public void newPlace(String id) {
        _idMap.put(id, _workflowNet.addNode(new VisualPlace("", 0, 0)));
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
        //todo id von kante wird nicht abgespeichert
        _workflowNet.connectNodes(_idMap.get(source), _idMap.get(target));
    }

    /**
     * Diese Methode kann überschrieben werden, um geladene Transitionen zu erstellen.
     *
     * @param id Identifikationstext der Transition
     */
    @Override
    public void newTransition(String id) {
        _idMap.put(id, _workflowNet.addNode(new VisualTransition("", 0, 0)));
    }

    /**
     * @return Gibt das geparste Workflownetz zurück
     */
    public VisualWorkflowNet CreateWorkflow(){
        _workflowNet = new VisualWorkflowNet();
        _idMap = new HashMap<>();
        this.initParser();
        this.parse();
        return _workflowNet;
    }

    /**
     * Workflownetz das durch den Parser erzeugt und zurückgegeben wird.
     */
    private VisualWorkflowNet _workflowNet;
    /**
     * Mapped die Id in der Workflownetz Datei mit der Id des ersellten Workflownetzes.
     */
    private HashMap<String, Integer> _idMap;
}