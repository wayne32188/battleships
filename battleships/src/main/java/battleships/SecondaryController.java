package battleships;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SecondaryController {

    @FXML
    private GridPane playerField;

    @FXML
    private GridPane enemyField;

    @FXML
    private VBox shipsBox;

    @FXML
    private Button resetButton;

    @FXML
    private Button openSecondaryButton;

    @FXML
    private HBox gridBox;

    private boolean isVertical = false;
    private ArrayList<Ship> ships = new ArrayList<>();

    @FXML
    public void initialize() {
        // Erstelle zwei GridPane-Instanzen für Spieler und Gegner
        playerField = createPlayerField();
        enemyField = createEnemyField();

        gridBox.getChildren().addAll(playerField, enemyField);

        // Schiffe erstellen mit Größe 3, 4, 5 
        ships.add(new Ship(3, false));
        ships.add(new Ship(4, false));
        ships.add(new Ship(5, false));

        // Visuelle Darstellung der Schiffe erstellen
        for (Ship ship : ships) {
            Rectangle shipRectangle = ship.createShip(Main.CELL_SIZE, isVertical);
            shipsBox.getChildren().add(shipRectangle);
        }

        resetButton.setOnAction(event -> {
            resetAllShips(playerField);
        });

    }

    private GridPane createPlayerField() {
        return Main.createGridPane(true);
    }

    private GridPane createEnemyField() {
        return Main.createGridPane(false);
    }

    private void resetAllShips(GridPane playerField) {
        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                int cellIndex = row * Main.GRID_SIZE + col;
                StackPane cell = (StackPane) playerField.getChildren().get(cellIndex);

                // Überprüfe die Farbe der Zelle
                Rectangle border = (Rectangle) cell.getChildren().get(0);
                if (border.getFill().equals(Color.DARKGREEN)) {
                    // Setze die Zellenfarbe zurück auf TRANSPARENT
                    border.setFill(Color.TRANSPARENT);
                }
            }
        }

        // Alle Schiffe wieder sichtbar machen und Startpositionen zurücksetzen
        for (Ship ship : ships) {
            ship.getShipVisuals().setVisible(true); // Sichtbarkeit wiederherstellen
        }
    }
}