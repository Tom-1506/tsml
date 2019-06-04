package distances.erp;

import distances.dtw.Dtw;
import utilities.ArrayUtilities;

public class Erp
    extends Dtw {

    private double penalty; // penalty for a gap, 0 best according to paper

    public final static double DEFAULT_PENALTY = 0;

    public Erp() {
        super();
        setPenalty(DEFAULT_PENALTY);
    }

    public Erp(double warpingWindowPercentage, double penalty) {
        super(warpingWindowPercentage);
        setPenalty(penalty);
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double g) {
        this.penalty = g;
    }

    @Override
    protected double measureDistance(
        double[] timeSeriesA,
        double[] timeSeriesB,
        double cutOff
                                    ) {

        // todo cleanup
        // todo trim memory to window by window
        // todo early abandon
        // todo remove sqrt (Jay says this changes the distance however, need to confirm!)

        // Current and previous columns of the matrix
        double[] curr = new double[timeSeriesB.length]; // todo use timeSeriesA as it's the longer of the two
        double[] prev = new double[timeSeriesB.length];

        // size of edit distance band
        // bandsize is the maximum allowed distance to the diagonal
//        int band = (int) Math.ceil(v2.getDimensionality() * bandSize);
        int band = (int) Math.ceil(timeSeriesB.length * getWarpingWindow());

        // g parameters for local usage
        double gValue = penalty;

        for (int i = 0;
             i < timeSeriesA.length;
             i++) {
            // Swap current and prev arrays. We'll just overwrite the new curr.
            {
                double[] temp = prev;
                prev = curr;
                curr = temp;
            }
            int l = i - (band + 1);
            if (l < 0) {
                l = 0;
            }
            int r = i + (band + 1);
            if (r > (timeSeriesB.length - 1)) {
                r = (timeSeriesB.length - 1);
            }

            for (int j = l;
                 j <= r;
                 j++) {
                if (Math.abs(i - j) <= band) {
                    // compute squared distance of feature vectors
                    double val1 = timeSeriesA[i];
                    double val2 = gValue;
                    double diff = (val1 - val2);
                    final double d1 = Math.sqrt(diff * diff);

                    val1 = gValue;
                    val2 = timeSeriesB[j];
                    diff = (val1 - val2);
                    final double d2 = Math.sqrt(diff * diff);

                    val1 = timeSeriesA[i];
                    val2 = timeSeriesB[j];
                    diff = (val1 - val2);
                    final double d12 = Math.sqrt(diff * diff);

                    final double dist1 = d1 * d1;
                    final double dist2 = d2 * d2;
                    final double dist12 = d12 * d12;

                    final double cost;

                    if ((i + j) != 0) {
                        if ((i == 0) || ((j != 0) && (((prev[j - 1] + dist12) > (curr[j - 1] + dist2)) && ((curr[j - 1] + dist2) < (prev[j] + dist1))))) {
                            // del
                            cost = curr[j - 1] + dist2;
                        } else if ((j == 0) || ((i != 0) && (((prev[j - 1] + dist12) > (prev[j] + dist1)) && ((prev[j] + dist1) < (curr[j - 1] + dist2))))) {
                            // ins
                            cost = prev[j] + dist1;
                        } else {
                            // match
                            cost = prev[j - 1] + dist12;
                        }
                    } else {
                        cost = 0;
                    }

                    curr[j] = cost;
                    // steps[i][j] = step;
                } else {
                    curr[j] = Double.POSITIVE_INFINITY; // outside band
                }
            }
        }

        return Math.sqrt(curr[timeSeriesB.length - 1]);
    }


    public static final String PENALTY_KEY = "penalty";

    @Override
    public String[] getOptions() {
        return ArrayUtilities.concat(super.getOptions(), new String[] {
            PENALTY_KEY,
            String.valueOf(penalty)
        });
    }


    @Override
    public void setOptions(String[] options) {
        super.setOptions(options);
        for (int i = 0; i < options.length - 1; i += 2) {
            String key = options[i];
            String value = options[i + 1];
            if (key.equals(PENALTY_KEY)) {
                setPenalty(Double.parseDouble(value));
            }
        }
    }

}