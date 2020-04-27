package sample;

public class GamePiece {
    public enum Type {
        Pawn, Rook, Knight, Bishop, King, Queen
    }
    public enum Color {
        White, Black
    }

    String name;
    Type type;
    Color color;
    public GamePiece(String name, Type type, Color color) {
        this.name = name;
        this.type = type;
        this.color = color;
    }

    public boolean isValidMove(int x, int y) {
        switch (type) {
            case King:
                return x == 1 || y == 1;
            case Pawn:
                return y == 1;
            case Queen:
                return x == 0 && y > 0 || x > 0  && y == 0 || x == y || x == -y;
            case Bishop:
                return x == y || x == -y;
            case Rook:
                return x == 0 && y > 0 || x > 0  && y == 0;
            case Knight:
                return Math.abs(x) == 1 && Math.abs(y) == 2 || Math.abs(x) == 2 && Math.abs(y) == 1;
            default:
                throw new IllegalArgumentException("Invalid Piece");
        }
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public Type getType() {
        return type;
    }
}
