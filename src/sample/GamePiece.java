package sample;


public class GamePiece {
    public enum Type {
        Pawn, Rook, Knight, Bishop, King, Queen
    }
    static boolean white = false;
    static boolean black = true;


    String name;
    Type type;
    boolean color;
    int lastStartI;
    int lastStartJ;
    int lastEndI;
    int lastEndJ;
    public GamePiece(String name, Type type, boolean color) {
        this.name = name;
        this.type = type;
        this.color = color;
    }

    public boolean isValidMove(int i, int j) {
        switch (type) {
            case King:
                return Math.abs(i) <= 1 && Math.abs(j) <= 1;
            case Pawn:
                i = color == black ? i: -i;
                return (i == 2) && j == 0  || i == 1 && Math.abs(j) <= 1;
            case Queen:
                return i == 0 && j != 0 || i != 0  && j == 0 || i == j || i == -j;
            case Bishop:
                return i == j || i == -j;
            case Rook:
                return i == 0 && j != 0 || i != 0  && j == 0;
            case Knight:
                return Math.abs(i) == 1 && Math.abs(j) == 2 || Math.abs(i) == 2 && Math.abs(j) == 1;
            default:
                throw new IllegalArgumentException("Invalid Piece");
        }
    }

    public String getName() {
        return name;
    }
    public boolean getColor() {
        return color;
    }
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name.length() == 2? name + " ": name;
    }
}
