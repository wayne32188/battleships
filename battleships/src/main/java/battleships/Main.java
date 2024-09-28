package battleships;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * JavaFX App
 *
 */
public class Main extends Application {

    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 50;
    private static final int BORDER_WIDTH = 1;

    private boolean placingShip = false;  // Status, ob gerade ein Schiff platziert wird
    private Ship currentShip;             // Das Schiff, das platziert wird

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Erstelle zwei identische GridPane-Instanzen
        GridPane playerField = createGridPane(true);
        GridPane enemyField = createGridPane(false);

        // Erstelle eine HBox, um die Grids nebeneinander anzuordnen
        HBox gridsBox = new HBox(20); // 20px Abstand zwischen den Grids
        gridsBox.getChildren().addAll(playerField, enemyField);

        // Erstelle eine HBox für die Buttons unter den Grids
        HBox buttonBox = new HBox(10); // 10px Abstand zwischen den Buttons
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");
        Button button3 = new Button("Button 3");

        // Füge die Buttons zur HBox hinzu
        buttonBox.getChildren().addAll(button1, button2, button3);

        // Erstelle eine VBox, um die Grids und die Buttons untereinander anzuordnen
        // Eine VBox ist ein Container, damit das Grid und die Buttons untereinander angeordnet werden
        VBox vbox = new VBox(10); // 10px Abstand zwischen den Grids und der Button-Box
        vbox.getChildren().addAll(gridsBox, buttonBox);

        // Erstelle die Szene
        Scene scene = new Scene(vbox, GRID_SIZE * CELL_SIZE * 2 + 40, GRID_SIZE * CELL_SIZE + 50); // Platz für 2 Grids und Buttons einplanen
        primaryStage.setScene(scene);
        primaryStage.setTitle("Zwei 10x10 Grids mit Buttons");
        primaryStage.show();
    }

    // Methode zur Erstellung eines GridPane
    private GridPane createGridPane(boolean isPlayerGrid) {
        GridPane gridPane = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane cell = new StackPane();

                // Hier jedes Mal ein neues Rectangle erstellen
                Rectangle border = new Rectangle(CELL_SIZE, CELL_SIZE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                border.setStrokeWidth(BORDER_WIDTH);
                
                // Füge den Listener für den Mausklick hinzu
                final int currentRow = row; // Nur für den print, weil Variablen in Lambda final sein müssen
                final int currentCol = col; 
                
                if (isPlayerGrid) {
                    cell.setOnMouseClicked((MouseEvent event) -> {
                        System.out.println("Zelle (" + currentRow + ", " + currentCol + ") wurde geklickt");
                        if (border.getFill() == Color.LIGHTGREEN) {
                            border.setFill(Color.TRANSPARENT);
                        } else {
                            border.setFill(Color.LIGHTGREEN);
                        }
                    });
                }

                // Hier das Rectangle dem StackPane hinzufügen
                cell.getChildren().add(border);
                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
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
