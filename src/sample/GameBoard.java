package sample;

import java.util.*;

import static sample.GamePiece.Type.*;

public class GameBoard implements Comparable<GameBoard> {
    GamePiece[][] board;
    HashSet<GamePiece> blackPieces;
    HashSet<GamePiece> whitePieces;
    public static final int boardSize = 8;
    static boolean white = false;
    static boolean black = true;
    boolean turn;

    /**
     * Creates a new Gameboard instance with initial piece placement
     */
    public GameBoard() {
        board = new GamePiece[boardSize][boardSize];
        blackPieces = new HashSet<GamePiece>();
        whitePieces = new HashSet<GamePiece>();
        initializeBoard();
        turn = white;
    }

    /**
     * Creates a GameBoard by copying another Gameboard
     * @param gb The given Gameboard
     */
    public GameBoard(GameBoard gb) {
        this.board = new GamePiece[boardSize][boardSize];
        this.turn = gb.turn;
        blackPieces = (HashSet<GamePiece>) gb.blackPieces.clone();
        whitePieces = (HashSet<GamePiece>) gb.whitePieces.clone();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
//                GamePiece piece = gb.pieceAt(i,j);
//                this.board[i][j] = new GamePiece(piece.name, piece.type, piece.color);
                this.board[i][j] = gb.pieceAt(i, j);
            }
        }
    }

    /**
     * Returns the 2d array of GamePieces
     * @return the GamePiece array
     */
    public GamePiece[][] getBoard() {
        return board;
    }

    /**
     * Returns The piece at a given location
     * @param x The row
     * @param y The column
     * @return
     */
    public GamePiece pieceAt(int x, int y) {
        return board[x][y];
    }

    /**
     * Maps Strings to GamePiece types
     * @param type The type as a string
     * @return The type as a GamePiece.Type
     */
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

    /**
     * Creates pieces and puts them on the board at initial positons
     */
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

    public boolean withinBounds(int i, int j) {
        return i < 8 && i >= 0 && j < 8 && j >= 0;
    }


    public boolean bishopPossibleMove(int startI, int startJ, int endI, int endJ) {
        if (endI - startI == endJ - startJ) {
            for (int i = Math.min(startI,endI) + 1, j = Math.min(startJ, endJ) + 1; i < Math.max(startI, endI); i++,j++) {
                if (pieceAt(i, j) != null) {
                    return false;
                }
            }
            return true;
        } else if (endI - startI == -(endJ - startJ)) {
            for (int i = Math.min(startI,endI) + 1, j = Math.max(startJ, endJ) - 1; i < Math.max(startI, endI); i++,j--) {
                if (pieceAt(i, j) != null) {
                    return false;
                }
            }
        }
        return false;
    }
    public boolean rookPossibleMove(int startI, int startJ, int endI, int endJ) {
        //Same row
        if (startI == endI) {
            if (endJ > startJ) {
                for (int j = startJ + 1; j < endJ; j++) {
                    if (pieceAt(startI, j) != null) {
                        return false;
                    }
                }
            }
            else {
                for (int j = startJ + 1; j < endJ; j++) {
                    if (pieceAt(startI, j) != null) {
                        return false;
                    }
                }
            }
            return true;
        }
        //Same col
        else if (startJ == endJ) {
            if (endI > startI) {
                for (int i = startI + 1; i < endI; i++) {
                    if (pieceAt(i, startJ) != null) {
                        return false;
                    }
                }
            }
            else {
                for (int i = startI - 1; i > endI; i--) {
                    if (pieceAt(i, startJ) != null) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    /**
     * Returns whether a move is legal and possible
      * @param piece The piece to move
     * @param startI Starting row
     * @param startJ Starting col
     * @param endI Ending row
     * @param endJ Ending col
     * @return
     */
    public boolean possibleMove(GamePiece piece, int startI, int startJ, int endI, int endJ) {
        if (withinBounds(startI, startJ) && withinBounds(endI, endJ) && piece != null && piece.getColor() == turn
                && piece.isValidMove(endI - startI, endJ - startJ)
        && (pieceAt(endI, endJ) == null || pieceAt(endI, endJ).color != turn)) {
            switch (piece.type) {
                case Rook: return rookPossibleMove(startI, startJ, endI, endJ);
                case Bishop: return bishopPossibleMove(startI, startJ, endI, endJ);
                case Queen: return (rookPossibleMove(startI, startJ, endI, endJ)
                        || bishopPossibleMove(startI, startJ, endI, endJ));
                case Pawn: return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Move a piece only if it is a legal move. If not, do nothing
     * @param startI The starting row
     * @param startJ The starting column
     * @param endI The ending row
     * @param endJ The ending column
     */
    public void move(int startI, int startJ, int endI, int endJ) {
        GamePiece piece = pieceAt(startI, startJ);
        if (possibleMove(piece, startI, startJ, endI, endJ)) {
            if (board[endI][endJ] != null && board[endI][endJ].getColor() != turn) {
                //Can only capture other color
                if (turn == black) {
                    assert whitePieces.contains(board[endI][endJ]);
                    whitePieces.remove(board[endI][endJ]);
                    System.out.println(whitePieces);
                }
                else {
                    assert blackPieces.contains(board[endI][endJ]);
                    System.out.println(whitePieces.contains(board[endI][endJ]));
                    blackPieces.remove(board[endI][endJ]);
                }
            }
            board[endI][endJ] = piece;
            board[startI][startJ] = null;
            turn = !turn;
        }
    }


//    public void addRookMoves(int i, int j, )

    PriorityQueue<GameBoard> states() {
        PriorityQueue<GameBoard> moves = new PriorityQueue<GameBoard>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                GamePiece piece = pieceAt(i, j);
                if (piece != null && (piece.color == turn)) {
                    for (int k = 0; k < boardSize; k++) {
                        for (int l = 0; l < boardSize; l++) {
                            if (possibleMove(piece, i, j, k, l)) {
                                GameBoard newBoard = new GameBoard(this);
                                newBoard.move(i,j,k,l);
                                moves.add(newBoard);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        String str = "\n";
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                GamePiece piece = board[i][j];
                if (piece == null) {
                    str += "___, ";
                }
                else {
                    str += board[i][j].toString() + ", ";
                }
            }
            str+="\n";
        }
        return str;
    }

    //Minimax Algorithm
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
        for (GamePiece piece : whitePieces) {
            score -= pieceValues.get(piece.getType());
        }
        return score;
    }

    //Computer Turn (black)
    public static GameBoard maxTurn(int a, int b) {
        return null;
    }

    //User Turn

    public static GameBoard minTurn(int a, int b) {
        return null;
    }

    @Override
    public int compareTo(GameBoard board2) {
        int score1 = score();
        int score2 = board2.score();
        if (score1 < score2) return -1;
        else if (score1 > score2) return 1;
        else return 0;
    }

//    @Override
//    public int compare(GameBoard board1, GameBoard board2) {
//        int score1 = board1.score();
//        int score2 = board2.score();
//        if (score1 < score2) return -1;
//        else if (score1 > score2) return 1;
//        else return 0;
//    }
}
