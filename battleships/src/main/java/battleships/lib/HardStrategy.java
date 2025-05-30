package battleships.lib;

public class HardStrategy implements ShootingStrategy {

    @Override
    public int[] makeShot(boolean targetIsPlayer) {
        // To be implemented
        return new int[] { -1, -1 }; // Platzhalter für ungültigen Schuss
    }

    @Override
    public void setLastShotHit(boolean isHit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLastShotHit'");
    }
}