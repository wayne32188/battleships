package battleships;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {

    @FXML
    Button Button1 = new Button();

    @FXML
    void buttonHandler() throws IOException {
        Main.setRoot("ShipPlacement");
    }
}
