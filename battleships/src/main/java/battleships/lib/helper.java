package battleships.lib;

public class helper {


    public static boolean doesShipFit(int cell, int SHIP_SIZE, int GRID_SIZE) {
        return cell + SHIP_SIZE <= GRID_SIZE;
    }
}
