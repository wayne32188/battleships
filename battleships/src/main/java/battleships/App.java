package battleships;

import java.io.IOException;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class App extends Application {

    private static final Dotenv dotenv = Dotenv.load();

    public static final int GRID_SIZE = Integer.parseInt(dotenv.get("GRID_SIZE"));
    public static final int CELL_SIZE = Integer.parseInt(dotenv.get("CELL_SIZE"));
    public static final int BORDER_WIDTH = Integer.parseInt(dotenv.get("BORDER_WIDTH"));

    private static Scene scene;

    ArrayList<Ship> ships = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Erstelle die Szene

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ShipPlacement.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root, 1000, 1100);
        ShipPlacementController shipPlacementController = fxmlLoader.getController();

        // Rotation durch Taste "R"
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                ShipPlacementController.isVertical = !ShipPlacementController.isVertical;
                shipPlacementController.setRotationText(ShipPlacementController.isVertical);
                System.out.println("Rotation: " + (ShipPlacementController.isVertical ? "Vertikal" : "Horizontal"));
                for (Ship ship : ships) {
                    ship.updateRotation(ShipPlacementController.isVertical); // Schiffe visuell aktualisieren
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Drag-and-Drop Schiffe auf einem 10x10 Grid");
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return (Parent) fxmlLoader.load();
    }
}