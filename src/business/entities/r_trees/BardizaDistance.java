package business.entities.r_trees;

public class BardizaDistance {
    private final Bardiza bardiza;
    private final double distance;

    BardizaDistance(Bardiza bardiza, double distance) {
        this.bardiza = bardiza;
        this.distance = distance;
    }

    Bardiza getBardiza() {
        return bardiza;
    }

    double getDistance() {
        return distance;
    }
}
