import java.io.Serializable;

public class Bounds implements Serializable {

    private final double upperBound;
    private final double lowerBound;

    public Bounds(double upperBound, double lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }
}
