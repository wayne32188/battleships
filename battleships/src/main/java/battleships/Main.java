package battleships;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * JavaFX App mit Drag-and-Drop-Schiffen
 */
public class Main extends Application {

    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 50;
    private static final int BORDER_WIDTH = 1;
    private static final int SHIP_SIZE = 3; // Beispielgröße für ein Schiff

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Erstelle zwei identische GridPane-Instanzen
        GridPane playerField = createPlayerField();
        GridPane enemyField = createEnemyField();

        // Schiffe, die gezogen werden können
        Rectangle ship = createShip(SHIP_SIZE);

        // Erstelle eine HBox, um die Grids und das Schiff nebeneinander anzuordnen
        HBox gridsBox = new HBox(20); // 20px Abstand zwischen den Grids
        gridsBox.getChildren().addAll(playerField, enemyField);

        // Erstelle eine VBox, um die Grids und das Schiff untereinander anzuordnen
        VBox vbox = new VBox(10); // 10px Abstand zwischen den Grids und der Button-Box
        vbox.getChildren().addAll(gridsBox, ship);

        // Erstelle die Szene
        Scene scene = new Scene(vbox, GRID_SIZE * CELL_SIZE * 2 + 100, GRID_SIZE * CELL_SIZE + 150); // Platz für 2 Grids und das Schiff
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drag-and-Drop Schiffe auf einem 10x10 Grid");
        primaryStage.show();
    }

    // Methode zur Erstellung eines GridPane
    private GridPane createGridPane(boolean isPlayerGrid) {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane cell = new StackPane();

                // Erstelle ein Rectangle für die Zellen
                Rectangle border = new Rectangle(CELL_SIZE, CELL_SIZE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                border.setStrokeWidth(BORDER_WIDTH);

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
                        border.setFill(Color.LIGHTBLUE); // Zeige an, dass das Schiff platziert wurde
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                // Füge das Rectangle zum StackPane hinzu
                cell.getChildren().add(border);
                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }

    // Methode zur Erstellung eines Schiffs (als Rechteck)
    private Rectangle createShip(int size) {
        Rectangle ship = new Rectangle(CELL_SIZE * size, CELL_SIZE);
        ship.setFill(Color.GRAY);

        // Drag-and-Drop-Ereignis für das Schiff
        ship.setOnDragDetected(event -> {
            Dragboard db = ship.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Ship"); // Platzhalter, damit etwas übertragen wird
            db.setContent(content);
            event.consume();
        });

        ship.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                ship.setVisible(false); // Schiff verschwindet, wenn es platziert wurde
            }
            event.consume();
        });

        return ship;
    }

    // Dummy Methode zum erstellen des Spieler Felds, zur besseren Lesbarkeit
    private GridPane createPlayerField() {
        return createGridPane(true);
    }

    // Dummy Methode zum erstellen des Gegner Felds, zur besseren Lesbarkeit
    private GridPane createEnemyField() {
        return createGridPane(false);
    }

    // Hilfsmethode zum Anzeigen einer Nachricht
    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
