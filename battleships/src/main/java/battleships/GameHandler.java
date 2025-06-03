package battleships;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import battleships.lib.NpcEnemy;
import battleships.lib.enums.Difficulty;
import javafx.scene.layout.StackPane;

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
    public static final Set<String> shotPlayerCells = new HashSet<>();
    private static final Set<String> shotEnemyCells = new HashSet<>();

    /** Zähler für zerstörte Schiffe des Spielers. */
    private int playerShipsDestroyed = 0;

    /** Zähler für zerstörte Schiffe des Gegners. */
    private int enemyShipsDestroyed = 0;

    /** Referenz auf den NPC-Gegner (falls aktiviert). */
    protected NpcEnemy npcEnemy;

    private boolean playerIsWinner;

    /**
     * Erstellt einen neuen GameHandler.
     * 
     * @param isNpcEnemy  True, wenn der Gegner ein NPC ist.
     * @param playerShips Die Schiffe des Spielers.
     */
    public GameHandler(boolean isNpcEnemy, ArrayList<Ship> playerShips) {
        this.isNpcEnemy = isNpcEnemy;
        this.gameIsRunning = true;
        this.playerShips = playerShips;
        if (isNpcEnemy) {
            this.npcEnemy = new NpcEnemy(ShipPlacementController.difficulty);
            this.enemyShips = npcEnemy.getShips();
        }
    }

    /**
     * Prüft, ob ein Schiff auf der angegebenen Seite (Spieler oder Gegner)
     * getroffen wurde.
     * 
     * @param targetIsPlayer True, wenn das Ziel der Spieler ist, sonst Gegner.
     * @param row            Zeile des Schusses.
     * @param col            Spalte des Schusses.
     * @return True, wenn ein Schiff getroffen wurde, sonst false.
     * @throws IOException
     */
    public boolean hitShip(boolean targetIsPlayer, int row, int col) {
        ArrayList<Ship> ships = targetIsPlayer ? playerShips : enemyShips;
        for (Ship ship : ships) {
            if (ship.isHit(row, col)) {
                if (ship.hasSunk()) {
                    if (targetIsPlayer) {
                        playerShipsDestroyed++;
                        if (ShipPlacementController.difficulty == Difficulty.HARD) {
                            npcEnemy.setShipSunken(true, ship.getSize());
                        }
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
     * 
     * @return True, wenn das Spiel läuft.
     */
    public boolean isRunning() {
        return gameIsRunning;
    }

    /**
     * Beendet das Spiel und gibt das Ergebnis aus.
     * 
     * @param isPlayerShips True, wenn die Schiffe des Spielers zerstört wurden
     *                      (Verlust).
     * @throws IOException
     */
    public void handleGameOver(boolean isPlayerShips) {
        if (isPlayerShips) {
            playerIsWinner = false;
            System.out.println("Du hast verloren!");
        } else {
            playerIsWinner = true;
            System.out.println("Du hast gewonnen!");
        }
        gameIsRunning = false;
        try {
            WinnerPopupController.openWinnerPopup(playerIsWinner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gibt zurück, ob der Gegner ein NPC ist. *
     * 
     * @return True, wenn NPC-Gegner.
     */
    public boolean isNpcEnemy() {
        return isNpcEnemy;
    }

    /**
     * Gibt zurück, ob der Host (Spieler) am Zug ist. *
     * 
     * @return True, wenn Host am Zug.
     */
    public boolean isHostTurn() {
        return isHostTurn;
    }

    /**
     * Führt den Zug des NPC-Gegners aus.
     * 
     * @throws IOException
     */
    public void npcMove() {
        int[] npcShot;
        do {
            npcShot = npcEnemy.shootAtEnemy();
        } while (isCellAlreadyShot((npcShot[0] + "," + npcShot[1]), true));

        StackPane cell = GameHandlerController.playerCells[npcShot[0]][npcShot[1]];
        boolean isHit = hitShip(true, npcShot[0], npcShot[1]);

        npcEnemy.setLastShotHit(isHit);

        GameHandlerController.handleEnemyMove(cell, isHit);
        isHostTurn = true;
        addShotCell(npcShot[0], npcShot[1], true);
    }

    /**
     * Beendet den Spielerzug und startet ggf. den NPC-Zug.
     * 
     * @throws IOException
     */
    public void endPlayerMove() {
        isHostTurn = false;
        if (isNpcEnemy) {
            npcMove();
        }
    }

    /**
     * Fügt eine beschossene Zelle zum passenden Set hinzu.
     * 
     * @param row            Zeile der Zelle.
     * @param col            Spalte der Zelle.
     * @param targetIsPlayer True, wenn auf das Spielfeld des Spielers geschossen
     *                       wurde, sonst Gegner.
     */
    public void addShotCell(int row, int col, boolean targetIsPlayer) {
        String key = row + "," + col;
        if (targetIsPlayer) {
            shotPlayerCells.add(key);
        } else {
            shotEnemyCells.add(key);
        }
    }

    /**
     * Prüft, ob eine Zelle bereits beschossen wurde.
     * 
     * @param row            Zeile der Zelle.
     * @param col            Spalte der Zelle.
     * @param targetIsPlayer True, wenn auf das Spielfeld des Spielers geprüft
     *                       werden soll, sonst Gegner.
     * @return True, wenn die Zelle bereits beschossen wurde.
     */
    public static boolean isCellAlreadyShot(String cellKey, boolean targetIsPlayer) {
        return targetIsPlayer ? shotPlayerCells.contains(cellKey) : shotEnemyCells.contains(cellKey);
    }

}
