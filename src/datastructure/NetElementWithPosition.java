package datastructure;

public abstract class NetElementWithPosition extends NetElement{
    NetElementWithPosition(int x, int y){
        _position = new Position(x, y);
    }

    private Position _position;

    public Position getPosition() { return _position; }

    public static class Position {
        public Position(int x, int y){
            setX(x);
            setY(y);
        }

        private int _x;
        private int _y;

        public void setX(int x) { _x = x; }
        public void setY(int y) { _y = y; }
        public int getX() { return _x; }
        public int getY() { return _y; }
    }
}
