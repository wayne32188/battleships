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

public class SecondaryController {

    @FXML
    private GridPane playerPlayingField;

    @FXML
    private GridPane enemyPlayingField;

    @FXML
    private HBox playingGridBox;

    private static GameHandler gameHandler;

    private static final Dotenv dotenv = Dotenv.load();

    @FXML
    private void initialize() {

        ArrayList<Ship> ships = ShipPlacementController.getShips();

        playerPlayingField = createGameGrid(true, ships);
        enemyPlayingField = createGameGrid(false, null);

        playingGridBox.getChildren().addAll(playerPlayingField, enemyPlayingField);

        System.out.println("SecondaryController initialized");

        gameHandler = new GameHandler(true);
    }

    public static GridPane createGameGrid(boolean isPlayerGrid, ArrayList<Ship> ships) {
        GridPane gridPane = new GridPane();
        StackPane[][] cells = new StackPane[Main.GRID_SIZE][Main.GRID_SIZE];

        for (int row = 0; row < Main.GRID_SIZE; row++) {
            for (int col = 0; col < Main.GRID_SIZE; col++) {
                StackPane cell = createClickableCell(row, col, isPlayerGrid);
                gridPane.add(cell, col, row);
                cells[row][col] = cell;
            }
        }

        if (isPlayerGrid) {
            drawShips(gridPane, ships);
        }

        return gridPane;
    }

    private static StackPane createClickableCell(int row, int col, boolean isPlayerGrid) {
        StackPane cell = ShipPlacementController.createCell();
        if (!isPlayerGrid)
            addMouseClickEvent(cell, row, col);
        return cell;
    }

    private static void addMouseClickEvent(StackPane cell, int row, int col) {
        cell.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("Zelle (" + row + ", " + col + ") wurde ausgewählt");

            Rectangle border = getCellBorder(cell);
            if (border != null) {
                // Überprüfen, ob die Zelle Teil eines Schiffs ist
                for (Ship ship : ShipPlacementController.getShips()) {
                    if (ship.isHit(row, col) && gameHandler.isHostTurn()) {
                        // Zelle ist Teil eines Schiffs und wurde getroffen
                        border.setFill(Color.web(dotenv.get("CELL_HIT_COLOR"))); // Markiere die Zelle rot
                        System.out.println("Schiff getroffen!");
                        return;
                    } else {
                        // Wenn die Zelle kein Teil eines Schiffs ist, markiere sie grün oder
                        // transparent
                        border.setFill(Color.web(dotenv.get("CELL_MISSED_COLOR")));
                    }
                }

            }
        });
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
