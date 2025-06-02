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
import battleships.lib.enums.Difficulty;


/**
 * Controller für das Schiffe-platzieren-Fenster.
 * Verwaltet die Anzeige, Drag&Drop und Validierung der Schiffsplatzierung.
 */
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

    @FXML
    private Text infoBox;

    @FXML
    private Text errorText;

    @FXML
    private Text currentDifficulty;
    public static Difficulty difficulty = Difficulty.EASY; // Standard-Schwierigkeitsgrad

    /** Instanz für statischen Zugriff aus Hilfsmethoden. */
    private static ShipPlacementController currentControllerInstance;

    /** Liste aller Schiffe, die platziert werden sollen. */
    private static final ArrayList<Ship> ships = new ArrayList<>();

    /** Liste aller belegten Zellen (row, col) auf dem Spielfeld. */
    public static ArrayList<int[]> occupiedCells = new ArrayList<>();

    /** 2D-Array für schnellen Zugriff auf die Zellen des Spielerfelds. */
    private static StackPane[][] playerCells = new StackPane[App.GRID_SIZE][App.GRID_SIZE];

    /** Zugriff auf Umgebungsvariablen (z.B. Farben). */
    private static final Dotenv dotenv = Dotenv.load();

    /** Gibt an, ob das aktuell platzierte Schiff vertikal ist. */
    public static boolean isVertical = false;

    /**
     * Initialisiert das Fenster, erzeugt Schiffe und setzt Infotexte.
     */
    @FXML
    public void initialize() {
        currentControllerInstance = this;

        // Erstelle zwei GridPane-Instanzen für Spieler und Gegner
        playerField.getChildren().setAll(createPlayerField().getChildren());
        enemyField.getChildren().setAll(createEnemyField().getChildren());

        final boolean IS_VERTICAL = false;
        setRotationText(IS_VERTICAL);

        // Schiffe erstellen mit Größe 3, 4, 5
        ships.add(new Ship(3, false));
        ships.add(new Ship(4, false));
        ships.add(new Ship(5, false));

        // Visuelle Darstellung der Schiffe erstellen
        for (Ship ship : ships) {
            StackPane shipRectangle = ship.createShipVisuals(App.CELL_SIZE, IS_VERTICAL);
            shipsBox.getChildren().add(shipRectangle);
        }

        // Info-Text für den Spieler
        infoBox.setText("Ziehen Sie die Schiffe auf das Spielfeld und platzieren Sie sie dort.\n" +
                "Klicken Sie auf 'Reset', um alle Schiffe zurückzusetzen.\n" +
                "Horizontale Schiffe werden von rechts nach links platziert, vertikale von oben nach unten.\n" +
                "Die Schiffe dürfen sich nicht überlappen und müssen vollständig im Spielfeld platziert werden.\n");

        errorText.setFill(Color.RED);
        errorText.setVisible(false);
    }

    /**
     * Startet das Spiel, wenn alle Schiffe platziert wurden.
     * 
     * @throws IOException falls das Spielfeld nicht geladen werden kann.
     */
    @FXML
    private void startGame() throws IOException {
        if (areShipsPlaced()) {
            System.out.println("Belegte Zellen (row, col):");
            for (int i = 0; i < occupiedCells.size(); i++) {
                int[] coord = occupiedCells.get(i);
                System.out.println((i + 1) + ". (" + coord[0] + ", " + coord[1] + ")");
            }
            App.setRoot("PlayingField");
        }
    }

    @FXML
    private void onEasy() {
        currentDifficulty.setText("Easy");
        difficulty = Difficulty.EASY;
    }

    @FXML
    private void onMedium() {
        currentDifficulty.setText("Medium");
        difficulty = Difficulty.MEDIUM;
    }

    @FXML
    private void onHard() {
        currentDifficulty.setText("Hard");
        difficulty = Difficulty.HARD;
    }

    /**
     * Erstellt das Spielerfeld mit Drag&Drop-Unterstützung.
     * 
     * @return Das GridPane für das Spielerfeld.
     */
    private GridPane createPlayerField() {
        return createGridPane(true, ships);
    }

    /**
     * Erstellt das Gegnerfeld (ohne Drag&Drop).
     * 
     * @return Das GridPane für das Gegnerfeld.
     */
    private GridPane createEnemyField() {
        return createGridPane(false, null);
    }

    /**
     * Setzt den Rotationstext für die Anzeige.
     * 
     * @param isVertical True, wenn vertikal.
     */
    protected void setRotationText(boolean isVertical) {
        if (isVertical) {
            rotationHint.setText("Vertical");
        } else {
            rotationHint.setText("Horizontal");
        }
    }

    /**
     * Setzt das Spielfeld und die Schiffe zurück.
     * Setzt belegte Zellen und Sichtbarkeit der Schiffe zurück.
     */
    @FXML
    private void resetAllShips() {
        occupiedCells.clear(); // Belegte Zellen zurücksetzen

        for (int row = 0; row < App.GRID_SIZE; row++) {
            for (int col = 0; col < App.GRID_SIZE; col++) {
                int cellIndex = row * App.GRID_SIZE + col;
                StackPane cell = (StackPane) playerField.getChildren().get(cellIndex);

                // Überprüfe die Farbe der Zelle
                Rectangle border = (Rectangle) cell.getChildren().get(0);
                if (!border.getFill().equals(Color.web(dotenv.get("CELL_BG_COLOR")))) {
                    border.setFill(Color.web(dotenv.get("CELL_BG_COLOR")));
                }
            }
        }
        currentControllerInstance.errorText.setVisible(false); // Fehlertext ausblenden

        // Alle Schiffe wieder sichtbar machen und Startpositionen zurücksetzen
        for (Ship ship : ships) {
            if (ship.getShipPane() != null) {
                ship.getShipPane().setVisible(true);
            }
            ship.setPosition(null);
        }
    }

    /**
     * Prüft, ob alle Schiffe platziert wurden.
     * 
     * @return True, wenn alle platziert sind.
     */
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

    /**
     * Gibt die aktuelle Schiffsliste zurück.
     * 
     * @return Liste der Schiffe.
     */
    public static ArrayList<Ship> getShips() {
        return ships;
    }

    /**
     * Zeigt einen Fehlertext im UI an.
     * 
     * @param text Der anzuzeigende Text.
     */
    private static void setErrorText(String text) {
        currentControllerInstance.errorText.setText(text);
        currentControllerInstance.errorText.setVisible(true);
    }

    /**
     * Erstellt ein GridPane für das Spieler- oder Gegnerfeld.
     * 
     * @param isPlayerGrid True, wenn Spielerfeld.
     * @param ships        Die Schiffe (nur für Spielerfeld).
     * @return Das erzeugte GridPane.
     */
    private static GridPane createGridPane(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < App.GRID_SIZE; row++) {
            for (int col = 0; col < App.GRID_SIZE; col++) {
                StackPane cell = createCell();

                if (isPlayerGrid) {
                    addDragAndDropHandlers(cell, gridPane, ships);
                    playerCells[row][col] = cell;
                }

                gridPane.add(cell, col, row);
            }
        }
        return gridPane;
    }

    /**
     * Erstellt eine einzelne Zelle für das Grid.
     * 
     * @return Die StackPane-Zelle.
     */
    public static StackPane createCell() {
        StackPane cell = new StackPane();
        Rectangle border = new Rectangle(App.CELL_SIZE, App.CELL_SIZE);
        border.setFill(Color.web(dotenv.get("CELL_BG_COLOR")));
        border.setStroke(Color.web(dotenv.get("CELL_STROKE_COLOR")));
        border.setStrokeWidth(App.BORDER_WIDTH);
        cell.getChildren().add(border);
        return cell;
    }

    /**
     * Fügt Drag&Drop-Handler zu einer Zelle hinzu.
     * 
     * @param cell     Die Zelle.
     * @param gridPane Das GridPane.
     * @param ships    Die Schiffe.
     */
    private static void addDragAndDropHandlers(StackPane cell, GridPane gridPane, ArrayList<Ship> ships) {
        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragDropped(event -> handleDragDropped(event, cell, gridPane, ships));
    }

    /**
     * Behandelt das Ablegen eines Schiffs auf einer Zelle.
     * 
     * @param event    Das DragEvent.
     * @param cell     Die Zielzelle.
     * @param gridPane Das GridPane.
     * @param ships    Die Schiffe.
     */
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
                    App.GRID_SIZE,
                    isVertical);

            if (isOverlapping(cellRow, cellCol, selectedShipSize, isVertical, occupiedCells)) {
                setErrorText("Das Schiff überlappt mit einem anderen!");
                return;
            }

            if (shipFitsInGrid) {
                success = true;
                placeShip(gridPane, cellRow, cellCol, selectedShipSize);
                saveShipPosition(ships, selectedShipSize, cellRow, cellCol);
                currentControllerInstance.errorText.setVisible(false); // Fehlertext ausblenden bei Erfolg
            } else {
                setErrorText("Das Schiff passt nicht ins Spielfeld.");
            }

            event.setDropCompleted(success);
            event.consume();
        }
    }

    /**
     * Platziert ein Schiff auf dem Grid und markiert die belegten Zellen.
     * 
     * @param gridPane Das GridPane.
     * @param cellRow  Startzeile.
     * @param cellCol  Startspalte.
     * @param shipSize Größe des Schiffs.
     */
    private static void placeShip(GridPane gridPane, int cellRow, int cellCol, int shipSize) {
        for (int i = 0; i < shipSize; i++) {
            int row = isVertical ? cellRow + i : cellRow;
            int col = isVertical ? cellCol : cellCol - i; // Achtung: ggf. zu cellCol + i ändern!

            occupiedCells.add(new int[] { row, col });

            StackPane targetCell = playerCells[row][col];
            Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
            targetBorder.setFill(Color.rgb(0, 100, 0, 0.5));
        }
    }

    /**
     * Speichert die Position eines platzierten Schiffs.
     * 
     * @param ships    Die Schiffe.
     * @param shipSize Die Größe des platzierten Schiffs.
     * @param cellRow  Startzeile.
     * @param cellCol  Startspalte.
     */
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