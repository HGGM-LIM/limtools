package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/**
 * This plugin masks a dynamic image (stack with frames - HyperStack) using
 * a mask of the same spatial dimensions. All voxels set to zero in the mask
 * will be set to zero in all the frames of the dynamic image.
 * 
 * @author José María Mateos - jmmateos@hggm.es
 *
 */
public class Mask_Dynamic_Image implements PlugIn {
    private ImagePlus dynamic, mask;
    
    @Override
    public void run(String s) {
        
        int [] images = WindowManager.getIDList();
        
        // If there are no images opened or there are not enough of them
        if (images == null || images.length < 2) {
            IJ.error("Need at least one dynamic image and one mask");
            return;
        }
        
        // Create a list of open images
        String [] open_images = new String[images.length];
        for (int i = 0; i < images.length; i++)
            open_images[i] = WindowManager.getImage(images[i]).getTitle();            
        
        GenericDialog gd = new GenericDialog("Choose dynamic image and mask");
        gd.addChoice("Dynamic image:", open_images, open_images[0]);
        gd.addChoice("Mask:", open_images, open_images[1]);
        gd.showDialog();
                
        // If user canceled, return
        if (gd.wasCanceled()) return;
        
        dynamic = WindowManager.getImage(gd.getNextChoice());
        mask = WindowManager.getImage(gd.getNextChoice());
        
        // Check if the dimensions agree
        int [] dim_dynamic = dynamic.getDimensions();
        int [] dim_mask = mask.getDimensions();
        
        if (dim_dynamic[0] != dim_mask[0] || 
            dim_dynamic[1] != dim_mask[1] ||
            dim_dynamic[3] != dim_mask[3]) {
            IJ.error("Dynamic image and mask spatial dimensions do not agree");
            return;
        }
        
        // Mask the original image
        
        ImageStack dyn_stack = dynamic.getStack();
        ImageStack mask_stack = mask.getStack();
        
        for (int slice = 1; slice <= dim_dynamic[3]; slice++) {
            for (int x = 0; x < dim_dynamic[0]; x++) {
                for (int y = 0; y < dim_dynamic[1]; y++) {
                    // Voxel masked?
                    boolean masked = mask_stack.getVoxel(x, y, slice - 1) == 0;
                    
                    if (masked) {
                        for (int f = 0; f <= dim_dynamic[4]; f++) {
                            int stackindex = dynamic.getStackIndex(1, slice, f);
                            dyn_stack.setVoxel(x, y, stackindex, 0.0);                            
                        } // end f                        
                    }
                } // end y
            } // end x
        } // end z
    } // end run
}
