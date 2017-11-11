package datastructure;

/**
 * Stellt ein Netzelement eines Petrinetzes dar.
 * Organisiert die Vergabe einer einzigartigen ID an die Netzelemente.
 */
abstract class NetElement {
    public NetElement(){
        _id = _idCounter++;
    }

    /**
     * Zählervariable für die Vergabe der nächsten freien Id.
     */
    private static int _idCounter = 0;



    /**
     * Persönliche Id des Netzelements.
     */
    private final int _id;
    public int getId() { return _id; }

}