package battleships.lib;

import java.util.ArrayList;

import battleships.Ship;

public class NpcEnemy {

    public NpcEnemy() {
        ArrayList<Ship> ships = generateRandomShips();
    }

    private ArrayList<Ship> generateRandomShips() {
        ArrayList<Ship> ships = new ArrayList<>();
        // 3 Schiffe generieren, mit zufälliger Rotation
        ships.add(new Ship(3, (Math.random() <= 0.5)));
        ships.add(new Ship(4, (Math.random() <= 0.5)));
        ships.add(new Ship(5, (Math.random() <= 0.5)));

        for (Ship ship : ships) {
            ship.setPosition(generateRandomPosition(ship.isVertical()));
        }
        return ships;
    }

    private int[] generateRandomPosition(boolean isVertical) {
        int[] randomPosition = new int[] {1, 2};
        return randomPosition;
    }

    
}
