package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

import static limtools.Utils.getTAC;
import static limtools.Utils.isMasked;

/**
 * <p>
 * This plugin outputs the dynamic data to an ImageJ results table that can be
 * stored to a file. The format for the output is the following:
 * </p>
 * 
 * <pre>
 * X Y SLICE FRAME1 FRAME2 FRAME3 ... FRAME N
 * </pre>
 * 
 * <p>
 * That is, the coordinates for each unmasked voxel in the first three columns
 * and then the value for each frame on each successive column.
 * </p>
 * 
 * @author José María Mateos - jmmateos@hggm.es
 * 
 */
public class Dynamic_to_Results implements PlugIn {

    private ImagePlus imp;
    private ImageStack is;
    private int[] dim;
    private Calibration cal;

    @Override
    public void run(String args) {

        // Dimensions check
        imp = IJ.getImage();
        dim = imp.getDimensions();
        // If not a HyperStack, return
        if (dim[4] < 2) {
            IJ.error("Not a HyperStack", "This plugin needs a HyperStack");
            return;
        }

        // Assign rest of variables
        is = imp.getStack();
        cal = imp.getCalibration();
        double calzero = cal.getCValue(0.0);

        ResultsTable rt = Analyzer.getResultsTable();
        for (int z = 0; z < dim[3]; z++) {
            // Update progress bar indicator
            IJ.showProgress(z, dim[3]);
            for (int x = 0; x < dim[0]; x++) {
                for (int y = 0; y < dim[1]; y++) {
                    // Obtain the TAC
                    double[] tac = getTAC(x, y, z + 1, dim[4], imp, is, cal);
                    // Ignore masked voxels and store the valid ones as 
                    // rows on the results table (coordinates first).
                    if (!isMasked(tac, calzero)) {
                        rt.incrementCounter();
                        rt.addValue("x", x);
                        rt.addValue("y", y);
                        rt.addValue("slice", z + 1);
                        for(int i = 0; i < tac.length; i++) {
                            String colName = String.format("F%d", i);
                            rt.addValue(colName, tac[i]);
                        }                        
                    }
                }
            }
        }
        
        rt.showRowNumbers(false);
        rt.show("Results");
        
    }
}
