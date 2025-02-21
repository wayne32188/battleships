package battleships.lib;

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
        return (cellRow + (shipSize - 1)) <= gridSize;
    }
}
