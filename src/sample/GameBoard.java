package sample;

import java.util.HashMap;
import java.util.HashSet;

import static sample.GamePiece.Type.*;

public class GameBoard {
    GamePiece[][] board;
    HashSet<GamePiece> blackPieces;
    HashSet<GamePiece> whitePieces;
    public static final int boardSize = 8;
    static boolean white = false;
    static boolean black = true;
    boolean turn;


    public GameBoard() {
        board = new GamePiece[boardSize][boardSize];
        blackPieces = new HashSet<GamePiece>();
        whitePieces = new HashSet<GamePiece>();
        initializeBoard();
        turn = white;
    }
    public GameBoard(GameBoard gb) {
        this.board = new GamePiece[boardSize][boardSize];
        this.turn = gb.turn;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                this.board[i][j] = gb.pieceAt(i,j);
                blackPieces = (HashSet<GamePiece>) gb.blackPieces.clone();
                whitePieces = (HashSet<GamePiece>) gb.whitePieces.clone();
            }
        }
    }
    
    public int score() {
        int score = 0;
        HashMap<GamePiece.Type, Integer> pieceValues = new HashMap<GamePiece.Type, Integer>();
        pieceValues.put(King, 900);
        pieceValues.put(Queen, 90);
        pieceValues.put(Rook, 50);
        pieceValues.put(Bishop, 30);
        pieceValues.put(Knight, 30);
        pieceValues.put(Pawn, 10);
        for (GamePiece piece : blackPieces) {
            score += pieceValues.get(piece.getType());
        }
        System.out.println(score);
        for (GamePiece piece : whitePieces) {
            score -= pieceValues.get(piece.getType());
        }
        return score;
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
            board[0][i] = new GamePiece("b" + type, stringToType(type), black);
            board[1][i] = new GamePiece("bp", Pawn, black);
            blackPieces.add(board[0][i]);
            blackPieces.add(board[1][i]);
            board[7][i] = new GamePiece("w" + type, stringToType(type), white);
            board[6][i] = new GamePiece("wp", Pawn, white);
            whitePieces.add(board[7][i]);
            whitePieces.add(board[6][i]);
        }
    }
    public boolean possibleMove(GamePiece piece, int i, int j) {
        return piece != null && piece.getColor() == turn && piece.isValidMove(i, j);
    }
    public void move(int startI, int startJ, int endI, int endJ) {
        GamePiece piece = pieceAt(startI, startJ);
        if (possibleMove(piece, endI - startI, endJ - startJ)) {
            if (board[endI][endJ] != null && board[endI][endJ].getColor() != turn) {
                //Can only capture other color
                if (turn == black) {
                    System.out.println("hi");
                    assert whitePieces.contains(board[endI][endJ]);
                    whitePieces.remove(board[endI][endJ]);
                }
                else {
                    assert blackPieces.contains(board[endI][endJ]);
                    blackPieces.remove(board[endI][endJ]);
                }
            }
            board[endI][endJ] = piece;
            board[startI][startJ] = null;
            turn = !turn;
        }
    }

}
