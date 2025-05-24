package battleships;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import battleships.lib.NpcEnemy;
import javafx.scene.layout.StackPane;

public class GameHandler {

    private boolean gameIsRunning = false;
    private boolean isHostTurn = true;
    private final boolean isNpcEnemy;
    private ArrayList<Ship> enemyShips = new ArrayList<>();
    private ArrayList<Ship> playerShips = new ArrayList<>();
    private static final Set<String> shotCells = new HashSet<>();

    protected NpcEnemy npcEnemy;

    GameHandler(boolean isNpcEnemy, ArrayList<Ship> playerShips) {
        this.isNpcEnemy = isNpcEnemy;
        this.gameIsRunning = true;
        this.playerShips = playerShips;
        if (isNpcEnemy) {
            this.npcEnemy = new NpcEnemy();
            this.enemyShips = npcEnemy.getShips();
        }
    }

    public boolean isShipHit(boolean isPlayerShips, int row, int col) {
        ArrayList<Ship> ships = isPlayerShips ? playerShips : enemyShips;
        for (Ship ship : ships) {
            if (ship.isHit(row, col)) {
                System.out.println("Schiff getroffen!");
                return true;
            }
        }
        return false;
    }

    public boolean isRunning() {
        return gameIsRunning;
    }

    public boolean isNpcEnemy() {
        return isNpcEnemy;
    }

    public boolean isHostTurn() {
        return isHostTurn;
    }

    public void npcMove() {
        int[] randomShot;
        do {
            randomShot = npcEnemy.randomShot();
        } while (!isCellAlreadyShot(randomShot[0] + "," + randomShot[1]));   

        StackPane cell = SecondaryController.playerCells[randomShot[0]][randomShot[1]];
        boolean hit = isShipHit(true, randomShot[0], randomShot[1]);
        SecondaryController.handleEnemyMove(cell, hit);
        isHostTurn = true;
    }

    public void playerMove() {
        isHostTurn = false;
        if (isNpcEnemy) {
            npcMove();
        }
    }

    public void addShotCell(int row, int col) {
        shotCells.add(row + "," + col);
    }

    public boolean isCellAlreadyShot(String cellKey) {
        return shotCells.contains(cellKey);
    }

}
