package workflownet;

/**
 * Stellt ein Netzelement eines Petrinetzes dar.
 * Organisiert die Vergabe einer einzigartigen ID an die Netzelemente.
 */
abstract class NetElement implements INetELement{
    public NetElement(NetElementType type){
        _id = _idCounter++;
        _type = type;
    }

    /**
     * Zählervariable für die Vergabe der nächsten freien Id.
     */
    private static int _idCounter = 0;

    /**
     * Persönliche Id des Netzelements.
     */
    private final int _id;
    private final NetElementType _type;
    public int getId() { return _id; }
    public NetElementType getType() {
        return _type;
    }
}