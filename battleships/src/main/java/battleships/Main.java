package battleships;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
    private boolean isVertical = false;
    ArrayList<Ship> ships = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        // Erstelle zwei GridPane-Instanzen für Spieler und Gegner
        // Spieler darf mit eigenem Feld direkt interagieren aber nicht mit Gegnerfeld
        GridPane playerField = createPlayerField();
        GridPane enemyField = createEnemyField();

        // Schiffe erstellen mit Größe 3, 4, 5 
        ships.add(new Ship(3, false));
        ships.add(new Ship(4, false));
        ships.add(new Ship(5, false));


        Button resetButton = new Button("Schiffe zurücksetzen");
        
        // Visuelle Darstellung der Schiffe erstellen
        VBox shipsBox = new VBox(10); // 10px Abstand zwischen den Schiffen
        for (Ship ship : ships) {
            Rectangle shipRectangle = ship.createShip(CELL_SIZE, isVertical);
            shipsBox.getChildren().add(shipRectangle);
        }
        
        resetButton.setOnAction(event -> { resetAllShips(playerField); });
        

        // Erstelle eine HBox, um die Grids und die Schiffe nebeneinander anzuordnen
        HBox gridsBox = new HBox(20); // 20px Abstand zwischen den Grids
        gridsBox.getChildren().addAll(playerField, enemyField);

        // Erstelle eine VBox, um die Grids und die Schiffe untereinander anzuordnen
        VBox vbox = new VBox(10); // 10px Abstand zwischen den Grids und der Schiff-Box
        vbox.getChildren().addAll(gridsBox, shipsBox, resetButton);

        
        // Erstelle die Szene
        Scene scene = new Scene(vbox, GRID_SIZE * CELL_SIZE * 2 + 100, GRID_SIZE * CELL_SIZE + 150);

        // Rotation durch Taste "R"
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                isVertical = !isVertical;
                for (Ship ship : ships) {
                    ship.updateOrientation(isVertical); // Schiffe visuell aktualisieren
                }
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
                        // Schiffgröße vom String zurück zum Integer umwandeln
                        int selectedShipSize = Integer.parseInt(db.getString());
                        boolean success = false;

                        if (db.hasString()) {
                            // Finde die Position der aktuellen Zelle
                            int cellCol = GridPane.getColumnIndex(cell);
                            int cellRow = GridPane.getRowIndex(cell);

                            // Überprüfe, ob das Schiff innerhalb des Rasters passt
                            boolean shipFitsInGrid = isVertical
                                    ? (cellRow + selectedShipSize <= GRID_SIZE) // Prüfe vertikale Platzierung
                                    : (cellCol + selectedShipSize <= GRID_SIZE); // Prüfe horizontale Platzierung

                            if (shipFitsInGrid) {
                                success = true;

                                // Setze das Schiff auf die nächsten SHIP_SIZE Zellen
                                for (int i = 0; i < selectedShipSize; i++) {
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

    // Dummy Methode zum Erstellen des Spielerfelds
    private GridPane createPlayerField() {
        return createGridPane(true);
    }

    // Dummy Methode zum Erstellen des Gegnerfelds
    private GridPane createEnemyField() {
        return createGridPane(false);
    }

    private void resetAllShips(GridPane playerField) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int cellIndex = row * GRID_SIZE + col;
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
    
    
    
    

    // Hilfsmethode zum Anzeigen einer Nachricht
    @SuppressWarnings("unused")
    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}