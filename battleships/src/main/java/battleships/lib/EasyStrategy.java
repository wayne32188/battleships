package battleships.lib;

import java.util.Random;

import battleships.App;
import battleships.GameHandler;

public class EasyStrategy implements ShootingStrategy {
    private final Random random = new Random();

    @Override
    public int[] makeShot(boolean targetIsPlayer) {
        int row, col;
        String cellKey;
        do {
            row = random.nextInt(App.GRID_SIZE);
            col = random.nextInt(App.GRID_SIZE);
            cellKey = row + "," + col;
        } while (GameHandler.isCellAlreadyShot(cellKey, targetIsPlayer));
        return new int[] { row, col };
    }

    @Override
    public void setLastShotHit(boolean isHit) {
        System.out.println("EasyStrategy: Last shot hit status is not tracked.");
    }
}
