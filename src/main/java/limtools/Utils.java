package limtools;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

/**
 * Utility class to store generic computational methods.
 * 
 * @author José María Mateos - jmmateos@hggm.es
 *
 */
public class Utils {
    
    /**
     * Tests if a given time-activity curve is masked in the original image.
     * @param tac The time-activity curve to test.
     * @param CALZERO The calibrated 0 value.
     * @return {@code true} if it is masked, false otherwise.
     */
    public static boolean isMasked(double [] tac, double CALZERO) {
        for (double d : tac)
            if (d != CALZERO && d != 0.0) return false;                
        return true;
    }
    
    /**
     * @param x x coordinate
     * @param y y coordinate
     * @param slice slice coordinate
     * @param t Number of time frames
     * @param imp {@link ImagePlus} object.
     * @param is {@link ImageStack} object (taken from {@code imp}).
     * @param cal {@link Calibration} object (taken from {@code imp}).
     * @return The calibrated time-activity curve for these coordinates.
     */
    public static double [] getTAC(int x, int y, int slice, int t, 
                                   ImagePlus imp, ImageStack is, 
                                   Calibration cal) {

        // Alloc space for the result
        double[] result = new double[t];

        // Set the desired slice and iterate through the frames
        for (int frame = 1; frame <= t; frame++) {
            // Use first channel
            int stack_number = imp.getStackIndex(1, slice, frame);            
            // Use calibration to return true value
            result[frame - 1] = cal.getCValue(
                                    is.getVoxel(x, y, stack_number - 1));
        }

        return result;
        
    }
    
    /**
     * Returns the maximum value for the given activity curve.
     * @param data The time-activity curve.
     * @return The maximum value.
     */
    public static double getMax(double [] data) {
        double res = -Double.MAX_VALUE;
        for (double d : data)
            if (d > res)
                res = d;
        return res;
    }

}
