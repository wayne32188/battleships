package battleships.lib;

import java.util.ArrayList;
import java.util.Random;

import battleships.Main;
import battleships.Ship;
import static battleships.lib.Helper.isOverlapping;

/**
 * Repräsentiert einen NPC-Gegner, der Schiffe zufällig platziert und zufällig
 * schießt.
 */
public class NpcEnemy {

    /** Die vom NPC platzierten Schiffe. */
    private ArrayList<Ship> ships;

    /**
     * Konstruktor. Platziert die Schiffe zufällig auf dem Spielfeld.
     */
    public NpcEnemy() {
        ships = generateRandomShips();
    }

    /**
     * Gibt die vom NPC platzierten Schiffe zurück.
     * 
     * @return Liste der Schiffe.
     */
    public ArrayList<Ship> getShips() {
        return ships;
    }

    /**
     * Generiert eine zufällige Platzierung der NPC-Schiffe auf dem Spielfeld.
     * Es wird geprüft, dass die Schiffe nicht überlappen und vollständig ins Grid
     * passen.
     * 
     * @return Liste der platzierten Schiffe.
     */
    private ArrayList<Ship> generateRandomShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        ArrayList<int[]> occupiedCells = new ArrayList<>();
        Random random = new Random();

        int[] sizes = { 3, 4, 5 }; // TODO: Anpassen, falls mehr/andere Schiffe möglich sind

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

    /**
     * Gibt einen zufälligen Schuss (Zellenkoordinaten) des NPC zurück.
     * 
     * @return Ein int-Array mit [row, col].
     */
    public int[] randomShot() {
        Random random = new Random();
        int row, col;

        // Der NPC wählt zufällig eine Zelle auf dem Spielfeld
        row = random.nextInt(Main.GRID_SIZE);
        col = random.nextInt(Main.GRID_SIZE);

        return new int[] { row, col };
    }
}