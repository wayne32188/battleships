package battleships.lib;

public interface ShootingStrategy {
    int[] makeShot(boolean targetIsPlayer);
    void setLastShotHit(boolean isHit);
}
