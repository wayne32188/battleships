package battleships;

import java.io.IOException;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller für das Spielfeld während des Spiels.
 * Verwaltet die Anzeige und Interaktion für Spieler- und Gegnerfeld,
 * sowie die Markierung von Treffern und Fehlschüssen.
 */
public class GameHandlerController {

    @FXML
    private GridPane playerPlayingField;

    @FXML
    private GridPane enemyPlayingField;

    @FXML
    private HBox playingGridBox;

    /** Referenz auf den GameHandler, der die Spiellogik verwaltet. */
    private static GameHandler gameHandler;

    /** Liste der Schiffe des Spielers. */
    public static ArrayList<Ship> playerShips = new ArrayList<>();

    /**
     * 2D-Array der StackPane-Zellen des Spielerfelds.
     * Wird auch vom GameHandler verwendet, um Treffer zu markieren.
     */
    public static StackPane[][] playerCells = new StackPane[App.GRID_SIZE][App.GRID_SIZE];

    /** Zugriff auf Umgebungsvariablen (z.B. Farben). */
    private static final Dotenv dotenv = Dotenv.load();


    /**
     * Initialisiert das Spielfeld, lädt die Schiffe und füllt beide Grids.
     */
    @FXML
    private void initialize() {
        playerShips = ShipPlacementController.getShips();
        gameHandler = new GameHandler(true, playerShips);

        // Fülle die vorhandenen GridPanes mit Zellen
        fillGridPane(playerPlayingField, true, playerShips);
        fillGridPane(enemyPlayingField, false, null);

        System.out.println("GameHandlerController initialized");
    }

    /**
     * Füllt das angegebene GridPane mit Zellen und zeichnet ggf. die Schiffe ein.
     * 
     * @param gridPane     Das zu füllende GridPane.
     * @param isPlayerGrid True, wenn es das Spielerfeld ist.
     * @param ships        Die Schiffe, die ggf. eingezeichnet werden sollen.
     */
    private void fillGridPane(GridPane gridPane, boolean isPlayerGrid, ArrayList<Ship> ships) {
        gridPane.getChildren().clear(); // Vorherige Kinder entfernen
        StackPane[][] cells = new StackPane[App.GRID_SIZE][App.GRID_SIZE];

        for (int row = 0; row < App.GRID_SIZE; row++) {
            for (int col = 0; col < App.GRID_SIZE; col++) {
                StackPane cell = isPlayerGrid
                        ? createNormalCell()
                        : createClickableCell(row, col);

                gridPane.add(cell, col, row);
                cells[row][col] = cell;
            }
        }

        if (isPlayerGrid) {
            playerCells = cells;
            drawShips(gridPane, ships);
        }
    }

    /**
     * Erstellt eine anklickbare Zelle für das Gegnerfeld.
     * 
     * @param row Zeile der Zelle.
     * @param col Spalte der Zelle.
     * @return Die erstellte StackPane.
     */
    private static StackPane createClickableCell(int row, int col) {
        StackPane cell = ShipPlacementController.createCell();
        addMouseClickEvent(cell, row, col);
        return cell;
    }

    /**
     * Erstellt eine normale (nicht anklickbare) Zelle für das Spielerfeld.
     * 
     * @return Die erstellte StackPane.
     */
    private static StackPane createNormalCell() {
        return ShipPlacementController.createCell();
    }

    /**
     * Fügt einer Zelle ein MouseClick-Event hinzu, um Schüsse auf das Gegnerfeld zu
     * ermöglichen.
     * 
     * @param cell Die Zelle.
     * @param row  Zeile der Zelle.
     * @param col  Spalte der Zelle.
     */
    private static void addMouseClickEvent(StackPane cell, int row, int col) {
        cell.setOnMouseClicked((MouseEvent event) -> {

            if (!gameHandler.isRunning()) {
                System.out.println("Das Spiel ist nicht aktiv!");
                return;
            }

            if (gameHandler.isHostTurn()) {
                String cellKey = row + "," + col;
                // Überprüfen, ob die Zelle bereits beschossen wurde
                if (gameHandler.isCellAlreadyShot(cellKey, false)) {
                    System.out.println("Zelle (" + row + ", " + col + ") wurde bereits beschossen!");
                    return;
                }
                // Prüfen, ob das Schiff getroffen wurde
                boolean isHit;
                isHit = gameHandler.hitShip(false, row, col);
                // Markiere die Zelle als getroffen oder verfehlt
                markCellHitOrMiss(cell, isHit);
                // Zelle als beschossen speichern
                gameHandler.addShotCell(row, col, false);

                gameHandler.endPlayerMove();
            }
        });
    }

    /**
     * Markiert eine Zelle nach einem gegnerischen Zug als getroffen oder verfehlt.
     * 
     * @param cell Die Zelle.
     * @param hit  True, wenn getroffen.
     */
    public static void handleEnemyMove(StackPane cell, boolean hit) {
        markCellHitOrMiss(cell, hit);
    }

    /**
     * Färbt eine Zelle je nach Trefferstatus ein.
     * 
     * @param cell Die Zelle.
     * @param hit  True, wenn getroffen.
     */
    private static void markCellHitOrMiss(StackPane cell, boolean hit) {
        Rectangle border = getCellBorder(cell);
        if (border != null) {
            if (hit) {
                border.setFill(Color.web(dotenv.get("CELL_HIT_COLOR")));
            } else {
                border.setFill(Color.web(dotenv.get("CELL_MISSED_COLOR")));
            }
        }
    }

    /**
     * Gibt das Rectangle (den Rand) einer Zelle zurück.
     * 
     * @param cell Die Zelle.
     * @return Das Rectangle-Objekt oder null.
     */
    private static Rectangle getCellBorder(StackPane cell) {
        return (Rectangle) cell.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .findFirst()
                .orElse(null);
    }

    /**
     * Zeichnet alle Schiffe auf das Spielerfeld.
     * 
     * @param gridPane Das Spielerfeld.
     * @param ships    Die zu zeichnenden Schiffe.
     */
    private static void drawShips(GridPane gridPane, ArrayList<Ship> ships) {
        for (Ship ship : ships) {
            int[] shipPositionOnGrid = ship.getPosition();
            int cellRow = shipPositionOnGrid[0];
            int cellCol = shipPositionOnGrid[1];

            for (int i = 0; i < ship.getSize(); i++) {
                StackPane targetCell = ship.isVertical()
                        ? (StackPane) gridPane.getChildren().get(((cellRow + i) * App.GRID_SIZE) + cellCol)
                        : (StackPane) gridPane.getChildren().get((cellRow * App.GRID_SIZE) + (cellCol - i));

                Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
                targetBorder.setFill(Color.rgb(0, 100, 0, 1));
            }
        }
    }

    /**
     * Handler für einen Button, der zurück zum Schiffe-platzieren-Screen führt.
     * 
     * @throws IOException falls das Laden fehlschlägt.
     */
    @FXML
    private void buttonHandler() throws IOException {
        App.setRoot("ShipPlacement");
    }
}