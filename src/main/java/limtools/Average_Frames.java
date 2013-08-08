package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This plugin creates a new image with the same spatial dimensions as the 
 * original one where each pixel value is the average of the original image
 * through the temporal dimension.
 * 
 * The created image will be a 32-bit image.
 * 
 * @author José María Mateos - jmmateos@hggm.es
 *
 */
public class Average_Frames implements PlugInFilter {
    
    private ImagePlus imp;    
    private int[] dim;

    @Override
    public void run(ImageProcessor arg0) {
        
        // Create dialog to obtain the first and last frames for the averaging                
        int lastframe = dim[4], initframe, endframe;     
        String[] choices = new String[lastframe];
        for (int i = 0; i < choices.length; i ++) {
            choices[i] = (i + 1) + "";
        }
        
        GenericDialog gd = new GenericDialog(
                                    "Choose first and last frame to average");
        gd.addChoice("First frame:", choices, "1");
        gd.addChoice("Last frame:", choices, lastframe + "");
        gd.showDialog();
        
        // If user canceled, return
        if (gd.wasCanceled()) return;
        
        initframe = Integer.parseInt(gd.getNextChoice());
        endframe = Integer.parseInt(gd.getNextChoice());
        
        // Create result image        
        String src_title = imp.getTitle();
        String res_title = src_title + String.format(" (average %d - %d)", 
                                                     initframe, endframe);
        ImagePlus result = IJ.createImage(res_title, "32-bit", dim[0], dim[1], 
                                          1, dim[3], 1);
        
        // Compute the mean frame value and set on the original image
        ImageStack source = imp.getStack();
        ImageStack target = result.getStack();    
        Calibration cal = imp.getCalibration();
        result.setCalibration(cal); // FIXME: values are not calibrated        
        double [] values = new double[endframe - initframe + 1];
        
        for (int z = 0; z < dim[3]; z++) {
            for (int x = 0; x < dim[0]; x++) {
                for (int y = 0; y < dim[1]; y++) {
                    for (int f = initframe; f <= endframe; f++) {
                        int stackindex = imp.getStackIndex(1, z + 1, f); 
                        values[f - initframe] = cal.getCValue(
                                                 source.getVoxel(x, y, 
                                                               stackindex - 1));
                    } // end f
                    target.setVoxel(x, y, z, _mean(values));                    
                } // end y
            } // end x
        } // end z        
        
        result.show();
    }

    @Override
    public int setup(String arg0, ImagePlus imp) {
        
        dim = imp.getDimensions();
        
        // If not a HyperStack, return
        if (dim[4] < 2) {
            IJ.error("Not a HyperStack", "This plugin needs a HyperStack");
            return DONE;
        }
            
        this.imp = imp;
        return DOES_16 + DOES_32 + DOES_8G;
    }
    
    private double _mean(double [] values) {
        double result = 0.0;
        for (double d : values) result += d;        
        return result / values.length;
    }

}
