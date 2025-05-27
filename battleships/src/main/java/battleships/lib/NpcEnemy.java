package battleships.lib;

import java.util.ArrayList;
import java.util.Random;

import battleships.Main;
import battleships.Ship;
import static battleships.lib.Helper.isOverlapping;

public class NpcEnemy {

    private ArrayList<Ship> ships;

    public NpcEnemy() {
        ships = generateRandomShips();
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    private ArrayList<Ship> generateRandomShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        ArrayList<int[]> occupiedCells = new ArrayList<>();
        Random random = new Random();

        int[] sizes = { 3, 4, 5 }; // TODO Ändern, wenn Spieler auch mehr und andere Schiffe haben kann

        for (int shipSize : sizes) {
            boolean placed = false;

            while (!placed) {
                boolean isVertical = random.nextBoolean();

                int maxRow = isVertical ? Main.GRID_SIZE - shipSize : Main.GRID_SIZE - 1;
                int maxCol = isVertical ? Main.GRID_SIZE - 1 : Main.GRID_SIZE - shipSize;

                int row = random.nextInt(maxRow + 1);
                int col = random.nextInt(maxCol + 1);

                // Prüfe, ob das Schiff auf das Feld passt
                if (!Helper.doesShipFit(isVertical ? row : col, shipSize, Main.GRID_SIZE, isVertical)) {
                    continue;
                }

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

        System.out.println("kommst du hier hin vallah?");
        return ships;
    }

    public int[] randomShot() {
        Random random = new Random();
        int row, col;

        // Der NPC wählt zufällig eine Zelle auf dem Spielfeld
        row = random.nextInt(Main.GRID_SIZE);
        col = random.nextInt(Main.GRID_SIZE);

        return new int[] { row, col };
    }
}
