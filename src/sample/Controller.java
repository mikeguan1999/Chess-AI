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
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private final int tileSize = 100;
    public static GameBoard board;
    private boolean firstClick = true;
    private int j;
    private int i;
    @FXML
    void mouseClick(MouseEvent event) {
        if (firstClick) {
            j = (int) (event.getX() / tileSize);
            i = (int) (event.getY() / tileSize);
            System.out.println("j " + j);
            System.out.println("i " + i);
            firstClick = false;
            drawTile(board, i, j);
        }
        else {
            int newJ = (int) (event.getX() / tileSize);
            int newI = (int) (event.getY() / tileSize);
            System.out.println("j " + newJ);
            System.out.println("i " + newI);
            board.move(i,  j, newI, newJ);
            firstClick = true;
            drawTile(board, i, j);
            drawTile(board, newI, newJ);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        board = new GameBoard();
        drawBoard(board);

    }


    public void drawTile(GameBoard board, int i, int j) {
        if ((i + j) % 2 == 0) {
            if(firstClick) {
                gc.setFill(Color.rgb(105, 20, 14));
            } else {
                gc.setFill(Color.rgb(70,0,0));
            }

        } else {
            if (firstClick) {
                gc.setFill(Color.rgb(213, 136, 54));
            } else {
                gc.setFill(Color.rgb(180,90, 30));
            }
        }
        gc.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
        GamePiece piece = board.pieceAt(i, j);
        if (piece != null) {
            InputStream input = getClass().getResourceAsStream("/resources/" + piece.getName() + ".png");
//            System.out.println("/resources/" + piece.getName() + ".png");
            if (input == null) System.out.println("File not found");
            Image pieceImage = new Image(input, 100, 100, true, true);
            gc.drawImage(pieceImage, j * tileSize, i * tileSize);
        }

    }

//    public void printB(GameBoard)

    public void drawBoard(GameBoard board) {
        for (int i = 0; i < GameBoard.boardSize; i++) {
            for (int j = 0; j < GameBoard.boardSize; j++) {
                drawTile(board, i, j);
            }
        }

    }
}
