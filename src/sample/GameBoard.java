package sample;

import java.util.HashSet;

public class GameBoard {
    GamePiece[][] board;
    HashSet<GamePiece> pieces;
    public static final int boardSize = 8;

    public GameBoard() {
        board = new GamePiece[boardSize][boardSize];
        pieces = new HashSet<GamePiece>();
    }

    public GamePiece[][] getBoard() {
        return board;
    }
    public GamePiece pieceAt(int x, int y) {
        return board[x][y];
    }
    public void move(int startX, int startY, int endX, int endY) {
        GamePiece piece = pieceAt(startX, startY);
        if (piece != null && piece.isValidMove(endX - startX, endY - startY)) {
            board[endX][endY] = piece;
            board[startX][startY] = null;
        }
    }

}
