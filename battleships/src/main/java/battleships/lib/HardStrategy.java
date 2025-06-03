package battleships.lib;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

import battleships.App;
import battleships.GameHandler;
import battleships.lib.enums.Mode;
import battleships.lib.enums.ShootingDirection;

public class HardStrategy implements ShootingStrategy {

    private int[] initialHit = null;
    private int[] lastShot = null;
    private Mode mode = Mode.RANDOM;
    private ShootingDirection currentDirection = null;
    private final Set<ShootingDirection> triedDirections = new HashSet<>();

    private boolean lastShotHit = false;
    private boolean hasShipSunk = false;
    private ArrayList<Integer> shipSizes;
    private final Random random = new Random();

    private int trackingHitCount = 0;
    private ShootingDirection confirmedDirection = null;
    private boolean triedOppositeDirection = false;

    public HardStrategy(ArrayList<Integer> shipSizes) {
        this.shipSizes = shipSizes;
    }

    @Override
    public int[] makeShot(boolean targetIsPlayer) {
        // Vor dem Schuss: Moduswechsel prüfen
        if (lastShotHit || hasShipSunk()) {
            if (mode == Mode.RANDOM) {
                mode = Mode.TRACKING;
                initialHit = lastShot;
                currentDirection = ShootingDirection.UP;
                triedDirections.clear();
                trackingHitCount = 1; // erster Treffer
                triedOppositeDirection = false;
            } else {
                trackingHitCount++;
                if (trackingHitCount == 2) {
                    confirmedDirection = currentDirection;
                }
            }
            setShipSunken(false);
        } else {
            if (mode == Mode.TRACKING) {
                if (confirmedDirection != null && !triedOppositeDirection) {
                    // Wir haben eine bestätigte Richtung, aber keinen Treffer -> Gegenseite
                    // probieren
                    currentDirection = getOppositeDirection(confirmedDirection);
                    triedOppositeDirection = true;
                    lastShot = initialHit; // Zurück zum Startpunkt
                } else {
                    triedDirections.add(currentDirection);
                    if (triedDirections.size() == 4) {
                        resetTracking();
                    } else {
                        currentDirection = getNextUntestedDirection();
                        lastShot = initialHit;
                    }
                }
            }
        }

        // Schuss generieren
        int[] newShot;
        if (mode == Mode.RANDOM) {
            newShot = getInformedShot(targetIsPlayer);
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
        int[] nextCell = getNextCellInDirection(lastShot, currentDirection);
        if (Helper.cellIsInGrid(nextCell[0], nextCell[1])
                && !GameHandler.isCellAlreadyShot(buildCellKey(nextCell), targetIsPlayer)) {
            return nextCell;
        } else {
            // Richtung ist blockiert oder schon beschossen, also nächste Richtung von
            // initialHit aus probieren
            triedDirections.add(currentDirection);
            for (int i = 0; i < 4; i++) {
                // Wenn alle Richtungen schon probiert wurden, Zufallsschuss
                if (triedDirections.size() == 4) {
                    resetTracking();
                    return getRandomShot(targetIsPlayer);
                }

                currentDirection = getNextUntestedDirection();
                nextCell = getNextCellInDirection(initialHit, currentDirection);

                if (Helper.cellIsInGrid(nextCell[0], nextCell[1])
                        && !GameHandler.isCellAlreadyShot(buildCellKey(nextCell), targetIsPlayer)) {
                    lastShot = initialHit; // Von initialHit aus in neuer Richtung starten
                    return nextCell;
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
        confirmedDirection = null;
        trackingHitCount = 0;
        triedOppositeDirection = false;
    }

    private ShootingDirection getNextUntestedDirection() {
        for (ShootingDirection dir : ShootingDirection.values()) {
            if (!triedDirections.contains(dir)) {
                return dir;
            }
        }
        return ShootingDirection.UP; // Fallback
    }

    private ShootingDirection getOppositeDirection(ShootingDirection direction) {
        return switch (direction) {
            case UP -> ShootingDirection.DOWN;
            case DOWN -> ShootingDirection.UP;
            case LEFT -> ShootingDirection.RIGHT;
            case RIGHT -> ShootingDirection.LEFT;
        };
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

    /**
     * 
     * Erstellt einen Key einer Position, der passend zu den Elementen in
     * shotPlayerCells oder shotEnemyCells
     */
    private String buildCellKey(int[] pos) {
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

    private boolean hasShipSunk() {
        return hasShipSunk;
    }

    /**
     * 
     * @param hasShipSunk Wurde das letzte Schiff versenkt
     * @param shipSize    Wie groß ist das zu entfernende Schiff
     */
    public void setShipSunken(boolean hasShipSunk, int shipSize) {
        setShipSunken(hasShipSunk);
        removeShip(shipSize);
    }

    /**
     * Entfernt kein Schiff aus shipSizes
     * 
     * @param hasShipSunk Wurde das letzte Schiff versenkt
     */
    private void setShipSunken(boolean hasShipSunk) {
        this.hasShipSunk = hasShipSunk;
    }

    private void removeShip(int shipSize) {
        shipSizes.remove(Integer.valueOf(shipSize));
    }

    private int[] getInformedShot(boolean targetIsPlayer) {
        int biggestShip = shipSizes.stream().max(Integer::compareTo).orElse(0);

        for (int row = 0; row < App.GRID_SIZE; row++) {
            for (int col = 0; col < App.GRID_SIZE; col++) {
                // Horizontal prüfen
                if (fitsHorizontally(row, col, biggestShip, targetIsPlayer)) {
                    return new int[] { row, col + biggestShip / 2 }; // Mitte des Bereichs
                }
                // Vertikal prüfen
                if (fitsVertically(row, col, biggestShip, targetIsPlayer)) {
                    return new int[] { row + biggestShip / 2, col }; // Mitte des Bereichs
                }
            }
        }

        // Falls nichts passt, fallback
        return getRandomShot(targetIsPlayer);
    }

    private boolean fitsHorizontally(int row, int col, int length, boolean targetIsPlayer) {
        if (col + length > App.GRID_SIZE)
            return false;

        for (int i = 0; i < length; i++) {
            String cellKey = buildCellKey(new int[] { row, col + i });
            if (GameHandler.isCellAlreadyShot(cellKey, targetIsPlayer)) {
                return false;
            }
        }

        return true;
    }

    private boolean fitsVertically(int row, int col, int length, boolean targetIsPlayer) {
        if (row + length > App.GRID_SIZE)
            return false;

        for (int i = 0; i < length; i++) {
            String key = (row + i) + "," + col;
            if (GameHandler.isCellAlreadyShot(key, targetIsPlayer)) {
                return false;
            }
        }

        return true;
    }

}