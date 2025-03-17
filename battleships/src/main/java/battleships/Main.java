package battleships;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static battleships.lib.Helper.doesShipFit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int GRID_SIZE = 10;
    public static final int CELL_SIZE = 50;
    public static final int BORDER_WIDTH = 1;
    public static boolean isVertical = false;
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
                isVertical = !isVertical;
                shipPlacementController.setOrientationText(isVertical);
                System.out.println("Rotation: " + (isVertical ? "Vertikal" : "Horizontal"));
                for (Ship ship : ships) {
                    ship.updateOrientation(isVertical); // Schiffe visuell aktualisieren
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

    public static GridPane createGridPane(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane cell = new StackPane();

                // Erstelle ein Rectangle für die Zellen
                Rectangle border = new Rectangle(CELL_SIZE, CELL_SIZE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                border.setStrokeWidth(BORDER_WIDTH);

                if (isPlayerGrid) {
                    // Erstelle das OnDragOver-Ereignis für die Zellen
                    cell.setOnDragOver(event -> {
                        if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                        event.consume();
                    });

                    // Erstelle das OnDragDropped-Ereignis für die Zellen
                    cell.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        boolean success = false;

                        if (db.hasString()) {

                            // Schiffgröße vom String zurück zum Integer umwandeln
                            int selectedShipSize = Integer.parseInt(db.getString());
                            
                            // Finde die Position der aktuellen Zelle
                            int cellCol = GridPane.getColumnIndex(cell);
                            int cellRow = GridPane.getRowIndex(cell);

                            // Überprüfe, ob das Schiff innerhalb des Rasters passt
                            boolean shipFitsInGrid = doesShipFit(
                                    isVertical ? cellRow : cellCol,
                                    selectedShipSize,
                                    GRID_SIZE,
                                    isVertical
                            );

                            System.out.print("\n");
                            System.out.print("Passt das Schiff: " + shipFitsInGrid);

                            if (shipFitsInGrid) {
                                success = true;

                                // Setze das Schiff auf die nächsten SHIP_SIZE Zellen
                                for (int i = 0; i < selectedShipSize; i++) {
                                    StackPane targetCell = isVertical
                                            ? (StackPane) gridPane.getChildren().get(((cellRow + i) * GRID_SIZE) + cellCol) // Vertikale Platzierung
                                            : (StackPane) gridPane.getChildren().get((cellRow * GRID_SIZE) + (cellCol - i)); // Horizontale Platzierung

                                    Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
                                    targetBorder.setFill(Color.rgb(0, 100, 0, 0.5)); // Dunkelgrün mit 50% Transparenz
                                }
                                // Speichere die Position des Schiffs
                                int[] positionOnGrid = new int[]{cellRow, cellCol};
                                for (Ship ship : ships) {
                                    if (ship.getSize() == selectedShipSize) {
                                        ship.setPosition(positionOnGrid);
                                        System.out.print(Arrays.toString(ship.getPosition()));
                                        break;
                                    }
                                }
                            }

                            event.setDropCompleted(success);
                            event.consume();
                        }
                    });

                }

                // Füge das Rectangle zum StackPane hinzu
                cell.getChildren().add(border);
                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }

}
