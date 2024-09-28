package battleships;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ShipController {

    @FXML
    private GridPane gridPane; // Die Referenz auf das GridPane aus der FXML-Datei

    @FXML
    private Button placeShipButton;

    // Methode zum Starten der Platzierung eines Schiffs
    @FXML
    private void startShipPlacement() {
        // Platzierungslogik hier (ähnlich wie in deinem bisherigen Code)
        System.out.println("Start placing ship...");
    }

    // Weitere Logik zur Platzierung des Schiffs
    private void placeShipAt(int row, int col) {
        // Hier kann die Logik zur Schiffsplatzierung ausgelagert werden
        System.out.println("Placing ship at: " + row + ", " + col);
    }
}
