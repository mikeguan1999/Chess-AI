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
    @FXML
    void mouseClick(MouseEvent event) {
        int x = (int) (event.getX() / tileSize);
        int y = (int) (event.getY() / tileSize);
        System.out.println(x);
        System.out.println(y);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        drawBoard(new GameBoard());

    }


    public void drawTile(GameBoard board, int i, int j) {
        if ((i + j) % 2 == 0) {
            gc.setFill(Color.rgb(105,20,14));
        } else {
            gc.setFill(Color.rgb(213,136,54));
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
