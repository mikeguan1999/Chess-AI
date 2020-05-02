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
    boolean whiteCastleRight;
    boolean whiteCastleLeft;
    boolean blackCastleRight;
    boolean blackCastleLeft;
    boolean winner;
    boolean gameOver;
    int prevStartI;
    int prevStartJ;
    int prevEndI;
    int prevEndJ;
    int blackKingI;
    int blackKingJ;
    int whiteKingI;
    int whiteKingJ;


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
        whiteCastleRight = whiteCastleLeft = blackCastleLeft = blackCastleRight = true;
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
        this.blackKingI = gb.blackKingI;
        this.blackKingJ = gb.blackKingJ;
        this.whiteKingI = gb.whiteKingI;
        this.whiteKingJ = gb.whiteKingJ;
        this.whiteCastleLeft = gb.whiteCastleLeft;
        this.whiteCastleRight = gb.whiteCastleRight;
        this.blackCastleLeft = gb.blackCastleLeft;
        this.blackCastleRight = gb.blackCastleRight;

        blackPieces = new HashSet<GamePiece>();
        whitePieces = new HashSet<GamePiece>();
//        blackPieces = (HashSet<GamePiece>) gb.blackPieces.clone();
//        whitePieces = (HashSet<GamePiece>) gb.whitePieces.clone();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                GamePiece piece = gb.pieceAt(i,j);
                if (piece != null) {
                    this.board[i][j] = new GamePiece(piece.name, piece.type, piece.color, i, j);
                    if (piece.color == white) {
                        whitePieces.add(this.board[i][j]);
                    }
                    else {
                        blackPieces.add(this.board[i][j]);
                    }
                }
//                this.board[i][j] = gb.pieceAt(i, j);
            }
        }
    }

    static double[][] pawnEvalWhite = {
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
            {5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0},
            {1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
            {0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
            {0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
            {0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
            {0.5,  1.0, 1.0,  -2.0, -2.0,  1.0,  1.0,  0.5},
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
            };


    static double[][] knightEval = {
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
            {-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
            {-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
            {-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
            {-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
            {-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
    };

    static double[][] bishopEvalWhite = {
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
            { -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
            { -1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
            { -1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
            { -1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
            { -1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
    };
    static double[][] rookEvalWhite = {
            {  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
            {  0.5,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            {  0.0,   0.0, 0.0,  0.5,  0.5,  0.0,  0.0,  0.0}
            };

    static double[][] evalQueen = {
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
            { -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
            { -0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
            {  0.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
            { -1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
            };

    static double[][] kingEvalWhite = {

            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
            { -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
            {  2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0 },
            {  2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0 }
            };

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
        blackKingI = 0;
        blackKingJ = 4;
        whiteKingI = 7;
        whiteKingJ = 4;
        for (int i = 0; i < boardSize; i++) {
            String type = mappings[i];
            board[0][i] = new GamePiece("b" + type, stringToType(type), black, 0, i);
            board[1][i] = new GamePiece("bp", Pawn, black, 1, i);
            blackPieces.add(board[0][i]);
            blackPieces.add(board[1][i]);
            board[7][i] = new GamePiece("w" + type, stringToType(type), white, 7, i);
            board[6][i] = new GamePiece("wp", Pawn, white, 6, i);
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

    public HashSet<GamePiece> getPieces(boolean turn) {
        if (turn == black) {
            return blackPieces;
        }
        return whitePieces;
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

//        if (new GameBoard(this).move(startI, startJ, endI, endJ);)
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
            else if (piece.type == King) {
                if (piece.color == white) {
                    if (endI == 7) {
                        if (endJ == 6) {
                            return whiteCastleRight && pieceAt(7,5) == null && pieceAt(7,6) == null;
                        }
                        else if (endJ == 2) {
                            return whiteCastleLeft && pieceAt(7,1) == null && pieceAt(7,2) == null
                                    && pieceAt(7,3) == null;
                        }
                    }
                }
                else if (endI == 0) {
                    if (endJ == 6) {
                        return blackCastleRight && pieceAt(0,5) == null && pieceAt(0,6) == null;
                    }
                    else if (endJ == 2) {
                        return blackCastleLeft && pieceAt(0,1) == null && pieceAt(0,2) == null
                                && pieceAt(0,3) == null;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheck(boolean color) {
//        GameBoard nextState = (new GameBoard(this)).move(startI, startJ, endI, endJ);
        for (GamePiece gamePiece : this.getPieces(this.turn)) {
            if (gamePiece.color == black) {
                if (this.possibleMove(gamePiece, gamePiece.i, gamePiece.j, whiteKingI, whiteKingJ)) return true;
            }
            else {
                if (this.possibleMove(gamePiece, gamePiece.i, gamePiece.j, blackKingI, blackKingJ)) return true;
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
                case King:
                    addPossibleKingMoves(list, i, j);
                    break;
                case Knight:
                    addPossibleKnightMoves(list, i, j);
                    break;
                case Rook:
                    addPossibleRookMoves(list, i, j);
                    break;
                case Bishop:
                    addPossibleBishopMoves(list, i, j);
                    break;
                case Queen:
                    addPossibleRookMoves(list, i, j);
                    addPossibleBishopMoves(list, i, j);
                    break;
                case Pawn:
                    addPossiblePawnMoves(list, i, j);
                    break;
            }
        }
        return list;
    }

    public void pawnPromotion(GamePiece pawn, int i, int j) {
        if (pawn.getType() == Pawn && (i == 0 || i == 7)) {
            //black
            GamePiece queen;
            if (pawn.getColor())  {
                queen = new GamePiece("bq", Queen, pawn.color, i, j);
                blackPieces.remove(pawn);
                blackPieces.add(queen);
            }
            //white
            else {
                queen = new GamePiece("wq", Queen, pawn.color, i, j);
                whitePieces.remove(pawn);
                whitePieces.add(queen);
            }
            board[i][j] = queen;
        }
        return;
    }

    /**
     * Move a piece only if it is a legal move. If not, do nothing
     * @param startI The starting row
     * @param startJ The starting column
     * @param endI The ending row
     * @param endJ The ending column
     */
    public GameBoard move(int startI, int startJ, int endI, int endJ) {
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
            if (piece.type == King) {
                if (piece.color == black) {
                    blackKingI = endI;
                    blackKingJ = endJ;
                    blackCastleLeft = false;
                    blackCastleRight = false;
                    if (Math.abs(endJ - startJ) == 2) {
                        if (endJ == 2) {
                            GamePiece rook = pieceAt(0,0);
                            board[0][0] = null;
                            board[0][3] = rook;
                        }
                        else {
                            assert endJ == 6;
                            GamePiece rook = pieceAt(0,7);
                            board[0][7] = null;
                            board[0][5] = rook;
                        }

                    }
                }
                else {
                    whiteKingI = endI;
                    whiteKingJ = endJ;
                    whiteCastleLeft = false;
                    whiteCastleRight = false;
                    if (Math.abs(endJ - startJ) == 2) {
                        if (endJ == 2) {
                            GamePiece rook = pieceAt(7,0);
                            board[7][0] = null;
                            board[7][3] = rook;
                        }
                        else {
                            assert endJ == 6;
                            GamePiece rook = pieceAt(7, 7);
                            board[7][7] = null;
                            board[7][5] = rook;
                        }
                    }
                }
            }
            else if (piece.type == Rook) {
                if (piece.color == black) {
                    if (blackCastleLeft) {
                        if (startI == 0 && startJ == 0) {
                            blackCastleLeft = false;
                        }
                    }
                    if (blackCastleRight) {
                        if (startI == 0 && startJ == 7) {
                            blackCastleRight = false;
                        }
                    }
                }
                else {
                    if (whiteCastleLeft) {
                        if (startI == 7 && startJ == 0) {
                            whiteCastleLeft = false;
                        }
                    }
                    if (whiteCastleRight) {
                        if (startI == 7 && startJ == 7) {
                            whiteCastleRight = false;
                        }
                    }
                }
            }
            board[endI][endJ] = piece;
            board[startI][startJ] = null;
            prevStartI = startI;
            prevStartJ = startJ;
            prevEndI = endI;
            prevEndJ = endJ;
            piece.i = endI;
            piece.j = endJ;
            pawnPromotion(piece, endI, endJ);
            turn = !turn;
        }
        return this;
    }

    public void CastleRight(boolean color) {

    }
    public void CastleLeft(boolean color) {

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
//        for (int i = 0; i < boardSize; i++) {
//            for (int j = 0; j < boardSize; j++) {
//                GamePiece piece = pieceAt(i, j);

        HashSet<GamePiece> pieces = turn? blackPieces: whitePieces;

        if (pieces == null) return null;
        if (gameOver) return null;

        for (GamePiece piece: pieces) {
            int i = piece.i;
            int j = piece.j;

            if (piece != null && (piece.color == turn)) {
                LinkedList<int[]> possibleMoves = possibleMovesFrom(i, j);
                for (int[] possibleMove: possibleMoves) {
                    if (possibleMove(piece, i, j, possibleMove[0], possibleMove[1])) {
                        GameBoard newBoard = new GameBoard(this);
                        newBoard.move(i,j,possibleMove[0],possibleMove[1]);
//                        if (!(newBoard.gameOver && winner == white)) {
                            moves.add(newBoard);
//                        }
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
//            switch (piece.getType()) {
//                case King:
//                    score += kingEvalWhite[(7 - piece.i)][(7 - piece.j)];
//                    break;
//                case Pawn:
//                    score += pawnEvalWhite[(7 - piece.i)][(7 - piece.j)];
//                    break;
//                case Queen:
//                    score += evalQueen[piece.i][piece.j];
//                    break;
//                case Rook:
//                    score += rookEvalWhite[(7 - piece.i)][(7 - piece.j)];
//                    break;
//                case Knight:
//                    score += knightEval[piece.i][piece.j];
//                    break;
//                case Bishop:
//                    score += bishopEvalWhite[(7 - piece.i)][(7 - piece.j)];
//                    break;
//                default:
//                    break;
//            }
        }
        for (GamePiece piece : whitePieces) {
            score -= valueOfType(piece.getType());
//            switch (piece.getType()) {
////                case King:
////                    score -= kingEvalWhite[piece.i][piece.j];
////                    break;
////                case Pawn:
////                    score -= pawnEvalWhite[piece.i][piece.j];
////                    break;
////                case Queen:
////                    score -= evalQueen[piece.i][piece.j];
////                    break;
////                case Rook:
////                    score -= rookEvalWhite[piece.i][piece.j];
////                    break;
////                case Knight:
////                    score -= knightEval[piece.i][piece.j];
////                    break;
////                case Bishop:
////                    score -= bishopEvalWhite[piece.i][piece.j];
////                    break;
////                default:
////                    break;
////            }
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
        HashSet<GameBoard> states = states();
        if (states == null) return this;
        for (GameBoard state: states) {
            if (state.gameOver) return state;
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
        HashSet<GameBoard> states = states();
        if (states == null) return this;
        for (GameBoard state: states) {
            if (state.gameOver) {
                return state;
            }
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
