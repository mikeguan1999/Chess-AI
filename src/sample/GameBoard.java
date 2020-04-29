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
    static int minimaxDepth = 4;
    boolean turn;
    boolean whiteCastle;
    boolean blackCastle;
    boolean winner;
    boolean gameOver;
    int prevStartI;
    int prevStartJ;
    int prevEndI;
    int prevEndJ;


    /**
     * Creates a new Gameboard instance with initial piece placement
     */
    public GameBoard() {
        board = new GamePiece[boardSize][boardSize];
        blackPieces = new HashSet<GamePiece>();
        whitePieces = new HashSet<GamePiece>();
        initializeBoard();
        turn = white;
        winner = false;
        gameOver = false;
        whiteCastle = true;
        blackCastle = false;
        prevEndI = -1;
        prevEndJ = -1;
        prevStartJ = -1;
        prevStartI = -1;
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
     * Creates pieces and puts them on the board at initial positions
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
            return true;
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
                for (int j = endJ + 1; j < startJ; j++) {
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
    public boolean pawnPossibleMove(boolean color, int startI, int startJ, int endI, int endJ) {
        if (startJ == endJ) {
           return Math.abs(endI - startI) == 1 && pieceAt(endI, endJ) == null || startI == (color? 1: 6)
                   && pieceAt(endI, endJ) == null && pieceAt(color? 2: 5, startJ) == null;
        }
        else {
            return pieceAt(endI,endJ) != null;
        }
    }

    /**
     * Returns whether a move is legal and possible
      * @param piece The piece to move
     * @param startI Starting row
     * @param startJ Starting col
     * @param endI Ending row
     * @param endJ Ending col
     * @return true if it is a possible move
     */
    public boolean possibleMove(GamePiece piece, int startI, int startJ, int endI, int endJ) {
        if (withinBounds(startI, startJ) && withinBounds(endI, endJ) && piece != null && piece.getColor() == turn
        && (pieceAt(endI, endJ) == null || pieceAt(endI, endJ).color != turn)) {
            if (piece.isValidMove(endI - startI, endJ - startJ)) {
                switch (piece.type) {
                    case Rook:
                        return rookPossibleMove(startI, startJ, endI, endJ);
                    case Bishop:
                        return bishopPossibleMove(startI, startJ, endI, endJ);
                    case Queen:
                        return (rookPossibleMove(startI, startJ, endI, endJ)
                                || bishopPossibleMove(startI, startJ, endI, endJ));
                    case Pawn:
                        return pawnPossibleMove(piece.color, startI, startJ, endI, endJ);
                }
                return true;
            }
        }
        return false;
    }




    public void addPossibleKingMoves(LinkedList<int[]> moves, int i, int j) {
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (possibleMove(pieceAt(i, j), i, j, i + k, j + l))  {
                    moves.add(new int[] {i + k, j + l});
                }
            }
        }
    }
    public void addPossibleKnightMoves(LinkedList<int[]> moves, int i, int j) {
        int[][] posMoves = new int[][] {{1,2},{1,-2},{2,1},{2,-1},{-1,2},{-1,-2},{-2,1},{-2,-1}};
        for (int[] arr : posMoves) {
            if (possibleMove(pieceAt(i, j), i, j, i + arr[0], j + arr[1])) {
                moves.add(new int[] {i + arr[0], j + arr[1]});
            }
        }
    }
    public void addPossibleRookMoves(LinkedList<int[]> moves, int i, int j) {
        for (int k = 0; k < boardSize; k++) {
            if (possibleMove(pieceAt(i, j), i, j, i, k)) {
                moves.add(new int[] {i, k});
            }
            if (possibleMove(pieceAt(i, j), i, j, k, j)) {
                moves.add(new int[] {k, j});
            }
        }
    }
    private void addPossibleBishopMoves(LinkedList<int[]> moves, int i, int j) {
        for (int k = -boardSize; k < boardSize; k++) {
            if(possibleMove(pieceAt(i, j), i, j, i + k, j + k)) {
                moves.add(new int[] {i + k, j + k});
            }
            if (possibleMove(pieceAt(i, j), i, j, i + k, j - k)) {
                moves.add(new int[] {i + k, j - k});
            }
        }
    }
    private void addPossiblePawnMoves(LinkedList<int[]> moves, int i, int j) {
        int multiplier = pieceAt(i, j).color? 1 : -1;
        if (possibleMove(pieceAt(i, j), i, j, i + 2 * multiplier, j)) {
            moves.add(new int[] {i + 2 * multiplier, j});
        }
        if (possibleMove(pieceAt(i, j), i, j, i + multiplier, j)) {
            moves.add(new int[] {i + multiplier, j});
        }
        if (possibleMove(pieceAt(i, j), i, j, i + multiplier, j + 1)) {
            moves.add(new int[] {i + multiplier, j + 1});
        }
        if (possibleMove(pieceAt(i, j), i, j, i + multiplier, j - 1)) {
            moves.add(new int[] {i + multiplier, j - 1});
        }
    }

    //possible moves from i, j
    public LinkedList<int[]> possibleMovesFrom(int i, int j) {
        GamePiece piece = pieceAt(i, j);
        LinkedList<int[]> list = new LinkedList<int[]>();
        if (piece != null /*&& piece.color == turn*/) {
            switch (piece.type) {
                case King: addPossibleKingMoves(list, i, j);
                case Knight: addPossibleKnightMoves(list, i, j);
                case Rook: addPossibleRookMoves(list, i, j);
                case Bishop: addPossibleBishopMoves(list, i, j);
                case Queen: addPossibleRookMoves(list, i, j); addPossibleBishopMoves(list, i, j);
                case Pawn: addPossiblePawnMoves(list, i, j);
            }
        }
        return list;
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
                //Game over if king captured
                if (board[endI][endJ].type == King) {
                    winner = turn;
                    gameOver = true;
                }
                //Can only capture other color
                if (turn == black) {
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
            prevStartI = startI;
            prevStartJ = startJ;
            prevEndI = endI;
            prevEndJ = endJ;
            turn = !turn;
        }
    }

    public GameBoard computerMove() {
        return maxTurn(Integer.MIN_VALUE, Integer.MAX_VALUE, minimaxDepth);
    }


//    public void addRookMoves(int i, int j, )

//    PriorityQueue<GameBoard> states() {
//        PriorityQueue<GameBoard> moves = new PriorityQueue<GameBoard>();
//        for (int i = 0; i < boardSize; i++) {
//            for (int j = 0; j < boardSize; j++) {
//                GamePiece piece = pieceAt(i, j);
//                if (piece != null && (piece.color == turn)) {
//                    for (int k = 0; k < boardSize; k++) {
//                        for (int l = 0; l < boardSize; l++) {
//                            if (possibleMove(piece, i, j, k, l)) {
//                                GameBoard newBoard = new GameBoard(this);
//                                newBoard.move(i,j,k,l);
//                                moves.add(newBoard);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return moves;
//    }

    public HashSet<GameBoard> states() {
        HashSet<GameBoard> moves = new HashSet<GameBoard>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                GamePiece piece = pieceAt(i, j);
                if (piece != null && (piece.color == turn)) {
                    LinkedList<int[]> possibleMoves = possibleMovesFrom(i, j);
                    for (int[] possibleMove: possibleMoves) {
                        if (possibleMove(piece, i, j, possibleMove[0], possibleMove[1])) {
                            GameBoard newBoard = new GameBoard(this);
                            newBoard.move(i,j,possibleMove[0],possibleMove[1]);
                            moves.add(newBoard);
                        }
                    }


//                    for (int k = 0; k < boardSize; k++) {
//                        for (int l = 0; l < boardSize; l++) {
//                            if (possibleMove(piece, i, j, k, l)) {
//                                GameBoard newBoard = new GameBoard(this);
//                                newBoard.move(i,j,k,l);
//                                moves.add(newBoard);
//                            }
//                        }
//                    }
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

    public static int valueOfType(GamePiece.Type type) {
        switch (type) {
            case King: return 900;
            case Queen: return 90;
            case Rook: return 50;
            case Bishop:
            case Knight:
                return 30;
            case Pawn: return 10;
            default: return 0;
        }
    }
    //Minimax Algorithm
    public int score() {
        int score = 0;
        for (GamePiece piece : blackPieces) {
            score += valueOfType(piece.getType());
        }
        for (GamePiece piece : whitePieces) {
            score -= valueOfType(piece.getType());
        }
        return score;
    }

    //Computer Turn (black)
    public GameBoard maxTurn(int a, int b, int depth) {
        assert turn == black;
        if (depth == 0) {
            return this;
        }
        int score = Integer.MIN_VALUE;
        GameBoard best = null;
        for (GameBoard state: states()) {
            GameBoard nextState = state.minTurn(a, b, depth - 1);
            if (nextState != null) {
                int nextScore = nextState.score();
                if (nextScore > score) {
                    score = nextScore;
                    best = state;
                }
            }
            if (score >= b) return best;
            a = Math.max(a, score);
        }
        return best;
    }

    //User Turn

    public GameBoard minTurn(int a, int b, int depth) {
        if (depth == 0) {
            return this;
        }
        int score = Integer.MAX_VALUE;
        GameBoard best = null;
        for (GameBoard state: states()) {
            GameBoard nextState = state.maxTurn(a, b, depth - 1);
            if (nextState != null) {
                int nextScore = nextState.score();
                if (nextScore < score) {
                    score = nextScore;
                    best = state;
                }
            }
            if (score <= a) return best;
            b = Math.min(b, score);
        }
        return best;
    }

    @Override
    public int compareTo(GameBoard board2) {
//        int multiplier = turn? 1 : -1;
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
