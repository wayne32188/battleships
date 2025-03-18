package battleships;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SecondaryController {

    @FXML
    private GridPane playerPlayingField;

    @FXML
    private GridPane enemyPlayingField;

    @FXML
    private HBox playingGridBox;

    @FXML
    private void initialize() {
        playerPlayingField = createGameGrid(true, ShipPlacementController.getShips());
        enemyPlayingField = createGameGrid(false, null);

        ArrayList<Ship> ships = ShipPlacementController.getShips();

        System.out.println(java.util.Arrays.toString(ships.get(1).getPosition()));

        playingGridBox.getChildren().addAll(playerPlayingField, enemyPlayingField);

        System.out.println("SecondaryController initialized");
    }

    @FXML
    private void buttonHandler() throws IOException {
        Main.setRoot("ShipPlacement");
    }

    public static GridPane createGameGrid(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();
        StackPane[][] cells = new StackPane[Main.GRID_SIZE][Main.GRID_SIZE]; // 2D-Array für einfacheren Zugriff

        // Erstelle das Grid mit leeren Zellen
        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                StackPane cell = new StackPane();

                Rectangle border = new Rectangle(Main.CELL_SIZE, Main.CELL_SIZE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                border.setStrokeWidth(Main.BORDER_WIDTH);

                cell.getChildren().add(border);
                gridPane.add(cell, col, row);

                cells[row][col] = cell; // Speichert die Zelle im 2D-Array
            }
        }

        // Falls es das Spielerfeld ist, Schiffe einzeichnen
        if (isPlayerGrid) {
            for (Ship ship : ships) {
                int[] shipPositionOnGrid = ship.getPosition();
                int cellRow = shipPositionOnGrid[0]; // Start-Zeile
                int cellCol = shipPositionOnGrid[1]; // Start-Spalte

                for (int i = 0; i < ship.getSize(); i++) {
                    StackPane targetCell = ship.isVertical()
                            ? (StackPane) gridPane.getChildren().get(((cellRow + i) * Main.GRID_SIZE) + cellCol)
                            : (StackPane) gridPane.getChildren().get((cellRow * Main.GRID_SIZE) + (cellCol - i));

                    Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
                    targetBorder.setFill(Color.rgb(0, 100, 0, 1)); // Dunkelgrün mit 100% Transparenz
                }
            }
        }

        return gridPane;
    }

}
