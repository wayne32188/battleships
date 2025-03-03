package battleships;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ShipPlacementController {

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

    @FXML
    private Text orientationHint;

    private final ArrayList<Ship> ships = new ArrayList<>();

    @FXML
    public void initialize() {

        // Erstelle zwei GridPane-Instanzen für Spieler und Gegner
        playerField = createPlayerField();
        enemyField = createEnemyField();

        boolean IS_VERTICAL = false;
        setOrientationText(IS_VERTICAL);

        gridBox.getChildren().addAll(playerField, enemyField);

        // Schiffe erstellen mit Größe 3, 4, 5 
        ships.add(new Ship(3, false));
        ships.add(new Ship(4, false));
        ships.add(new Ship(5, false));

        // Visuelle Darstellung der Schiffe erstellen
        for (Ship ship : ships) {
            Rectangle shipRectangle = ship.createShip(Main.CELL_SIZE, IS_VERTICAL);
            shipsBox.getChildren().add(shipRectangle);
        }

        resetButton.setOnAction(event -> {
            resetAllShips(playerField);
        });


    }

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("PlayingField");
    }
    
    private GridPane createPlayerField() {
        return Main.createGridPane(true);
    }

    private GridPane createEnemyField() {
        return Main.createGridPane(false);
    }

    public void setOrientationText(boolean isVertical) {
        if (isVertical) {
            orientationHint.setText("Vertical");
        } else {
            orientationHint.setText("Horizontal");
        }
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
