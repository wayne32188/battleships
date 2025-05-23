package battleships;

import static battleships.lib.Helper.doesShipFit;
import static battleships.lib.Helper.isOverlapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ShipPlacementController {

    @FXML
    private GridPane playerField;

    @FXML
    private GridPane enemyField;

    @FXML
    private VBox shipsBox;

    @FXML
    private HBox gridBox;

    @FXML
    private Text rotationHint;

    private static final ArrayList<Ship> ships = new ArrayList<>();
    public static ArrayList<int[]> occupiedCells = new ArrayList<>();

    private static final Dotenv dotenv = Dotenv.load();

    public static boolean isVertical = false;

    @FXML
    public void initialize() {

        // Erstelle zwei GridPane-Instanzen für Spieler und Gegner
        playerField = createPlayerField();
        enemyField = createEnemyField();

        final boolean IS_VERTICAL = false;
        setRotationText(IS_VERTICAL);

        gridBox.getChildren().addAll(playerField, enemyField);

        // Schiffe erstellen mit Größe 3, 4, 5
        ships.add(new Ship(3, false));
        ships.add(new Ship(4, false));
        ships.add(new Ship(5, false));

        // Visuelle Darstellung der Schiffe erstellen
        for (Ship ship : ships) {
            Rectangle shipRectangle = ship.createShip(Main.CELL_SIZE, IS_VERTICAL);
            shipsBox.getChildren().add(shipRectangle);
        }
    }

    @FXML
    private void startGame() throws IOException {
        if (areShipsPlaced()) {
            System.out.println("Belegte Zellen (row, col):");
            for (int i = 0; i < occupiedCells.size(); i++) {
                int[] coord = occupiedCells.get(i);
                System.out.println((i + 1) + ". (" + coord[0] + ", " + coord[1] + ")");
            }
            Main.setRoot("PlayingField");
        }
    }

    private GridPane createPlayerField() {
        return createGridPane(true, ships);
    }

    private GridPane createEnemyField() {
        return createGridPane(false, null);
    }

    protected void setRotationText(boolean isVertical) {
        if (isVertical) {
            rotationHint.setText("Vertical");
        } else {
            rotationHint.setText("Horizontal");
        }
    }

    @FXML
    private void resetAllShips() {
        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                int cellIndex = row * Main.GRID_SIZE + col;
                StackPane cell = (StackPane) playerField.getChildren().get(cellIndex);

                // Überprüfe die Farbe der Zelle
                Rectangle border = (Rectangle) cell.getChildren().get(0);
                if (!border.getFill().equals(Color.web(dotenv.get("CELL_BG_COLOR")))) {
                    // Setze die Zellenfarbe zurück auf blau
                    border.setFill(Color.web(dotenv.get("CELL_BG_COLOR")));
                }
            }
        }

        // Alle Schiffe wieder sichtbar machen und Startpositionen zurücksetzen
        for (Ship ship : ships) {
            ship.getShipVisuals().setVisible(true); // Sichtbarkeit wiederherstellen
        }
    }

    private boolean areShipsPlaced() {
        boolean allShipsPlaced = true;
        for (Ship ship : ships) {
            if (ship.getPosition() == null) {
                allShipsPlaced = false;
                break;
            }
        }

        if (allShipsPlaced) {
            System.out.println("\nAll ships placed!");
            return true;
        }
        System.out.println("\nNot all ships placed!");
        return false;
    }

    public static ArrayList<Ship> getShips() {
        return ships;
    }

    private static GridPane createGridPane(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                StackPane cell = createCell();

                if (isPlayerGrid) {
                    addDragAndDropHandlers(cell, gridPane, ships);
                }

                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }

    public static StackPane createCell() {

        StackPane cell = new StackPane();
        Rectangle border = new Rectangle(Main.CELL_SIZE, Main.CELL_SIZE);
        border.setFill(Color.web(dotenv.get("CELL_BG_COLOR")));
        border.setStroke(Color.web(dotenv.get("CELL_STROKE_COLOR")));
        border.setStrokeWidth(Main.BORDER_WIDTH);
        cell.getChildren().add(border);
        return cell;
    }

    private static void addDragAndDropHandlers(StackPane cell, GridPane gridPane, ArrayList<Ship> ships) {
        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragDropped(event -> handleDragDropped(event, cell, gridPane, ships));
    }

    private static void handleDragDropped(DragEvent event, StackPane cell, GridPane gridPane, ArrayList<Ship> ships) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            int selectedShipSize = Integer.parseInt(db.getString());
            int cellCol = GridPane.getColumnIndex(cell);
            int cellRow = GridPane.getRowIndex(cell);

            boolean shipFitsInGrid = doesShipFit(
                    isVertical ? cellRow : cellCol,
                    selectedShipSize,
                    Main.GRID_SIZE,
                    isVertical);

            if (isOverlapping(cellRow, cellCol, selectedShipSize, isVertical, occupiedCells)) {
                System.out.println("Das Schiff überlappt mit einem anderen!");
                return;
            }
            if (shipFitsInGrid) {
                success = true;
                placeShip(gridPane, cellRow, cellCol, selectedShipSize);
                saveShipPosition(ships, selectedShipSize, cellRow, cellCol);
            } else {
                System.out.println("Das Schiff passt nicht ins Grid.");
            }

            event.setDropCompleted(success);
            event.consume();
        }
    }

    private static void placeShip(GridPane gridPane, int cellRow, int cellCol, int shipSize) {
        for (int i = 0; i < shipSize; i++) {
            int row = isVertical ? cellRow + i : cellRow;
            int col = isVertical ? cellCol : cellCol - i;

            // Koordinaten zur Liste hinzufügen
            occupiedCells.add(new int[] { row, col });

            // Zelle einfärben
            StackPane targetCell = (StackPane) gridPane.getChildren().get((row * Main.GRID_SIZE) + col);
            Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
            targetBorder.setFill(Color.rgb(0, 100, 0, 0.5));
        }
    }

    private static void saveShipPosition(ArrayList<Ship> ships, int shipSize, int cellRow, int cellCol) {
        int[] positionOnGrid = new int[] { cellRow, cellCol };
        for (Ship ship : ships) {
            if (ship.getSize() == shipSize) {
                ship.setRotation(isVertical);
                ship.setPosition(positionOnGrid);
                System.out.println("Schiff gespeichert auf Koordinaten:");
                System.out.println(Arrays.toString(positionOnGrid));
                break;
            }
        }
    }
}
