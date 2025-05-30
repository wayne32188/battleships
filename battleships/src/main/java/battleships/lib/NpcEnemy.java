package battleships.lib;

import static battleships.lib.Helper.isOverlapping;

import java.util.ArrayList;
import java.util.Random;

import battleships.App;
import battleships.Ship;

public class NpcEnemy {

    private ShootingStrategy strategy;

    /** Die vom NPC platzierten Schiffe. */
    private ArrayList<Ship> ships;

    public NpcEnemy(int difficulty) {

        ships = generateRandomShips();

        switch (difficulty) {
            case 1 -> strategy = new EasyStrategy();
            case 2 -> strategy = new MediumStrategy();
            case 3 -> strategy = new HardStrategy(); // optional
        }
    }

    public int[] shootAtEnemy() {
        return strategy.makeShot(true); // Beispielaufruf
    }

    public void setLastShotHit(boolean isHit) {
        strategy.setLastShotHit(isHit);
    }

    private ArrayList<Ship> generateRandomShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        ArrayList<int[]> occupiedCells = new ArrayList<>();
        Random random = new Random();

        int[] sizes = { 3, 4, 5 }; // TODO: Anpassen, falls mehr/andere Schiffe möglich sind

        for (int shipSize : sizes) {
            boolean placed = false;

            while (!placed) {
                boolean isVertical = random.nextBoolean();

                int maxRow = isVertical ? App.GRID_SIZE - shipSize : App.GRID_SIZE - 1;
                int maxCol = isVertical ? App.GRID_SIZE - 1 : App.GRID_SIZE - shipSize;

                int row = random.nextInt(maxRow + 1);
                int col = random.nextInt(maxCol + 1);

                // Prüfe, ob das Schiff auf das Feld passt
                if (!Helper.doesShipFit(isVertical ? row : col, shipSize, App.GRID_SIZE, isVertical)) {
                    continue;
                }

                // Prüfe, ob das Schiff mit anderen Schiffen überlappt
                if (!isOverlapping(row, col, shipSize, isVertical, occupiedCells)) {
                    Ship ship = new Ship(shipSize, isVertical);
                    ship.setPosition(new int[] { row, col });
                    ships.add(ship);

                    // Markiere belegte Zellen
                    for (int i = 0; i < shipSize; i++) {
                        int r = isVertical ? row + i : row;
                        int c = isVertical ? col : col + i;
                        occupiedCells.add(new int[] { r, c });
                    }

                    placed = true;
                }
            }
        }

        return ships;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }
}
