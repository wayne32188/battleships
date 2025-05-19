package battleships.lib;

import java.util.ArrayList;

import battleships.Ship;

public class Helper {

    public static boolean doesShipFit(int cellColOrRow, int ship_size, int grid_size, boolean isVertical) {
        return isVertical
                ? doesVerticalShipFit(cellColOrRow, ship_size, grid_size)
                : doesHorizontalShipFit(cellColOrRow, ship_size);
    }

    private static boolean doesHorizontalShipFit(int cellCol, int ship_size) {
        return cellCol >= (ship_size - 1);
    }

    private static boolean doesVerticalShipFit(int cellRow, int shipSize, int gridSize) {
        return (cellRow + shipSize) <= gridSize;
    }

    
    public static boolean isOverlapping(int startRow, int startCol, int size, boolean isVertical,
            ArrayList<Ship> ships) {
        for (Ship ship : ships) {
            int[] pos = ship.getPosition();
            if (pos == null)
                continue;

            int shipRow = pos[0];
            int shipCol = pos[1];

            for (int i = 0; i < ship.getSize(); i++) {
                int occupiedRow = ship.isVertical() ? shipRow + i : shipRow;
                int occupiedCol = ship.isVertical() ? shipCol : shipCol - i;

                for (int j = 0; j < size; j++) {
                    int newRow = isVertical ? startRow + j : startRow;
                    int newCol = isVertical ? startCol : startCol - j;

                    if (newRow == occupiedRow && newCol == occupiedCol) {
                        System.out.println("Schiff überlappt mit einem anderen!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
