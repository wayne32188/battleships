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
    
        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                StackPane cell = new StackPane();
    
                // Erstelle ein Rechteck für die Zelle
                Rectangle border = new Rectangle(Main.CELL_SIZE, Main.CELL_SIZE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                border.setStrokeWidth(Main.BORDER_WIDTH);
    
                // Falls es das Spielerfeld ist, Schiffe anzeigen
                if (isPlayerGrid) {
                    for (Ship ship : ships) {
                        int[] shipPositionOnGrid = ship.getPosition();
                        if (shipPositionOnGrid != null && shipPositionOnGrid.length == 2) {
                            int shipRow = shipPositionOnGrid[0]; // Startzeile
                            int shipCol = shipPositionOnGrid[1]; // Startspalte
    
                            // Prüfen, ob die aktuelle Zelle Teil eines Schiffs ist
                            boolean isPartOfShip = false;
                            for (int i = 0; i < ship.getSize(); i++) {
                                if (ship.isVertical()) { 
                                    if (row == shipRow + i && col == shipCol) {
                                        isPartOfShip = true;
                                        break;
                                    }
                                } else { 
                                    if (row == shipRow && col == shipCol + i) {
                                        isPartOfShip = true;
                                        break;
                                    }
                                }
                            }
    
                            // Wenn die Zelle zu einem Schiff gehört, färbe sie ein
                            if (isPartOfShip) {
                                border.setFill(Color.rgb(0, 100, 0, 1)); // Dunkelgrün
                            }
                        }
                    }
                }
    
                // Füge das Rechteck zum StackPane hinzu
                cell.getChildren().add(border);
                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }
    
}
