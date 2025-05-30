package battleships.lib;

public class DirectionHelper {

    enum ShootingDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}
    public static int[] getNextCellInDirection(int[] origin, ShootingDirection direction) {
        int row = origin[0];
        int col = origin[1];
        return switch (direction) {
            case UP -> new int[] { row - 1, col };
            case DOWN -> new int[] { row + 1, col };
            case LEFT -> new int[] { row, col - 1 };
            case RIGHT -> new int[] { row, col + 1 };
        };
    }

    public static ShootingDirection getOppositeDirection(ShootingDirection dir) {
        return switch (dir) {
            case UP -> ShootingDirection.DOWN;
            case DOWN -> ShootingDirection.UP;
            case LEFT -> ShootingDirection.RIGHT;
            case RIGHT -> ShootingDirection.LEFT;
        };
    }
}
