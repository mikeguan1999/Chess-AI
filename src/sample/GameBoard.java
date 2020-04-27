package sample;

import java.util.HashSet;

import static sample.GamePiece.Color.*;
import static sample.GamePiece.Type.*;

public class GameBoard {
    GamePiece[][] board;
    HashSet<GamePiece> pieces;
    public static final int boardSize = 8;

    public GameBoard() {
        board = new GamePiece[boardSize][boardSize];
        pieces = new HashSet<GamePiece>();
        initializeBoard();
    }

    public GamePiece[][] getBoard() {
        return board;
    }
    public GamePiece pieceAt(int x, int y) {
        return board[x][y];
    }
    public static GamePiece.Type stringToType(String type) {
        switch (type) {
            case "r" : return Rook;
            case "kn" : return Knight;
            case "b" : return Bishop;
            case "q" : return Queen;
            case "k" : return King;
            case "p" : return Pawn;
            default: throw new IllegalArgumentException("Invalid Piece");
        }


    }
    public void initializeBoard() {
        String[] mappings = new String[] {"r", "kn", "b", "q", "k", "b", "kn", "r"};
        for (int i = 0; i < boardSize; i++) {
            String type = mappings[i];
            board[0][i] = new GamePiece("b" + type, stringToType(type), Black);
            board[1][i] = new GamePiece("bp", Pawn, Black);
            board[7][i] = new GamePiece("w" + type, stringToType(type), White);
            board[6][i] = new GamePiece("wp", Pawn, White);
        }
    }
    public void move(int startX, int startY, int endX, int endY) {
        GamePiece piece = pieceAt(startX, startY);
        if (piece != null && piece.isValidMove(endX - startX, endY - startY)) {
            board[endX][endY] = piece;
            board[startX][startY] = null;
        }
    }

}
