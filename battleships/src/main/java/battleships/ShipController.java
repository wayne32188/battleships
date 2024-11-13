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
        // Platzierungslogik hier 
        System.out.println("Start placing ship...");
    }


    private void placeShipAt(int row, int col) {

        System.out.println("Placing ship at: " + row + ", " + col);
    }
}
