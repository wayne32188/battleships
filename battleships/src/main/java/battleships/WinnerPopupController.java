package battleships;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WinnerPopupController {
    @FXML
    public Text winnerText;

    public static void openWinnerPopup(boolean isPlayerWinner) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(App.class.getResource("WinnerPopup.fxml"));
        Scene scene = new Scene(loader.load(), 500, 300);
        WinnerPopupController controller = loader.getController();
        controller.winnerText.setText(isPlayerWinner ? "Du hast gewonnen!" : "Du hast verloren!");
        stage.setScene(scene);
        stage.show();
    }
}