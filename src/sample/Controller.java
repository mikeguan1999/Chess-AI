package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

import static sample.GamePiece.black;

public class Controller implements Initializable, Runnable{
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private final int tileSize = 80;
    public static GameBoard board;
    private boolean firstClick = true;
    private int j;
    private int i;
    @FXML
    void mouseClick(MouseEvent event) {
        if (!board.gameOver) {
            int newJ = (int) (event.getX() / tileSize);
            int newI = (int) (event.getY() / tileSize);
            if (!(newJ >= 0 && newJ < GameBoard.boardSize && newI >= 0 && newI < GameBoard.boardSize)) {
                return;
            }
            if (firstClick) {
                j = newJ;
                i = newI;
                firstClick = false;
                drawTile(board, i, j, false);
                for (int[] move : board.possibleMovesFrom(i, j)) {
                    drawTile(board, move[0], move[1], false);
                }
            } else {
                //my turn
                newJ = (int) (event.getX() / tileSize);
                newI = (int) (event.getY() / tileSize);
                board.move(i, j, newI, newJ);
                firstClick = true;
//            drawTile(board, i, j, true);
//            drawTile(board, newI, newJ, true);
//                drawBoard(board);

                if (board.gameOver) {
                    System.out.println("Game Over! The Winner is " + (board.winner ? "Computer" : "Player"));
                    return;
                }
                HashSet<GameBoard> queue = board.states();
                int count = 0;
                for (GameBoard newBoard: queue) {
                    System.out.println(newBoard);
                    System.out.println(newBoard.score());
                    count++;

                }
                System.out.println("\n count: " + count);


                drawBoard(board);
                //blackturn
                if (board.turn == black) {
                    System.out.println("hi");
                    board = board.computerMove();
                    drawBoard(board);
                    drawTile(board, board.prevStartI, board.prevStartJ, true);
                    drawTile(board, board.prevEndI, board.prevEndJ, true);
                }
                if (board.gameOver) {
                    System.out.println("Game Over! The Winner is " + (board.winner ? "Computer" : "Player"));
                    return;
                }
            }
        }
    }

//    public void makeMove(int i, int j) {
//
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        board = new GameBoard();
        long time = System.nanoTime();
        for (int k = 0; k < 1000000; k++) {
            GameBoard board1 = new GameBoard(board);
        }
        drawBoard(board);
        run();
//        System.out.println(board);

//        HashSet<GameBoard> queue = board.states();
//        int count = 0;
//        for (GameBoard newBoard: queue) {
//            System.out.println(newBoard);
//            System.out.println(newBoard.score());
//            count++;
//
//        }
//        System.out.println("count: " + count);
//        System.out.print("time: ");
//        System.out.println((System.nanoTime() - time)/100000000);
//        displayWinner(board);
    }


    public void drawTile(GameBoard board, int i, int j, boolean darken) {
        if ((i + j) % 2 == 0) {
            if(firstClick && !darken) {
                gc.setFill(Color.rgb(105, 20, 14));
            } else {
                gc.setFill(Color.rgb(60,0,0));
            }

        } else {
            if (firstClick && !darken) {
                gc.setFill(Color.rgb(213, 136, 54));
            } else {
                gc.setFill(Color.rgb(200,50, 0));
            }
        }
        gc.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
        GamePiece piece = board.pieceAt(i, j);
        if (piece != null) {
            InputStream input = getClass().getResourceAsStream("/resources/" + piece.getName() + ".png");
//            System.out.println("/resources/" + piece.getName() + ".png");
            if (input == null) System.out.println("File not found");
            Image pieceImage = new Image(input, tileSize, tileSize, true, true);
            gc.drawImage(pieceImage, j * tileSize, i * tileSize);
        }

    }

//    public void printB(GameBoard)

    public void drawBoard(GameBoard board) {
        for (int i = 0; i < GameBoard.boardSize; i++) {
            for (int j = 0; j < GameBoard.boardSize; j++) {
                drawTile(board, i, j, false);
            }
        }

    }
    public void displayWinner(GameBoard board) {
        InputStream input = getClass().getResourceAsStream("/resources/winner.png");
//            System.out.println("/resources/" + piece.getName() + ".png");
        if (input == null) System.out.println("File not found");
        Image pieceImage = new Image(input, tileSize * 4, tileSize * 2, true, true);
        gc.drawImage(pieceImage, 2 * tileSize, 2 * tileSize);
    }

    @Override
    public void run() {
        System.out.println("hihihi");
    }
}
