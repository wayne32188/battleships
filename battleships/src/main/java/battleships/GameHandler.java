package battleships;

public class GameHandler {

    private boolean gameIsRunning = false;
    private boolean isHostTurn = true;

    GameHandler(boolean isNpcEnemy) {
        setGameIsRunning(true);
        while (isRunning()) {
            if (!isNpcEnemy) {
                System.out.println("Zwei Spieler Modus noch nicht implementiert");
            } else {
                
            }
        }
    }

    private boolean isRunning() {
        return gameIsRunning;
    }

    

    private void setGameIsRunning(boolean gameIsRunning) {
        this.gameIsRunning = gameIsRunning;
    }

    public boolean isHostTurn() {
        return isHostTurn;
    }

}
