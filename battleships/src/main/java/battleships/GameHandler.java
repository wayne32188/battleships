package battleships;

import javafx.scene.layout.GridPane;

public class GameHandler {

    private boolean isGameOver = false;

    GameHandler (GridPane playerField, GridPane enemyField) {
        System.out.println("GameHandler initialized");
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }


}
