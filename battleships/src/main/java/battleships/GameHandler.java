package battleships;

public class GameHandler {

    private boolean gameIsRunning = false;
    private boolean isHostTurn = true;

    GameHandler(boolean isNpcEnemy) {
        while (isRunning()) {
            if (!isNpcEnemy) {
            }
        }
    }

    private boolean isRunning() {
        return gameIsRunning;
    }

    public void setGameIsRunning(boolean gameIsRunning) {
        this.gameIsRunning = gameIsRunning;
    }

    public boolean isHostTurn() {
        return isHostTurn;
    }

}
