package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

import static limtools.Utils.isMasked;

/**
 * Shows the mean time-activity values for each frame for the unmasked voxels
 * in the current dynamic image.
 * 
 * @author José María Mateos - jmmateos@hggm.es
 */

public class Measure_Time_Activity implements PlugIn {

    private ImagePlus imp;
    private ImageStack stack;
    private Calibration cal;
    private int dim[];
    private double CALZERO;
    
    @Override
    public void run(String arg0) {
        
        imp = IJ.getImage();
        
        // Check for opened images
        if (imp == null) {
            IJ.error("No image selected");
            return;
        }
        
        dim = imp.getDimensions();
        
        // Check for image type
        int type = imp.getType();
        if (type == ImagePlus.COLOR_256 || type == ImagePlus.COLOR_RGB) {
            IJ.error("This plugin only works on grayscale images");
            return;
        }
        
        // Check for frames
        if (dim[4] < 2) {
            IJ.error("It is not possible to measure time activity " + 
                     "for a static image. Please apply this plugin " +
                     "to an image that contains several frames");
            return;
        }
        
        // Fill TAC
        double [] tac = new double[dim[4]]; 
        double [] temp;
        stack = imp.getStack();      
        cal = imp.getCalibration();
        CALZERO = cal.getCValue(0.0);        
        int total = 0;
        
        
        for (int slice = 1; slice <= dim[3]; slice++) {
            // Update progress bar
            IJ.showProgress(slice - 1, dim[3]);
            for (int x = 0; x < dim[0]; x++) {
                for (int y = 0; y < dim[1]; y++) {
                    temp = _getTAC(x, y, slice);
                    if (!isMasked(temp, CALZERO)) {
                        total++;
                        for (int i = 0; i < tac.length; i++) {
                            tac[i] += temp[i];                        
                        }                        
                    }
                }
            }            
        }
        
        // Display the results in a ResultsTable object
        ResultsTable rt = Analyzer.getResultsTable();
        for (int i = 0; i < tac.length; i++) {            
            tac[i] /= (double) total;
            rt.incrementCounter();
            rt.addValue("Frame", i + 1);
            rt.addValue("Activity", tac[i]);
        }  
        rt.showRowNumbers(false);
        rt.show("Results");
    }
    
    private double [] _getTAC(int x, int y, int slice) {
        double [] tac = new double[dim[4]];
        for (int f = 1; f <= dim[4]; f++) {
            int index = imp.getStackIndex(1, slice, f);
            tac[f - 1] = cal.getCValue(stack.getVoxel(x, y, index - 1));
        }
        return tac;
    }
}
