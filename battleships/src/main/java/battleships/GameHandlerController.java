package battleships;

import java.io.IOException;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameHandlerController {

    @FXML
    private GridPane playerPlayingField;

    @FXML
    private GridPane enemyPlayingField;

    @FXML
    private HBox playingGridBox;

    private static GameHandler gameHandler;
    public static ArrayList<Ship> playerShips = new ArrayList<>();

    private static StackPane[][] enemyCells = new StackPane[Main.GRID_SIZE][Main.GRID_SIZE];
    public static StackPane[][] playerCells = new StackPane[Main.GRID_SIZE][Main.GRID_SIZE];

    private static final Dotenv dotenv = Dotenv.load();

    @FXML
    private void initialize() {

        playerShips = ShipPlacementController.getShips();

        gameHandler = new GameHandler(true, playerShips);

        playerPlayingField = createGameGrid(true, playerShips);
        enemyPlayingField = createGameGrid(false, null);

        playingGridBox.getChildren().addAll(playerPlayingField, enemyPlayingField);

        System.out.println("GameHandlerController initialized");

    }

    public static GridPane createGameGrid(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();
        StackPane[][] cells = new StackPane[Main.GRID_SIZE][Main.GRID_SIZE];

        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                StackPane cell = isPlayerGrid
                        ? createNormalCell()
                        : createClickableCell(row, col);
                gridPane.add(cell, col, row);
                cells[row][col] = cell;
            }
        }

        if (!isPlayerGrid) {
            enemyCells = cells;
        } else {
            playerCells = cells;
            drawShips(gridPane, ships);
        }

        return gridPane;
    }

    private static StackPane createClickableCell(int row, int col) {
        StackPane cell = ShipPlacementController.createCell();
        addMouseClickEvent(cell, row, col);
        return cell;
    }

    private static StackPane createNormalCell() {
        return ShipPlacementController.createCell();
    }

    private static void addMouseClickEvent(StackPane cell, int row, int col) {
        cell.setOnMouseClicked((MouseEvent event) -> {

            if (!gameHandler.isRunning()) {
                System.out.println("Das Spiel ist nicht aktiv!");
                return;
            }

            if (gameHandler.isHostTurn()) {
                String cellKey = row + "," + col;
                // Überprüfen, ob die Zelle bereits beschossen wurde
                if (gameHandler.isCellAlreadyShot(cellKey)) {
                    System.out.println("Zelle (" + row + ", " + col + ") wurde bereits beschossen!");
                    return;
                }
                System.out.println("Zelle (" + row + ", " + col + ") wurde ausgewählt");
                // Prüfen, ob das Schiff getroffen wurde
                boolean isHit = gameHandler.hitShip(false, row, col);

                // Markiere die Zelle als getroffen oder verfehlt
                markCellHitOrMiss(cell, isHit);
                gameHandler.addShotCell(row, col);
                ; // Zelle als beschossen speichern
                gameHandler.playerMove(); // Spielerzug beenden
            }
        });
    }

    public static void handleEnemyMove(StackPane cell, boolean hit) {
        markCellHitOrMiss(cell, hit);

    }

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

    private static Rectangle getCellBorder(StackPane cell) {
        return (Rectangle) cell.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .findFirst()
                .orElse(null);
    }

    private static void drawShips(GridPane gridPane, ArrayList<Ship> ships) {
        for (Ship ship : ships) {
            int[] shipPositionOnGrid = ship.getPosition();
            int cellRow = shipPositionOnGrid[0];
            int cellCol = shipPositionOnGrid[1];

            for (int i = 0; i < ship.getSize(); i++) {
                StackPane targetCell = ship.isVertical()
                        ? (StackPane) gridPane.getChildren().get(((cellRow + i) * Main.GRID_SIZE) + cellCol)
                        : (StackPane) gridPane.getChildren().get((cellRow * Main.GRID_SIZE) + (cellCol - i));

                Rectangle targetBorder = (Rectangle) targetCell.getChildren().get(0);
                targetBorder.setFill(Color.rgb(0, 100, 0, 1));

            }
        }
    }

    @FXML
    private void buttonHandler() throws IOException {
        Main.setRoot("ShipPlacement");
    }
}
