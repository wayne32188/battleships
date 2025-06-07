package battleships.lib;

import java.util.ArrayList;

import battleships.App;


public class Helper {

    public static boolean doesShipFit(int cellColOrRow, int ship_size, int grid_size, boolean isVertical) {
        return isVertical
                ? doesVerticalShipFit(cellColOrRow, ship_size, grid_size)
                : doesHorizontalShipFit(cellColOrRow, ship_size);
    }

    public static boolean cellIsInGrid(int cellRow, int cellCol) {
        return cellRow >= 0 && cellCol < App.GRID_SIZE && cellCol >= 0 && cellCol < App.GRID_SIZE;
    }

    private static boolean doesHorizontalShipFit(int cellCol, int ship_size) {
        return cellCol >= (ship_size - 1);
    }

    private static boolean doesVerticalShipFit(int cellRow, int shipSize, int gridSize) {
        return (cellRow + shipSize) <= gridSize;
    }

    public static boolean isOverlapping(int cellRow, int cellCol, int shipSize, boolean isVertical,
            ArrayList<int[]> occupiedCells) {

        for (int i = 0; i < shipSize; i++) {
            int row = isVertical ? cellRow + i : cellRow;
            int col = isVertical ? cellCol : cellCol - i;

            // Prüfen, ob diese Koordinate schon belegt ist
            for (int[] coord : occupiedCells) {
                if (coord[0] == row && coord[1] == col) {
                    return true; // Überlappung gefunden
                }
            }
        }
        return false; // Keine Überlappung
    }

}
