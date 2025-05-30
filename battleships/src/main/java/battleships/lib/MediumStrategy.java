package battleships.lib;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import battleships.App;
import battleships.GameHandler;

public class MediumStrategy implements ShootingStrategy {

    private enum Mode {
        RANDOM, TRACKING
    }

    private enum ShootingDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private int[] initialHit = null;
    private int[] lastShot = null;
    private Mode mode = Mode.RANDOM;
    private ShootingDirection currentDirection = null;
    private final Set<ShootingDirection> triedDirections = new HashSet<>();

    private boolean lastShotHit = false;
    private final Random random = new Random();

    @Override
    public int[] makeShot(boolean targetIsPlayer) {
        // Vor dem Schuss: Moduswechsel prüfen
        if (lastShotHit) {
            if (mode == Mode.RANDOM) {
                mode = Mode.TRACKING;
                initialHit = lastShot;
                currentDirection = ShootingDirection.UP;
                triedDirections.clear();
            }
            // Im TRACKING-Modus: Richtung beibehalten, lastShot bleibt erhalten
        } else {
            if (mode == Mode.TRACKING) {
                triedDirections.add(currentDirection);
                if (triedDirections.size() == 4) {
                    resetTracking();
                } else {
                    currentDirection = getNextUntestedDirection();
                    lastShot = initialHit;
                }
            }
        }

        int[] newShot;
        if (mode == Mode.RANDOM) {
            newShot = getRandomShot(targetIsPlayer);
        } else {
            newShot = getNextDirectionalShot(targetIsPlayer);
        }
        lastShot = newShot;
        return newShot;
    }

    @Override
    public void setLastShotHit(boolean isHit) {
        this.lastShotHit = isHit;
    }

    private int[] getNextDirectionalShot(boolean targetIsPlayer) {
        // Versuche, in der aktuellen Richtung von lastShot aus zu schießen
        int[] next = getNextCellInDirection(lastShot, currentDirection);
        if (Helper.cellIsInGrid(next[0], next[1]) && !GameHandler.isCellAlreadyShot(getCellKey(next), targetIsPlayer)) {
            return next;
        } else {
            // Richtung ist blockiert oder schon beschossen, also nächste Richtung von initialHit aus probieren
            triedDirections.add(currentDirection);
            for (int i = 0; i < 4; i++) {
                if (triedDirections.size() == 4) {
                    resetTracking();
                    return getRandomShot(targetIsPlayer);
                }
                currentDirection = getNextUntestedDirection();
                next = getNextCellInDirection(initialHit, currentDirection);
                if (Helper.cellIsInGrid(next[0], next[1]) && !GameHandler.isCellAlreadyShot(getCellKey(next), targetIsPlayer)) {
                    lastShot = initialHit; // Von initialHit aus in neuer Richtung starten
                    return next;
                } else {
                    triedDirections.add(currentDirection);
                }
            }
            resetTracking();
            return getRandomShot(targetIsPlayer);
        }
    }

    private void resetTracking() {
        initialHit = null;
        lastShot = null;
        mode = Mode.RANDOM;
        triedDirections.clear();
        currentDirection = null;
    }

    private ShootingDirection getNextUntestedDirection() {
        for (ShootingDirection dir : ShootingDirection.values()) {
            if (!triedDirections.contains(dir)) {
                return dir;
            }
        }
        return ShootingDirection.UP; // Fallback
    }

    private int[] getNextCellInDirection(int[] origin, ShootingDirection direction) {
        int row = origin[0];
        int col = origin[1];
        return switch (direction) {
            case UP -> new int[] { row - 1, col };
            case DOWN -> new int[] { row + 1, col };
            case LEFT -> new int[] { row, col - 1 };
            case RIGHT -> new int[] { row, col + 1 };
        };
    }

    private String getCellKey(int[] pos) {
        return pos[0] + "," + pos[1];
    }


    private int[] getRandomShot(boolean targetIsPlayer) {
        int row, col;
        String cellKey;
        do {
            row = random.nextInt(App.GRID_SIZE);
            col = random.nextInt(App.GRID_SIZE);
            cellKey = row + "," + col;
        } while (GameHandler.isCellAlreadyShot(cellKey, targetIsPlayer));
        return new int[] { row, col };
    }
}