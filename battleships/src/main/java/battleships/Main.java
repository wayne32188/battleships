package battleships;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
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
    private static final int SHIP_SIZE = 5; // Beispielgröße für ein Schiff

    private boolean isVertical = false;

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

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) { // Drücke "R", um die Richtung zu ändern
                isVertical = !isVertical;
            }
        });
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
                            // Finde die Position der aktuellen Zelle
                            int cellCol = GridPane.getColumnIndex(cell);
                            int cellRow = GridPane.getRowIndex(cell);

                            // Überprüfe, ob das Schiff innerhalb des Rasters passt
                            boolean shipFitsInGrid = isVertical
                                    ? (cellRow + SHIP_SIZE <= GRID_SIZE) // Prüfe vertikale Platzierung
                                    : (cellCol + SHIP_SIZE <= GRID_SIZE); // Prüfe horizontale Platzierung

                            if (shipFitsInGrid) {
                                success = true;

                                // Setze das Schiff auf die nächsten SHIP_SIZE Zellen
                                for (int i = 0; i < SHIP_SIZE; i++) {
                                    StackPane targetCell = isVertical
                                            ? (StackPane) gridPane.getChildren().get(((cellRow + i) * GRID_SIZE) - cellCol) // Vertikale Platzierung
                                            : (StackPane) gridPane.getChildren().get((cellRow * GRID_SIZE) + (cellCol - i)); // Horizontale Platzierung

                                    Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
                                    targetBorder.setFill(Color.DARKGREEN);
                                }
                            }
                        }

                        event.setDropCompleted(success);
                        event.consume();
                    });

                }

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
            ship.setMouseTransparent(true); // Verhindert, dass das Schiff die Maus blockiert
            Dragboard db = ship.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Ship"); // Platzhalter, damit etwas übertragen wird
            db.setContent(content);
            event.consume();
        });

        ship.setOnMouseMoved(event -> {
            ship.setX(event.getSceneX() - ship.getWidth() / 2);
            ship.setY(event.getSceneY() - ship.getHeight() / 2);
        });

        ship.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                ship.setVisible(false); // Schiff verschwindet, wenn es platziert wurde
            }
            ship.setMouseTransparent(false); // Mausinteraktionen wieder aktivieren
            event.consume();
        });

        // Umschalten der Ausrichtung durch Taste "R"
        ship.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) { // Rotation bei "R"
                isVertical = !isVertical;
                if (isVertical) {
                    ship.setWidth(CELL_SIZE);
                    ship.setHeight(CELL_SIZE * size);
                } else {
                    ship.setWidth(CELL_SIZE * size);
                    ship.setHeight(CELL_SIZE);
                }
            }
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
