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
    private Image bk;
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
        InputStream input = getClass().getResourceAsStream("/resources/bb.png");
        if (input == null) System.out.println("File not found");
        bk = new Image(input, 100, 100, true, true);
        drawBoard(new GameBoard());
    }


    public void drawTile(GameBoard board, int i, int j) {
        if ((i + j) % 2 == 0) {
            gc.setFill(Color.rgb(105,20,14));
        } else {
            gc.setFill(Color.rgb(213,136,54));
        }
        gc.fillRect(i * tileSize, j * tileSize, i + tileSize, j + tileSize);
        if (board.pieceAt(i, j) != null) {
            gc.drawImage(bk, 0 , 0);
        }

    }

    public void drawBoard(GameBoard board) {
        for (int i = 0; i < GameBoard.boardSize; i++) {
            for (int j = 0; j < GameBoard.boardSize; j++) {
                drawTile(board, i, j);
            }
        }

    }
}
