package battleships;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import battleships.lib.NpcEnemy;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;

/**
 * Die zentrale Spiellogik für das Battleships-Spiel.
 * Verwaltet Spieler- und Gegner-Schiffe, Spielstatus und Spielzüge.
 */
public class GameHandler {

    /** Gibt an, ob das Spiel aktuell läuft. */
    private boolean gameIsRunning = false;

    /** Gibt an, ob der Host (Spieler) am Zug ist. */
    private boolean isHostTurn = true;

    /** True, wenn der Gegner ein NPC ist. */
    private final boolean isNpcEnemy;

    /** Liste der gegnerischen Schiffe. */
    private ArrayList<Ship> enemyShips = new ArrayList<>();

    /** Liste der Spieler-Schiffe. */
    private ArrayList<Ship> playerShips = new ArrayList<>();

    /** Set mit allen bereits beschossenen Zellen (Format: "row,col"). */
    private static final Set<String> shotCells = new HashSet<>();
    // TODO für echten Multiplayer weitere Variable für die Shot cells einbauen

    /** Zähler für zerstörte Schiffe des Spielers. */
    private int playerShipsDestroyed = 0;

    /** Zähler für zerstörte Schiffe des Gegners. */
    private int enemyShipsDestroyed = 0;

    /** Referenz auf den NPC-Gegner (falls aktiviert). */
    protected NpcEnemy npcEnemy;

    /**
     * Erstellt einen neuen GameHandler.
     * @param isNpcEnemy True, wenn der Gegner ein NPC ist.
     * @param playerShips Die Schiffe des Spielers.
     */
    public GameHandler(boolean isNpcEnemy, ArrayList<Ship> playerShips) {
        this.isNpcEnemy = isNpcEnemy;
        this.gameIsRunning = true;
        this.playerShips = playerShips;
        if (isNpcEnemy) {
            this.npcEnemy = new NpcEnemy();
            this.enemyShips = npcEnemy.getShips();
        }
    }

    /**
     * Prüft, ob ein Schiff auf der angegebenen Seite (Spieler oder Gegner) getroffen wurde.
     * @param targetIsPlayer True, wenn das Ziel der Spieler ist, sonst Gegner.
     * @param row Zeile des Schusses.
     * @param col Spalte des Schusses.
     * @return True, wenn ein Schiff getroffen wurde, sonst false.
     */
    public boolean hitShip(boolean targetIsPlayer, int row, int col) {
        ArrayList<Ship> ships = targetIsPlayer ? playerShips : enemyShips;
        for (Ship ship : ships) {
            if (ship.isHit(row, col)) {
                System.out.println("Schiff getroffen!");
                if (ship.isDestroyed()) {
                    if (targetIsPlayer) {
                        playerShipsDestroyed++;
                        if (playerShipsDestroyed == playerShips.size()) {
                            handleGameOver(true);
                        }
                    } else {
                        enemyShipsDestroyed++;
                        if (enemyShipsDestroyed == enemyShips.size()) {
                            handleGameOver(false);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt zurück, ob das Spiel aktuell läuft.
     * @return True, wenn das Spiel läuft.
     */
    public boolean isRunning() {
        return gameIsRunning;
    }

    /**
     * Beendet das Spiel und gibt das Ergebnis aus.
     * @param isPlayerShips True, wenn die Schiffe des Spielers zerstört wurden (Verlust).
     */
    public void handleGameOver(boolean isPlayerShips) {
        if (isPlayerShips) {
            System.out.println("Du hast verloren!");
        } else {
            System.out.println("Du hast gewonnen!");
        }
        gameIsRunning = false;
    }

    /**
     * Gibt zurück, ob der Gegner ein NPC ist.
     * @return True, wenn NPC-Gegner.
     */
    public boolean isNpcEnemy() {
        return isNpcEnemy;
    }

    /**
     * Gibt zurück, ob der Host (Spieler) am Zug ist.
     * @return True, wenn Host am Zug.
     */
    public boolean isHostTurn() {
        return isHostTurn;
    }

    /**
     * Führt den Zug des NPC-Gegners aus.
     * Wählt zufällig eine freie Zelle und feuert darauf.
     */
    public void npcMove() {
        int[] randomShot;
        do {
            randomShot = npcEnemy.randomShot();
        } while (isCellAlreadyShot(randomShot[0] + "," + randomShot[1]));

        StackPane cell = GameHandlerController.playerCells[randomShot[0]][randomShot[1]];
        boolean hit = hitShip(true, randomShot[0], randomShot[1]);
        GameHandlerController.handleEnemyMove(cell, hit);
        isHostTurn = true;
        addShotCell(randomShot[0], randomShot[1]);
    }

    /**
     * Beendet den Spielerzug und startet ggf. den NPC-Zug.
     */
    public void playerMove() {
        isHostTurn = false;
        if (isNpcEnemy) {
            npcMove();
        }
    }

    /**
     * Fügt eine beschossene Zelle zum Set hinzu.
     * @param row Zeile der Zelle.
     * @param col Spalte der Zelle.
     */
    public void addShotCell(int row, int col) {
        shotCells.add(row + "," + col);
    }

    /**
     * Prüft, ob eine Zelle bereits beschossen wurde.
     * @param cellKey Schlüssel im Format "row,col".
     * @return True, wenn die Zelle bereits beschossen wurde.
     */
    public boolean isCellAlreadyShot(String cellKey) {
        return shotCells.contains(cellKey);
    }

}
