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
    private static Position _position;


    /**
     * Persönliche Id des Netzelements.
     */
    private final int _id;
    public int getId() { return _id; }
    public Position getPosition() { return _position; }

    private static class Position {
        public Position(int x, int y){
            _x = x;
            _y = y;
        }

        private int _x;
        private int _y;

        public int getX() { return _x; }
        public int getY() { return _y; }
    }
}