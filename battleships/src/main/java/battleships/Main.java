package battleships;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int GRID_SIZE = 10;
    public static final int CELL_SIZE = 50;
    public static final int BORDER_WIDTH = 1;
    private static Scene scene;
    

    ArrayList<Ship> ships = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Erstelle die Szene

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ShipPlacement.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        ShipPlacementController shipPlacementController = fxmlLoader.getController();

        // Rotation durch Taste "R"
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                ShipPlacementController.isVertical = !ShipPlacementController.isVertical;
                shipPlacementController.setOrientationText(ShipPlacementController.isVertical);
                System.out.println("Rotation: " + (ShipPlacementController.isVertical ? "Vertikal" : "Horizontal"));
                for (Ship ship : ships) {
                    ship.updateOrientation(ShipPlacementController.isVertical); // Schiffe visuell aktualisieren
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

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return (Parent) fxmlLoader.load();
    }
}