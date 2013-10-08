package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static limtools.Utils.getTAC;
import static limtools.Utils.isMasked;
import static limtools.Utils.getMax;

/**
 * This plugin normalizes each time-activity curve (TAC) its maximum value. This
 * operation can be used to remove the effect of the amplitude in any given
 * dynamic sequence.
 * 
 * @author José María Mateos - jmmateos@hggm.es
 * 
 */
public class Normalize_Dynamic implements PlugInFilter {

    private ImagePlus imp;
    private ImageStack is;
    private int[] dim;
    private Calibration cal;

    @Override
    public void run(ImageProcessor ip) {
        
        is = imp.getStack();        
        cal = imp.getCalibration();        
        double calzero = cal.getCValue(0.0);
        
        for (int z = 0; z < dim[3]; z++) {
            // Update progress bar indicator
            IJ.showProgress(z, dim[3]);
            for (int x = 0; x < dim[0]; x++) {
                for (int y = 0; y < dim[1]; y++) {
                    // Obtain the TAC
                    double [] tac = getTAC(x, y, z + 1, dim[4], imp, is, cal);
                    // Ignore masked voxels                                          
                    if(!isMasked(tac, calzero)) {
                        double m = getMax(tac);
                        // Set the normalized values for each non-masked TAC
                        for (int f = 1; f <= dim[4]; f++) {
                            int stackindex = imp.getStackIndex(1, z + 1, f);
                            double v = cal.getCValue(is.getVoxel(
                                    x, y, stackindex - 1)) / m;
                            is.setVoxel(x, y, stackindex - 1, v);                            
                        }                        
                    }                    
                }
            }
        }
        
        imp.updateImage();
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        
        dim = imp.getDimensions();

        // If not a HyperStack, return
        if (dim[4] < 2) {
            IJ.error("Not a HyperStack", "This plugin needs a HyperStack");
            return DONE;
        }
        
        this.imp = imp;
        return DOES_ALL;
    }

}
