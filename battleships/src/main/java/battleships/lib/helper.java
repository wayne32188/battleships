package battleships.lib;

public class Helper {

    public boolean doesShipFit(int cellColOrRow, int ship_size, int grid_size, boolean isVertical) {

        return isVertical
                ? doesVerticalShipFit(cellColOrRow, ship_size, grid_size)
                : doesHorizontalShipFit(cellColOrRow, ship_size, grid_size);

    }

    private boolean doesHorizontalShipFit(int cellCol, int ship_size, int grid_size) {
        return cellCol >= (ship_size - 1);
    }

    private boolean doesVerticalShipFit(int cellRow, int shipSize, int gridSize) {
        return (cellRow + (shipSize - 1)) <= gridSize;
    }
}
