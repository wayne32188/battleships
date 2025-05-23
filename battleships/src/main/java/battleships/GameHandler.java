package battleships;

import battleships.lib.NpcEnemy;

public class GameHandler {

    private boolean gameIsRunning = false;
    private boolean isHostTurn = true;
    private boolean isNpcEnemy;
    protected NpcEnemy npcEnemy;

    GameHandler(boolean isNpcEnemy) {
        this.isNpcEnemy = isNpcEnemy;
        setGameIsRunning(true);
        while (isRunning()) {
            if (!isNpcEnemy) {
                System.out.println("Zwei Spieler Modus noch nicht implementiert");
            } else {
                npcEnemy = new NpcEnemy();
            }
        }
    }

    private boolean isRunning() {
        return gameIsRunning;
    }

    public boolean isNpcEnemy() {
        return isNpcEnemy;
    }

    private void setGameIsRunning(boolean gameIsRunning) {
        this.gameIsRunning = gameIsRunning;
    }

    public boolean isHostTurn() {
        return isHostTurn;
    }

}
