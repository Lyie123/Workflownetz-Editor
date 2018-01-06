package workflownet;

import javafx.geometry.Point2D;

/**
 * Die Klasse NetElement stellt die gemeinsame Basis fuer alle Netzelemente eines Workflownetzes zur Verfuegung.
 * Ausserdem organisiert die Klasse die einzigartige Vergabe von Identifikationsnummern.
 */
public abstract class NetElement implements IDrawable{
    /**Konstruktor der abstrakten Klasse NetElement
     * @param type Dies stellt den Typ des Netzelements dar.
     */
    public NetElement(NetElementType type){
        _type = type;
        _id = _counter++;
    }

    /**
     * Legt die Strichstaerke fest, mit der alle Netzelemente gezeichnet werden.
     */
    private static double _strokeThickness = 2.5;

    /**
     * @return liefert die Zeichenstaerke des Netzelements zurück in Abhaengigkeit der eingestellten Skalierung der Netzelemente.
     */
    public static double getStrokeThickness() {return _strokeThickness *Scale; }

    /**
     * Der globale Skalierungsfaktor aller Netzelemente.
     */
    public static double Scale = 1;

    /**
     * @return liefert die id des Netzelements zurück
     */
    public int getId(){
        return _id;
    }

    /**
     * Ueberprueft ob der Punkt p innerhalb des Netzelements liegt.
     * @param p Punkt der ueberprueft werden soll ob er innerhalb des Netzelements liegt.
     * @return true wenn p innerhalb des Netzelements liegt,
     *         sonst false
     */
    public abstract boolean PointLiesOnNetElement(Point2D p);

    /**
     * @return Gibt den Typ des Netzelements zurück
     */
    public NetElementType getType() {
        return _type;
    }

    /**
     * @return true wenn Netzelement selektiert ist,
     *         sonst false
     */
    public boolean isSelected(){ return _selected; }

    /**
     * @param value legt fest ob das Netzelement selektiert wurde oder nicht.
     */
    void setSelected(boolean value){ _selected = value; }

    /**
     * Gibt an ob das Netzelement selektiert wurde oder nicht.
     */
    private boolean _selected = false;
    /**
     * Legt fest welchen Typ das Netzelement hat.
     */
    private NetElementType _type;
    /**
     * Die einzigartige id des Netzelements
     */
    private int _id;
    /**
     * Laufvariable die sich nach jeder Erstellung eines Netzelements um 1 erhoeht.
     * Dadurch wird sichergestellt das jede id nur einmal vergeben wird.
     */
    private static int _counter = 0;
}
