import java.io.Serializable;

// Κλάση που αναπαριστά το upper και το lower bound ενός αντικειμένου σε μία διάσταση
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
