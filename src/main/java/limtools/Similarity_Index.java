package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

/**
 * Computes the Jaccard index (http://en.wikipedia.org/wiki/Jaccard_index) and
 * Dice coefficient (http://en.wikipedia.org/wiki/Dice%27s_coefficient) for
 * two given binary images (masks).
 * 
 * Dice coefficient is computed from the Jaccard index as 2J/(1 + J).
 * 
 * @author José María Mateos - jmmateos@hggm.es
 */
public class Similarity_Index implements PlugIn {
    
    private ImagePlus A, B;

    @Override
    public void run(String arg0) {
        
        int [] images = WindowManager.getIDList();
        
        // If there are no images opened or there are not enough of them
        if (images == null || images.length < 2) {
            IJ.error("Need at least two images");
            return;
        }
        
        // Create a list of open images
        String [] open_images = new String[images.length];
        for (int i = 0; i < images.length; i++)
            open_images[i] = WindowManager.getImage(images[i]).getTitle();            
        
        GenericDialog gd = new GenericDialog("Choose images");
        gd.addChoice("Image 1:", open_images, open_images[0]);
        gd.addChoice("Image 2:", open_images, open_images[1]);
        gd.showDialog();
                
        // If user canceled, return
        if (gd.wasCanceled()) return;
        
        A = WindowManager.getImage(gd.getNextChoice());
        B = WindowManager.getImage(gd.getNextChoice());
        
        // Check that the image types are appropriate (masks: 8-bit).
        int [] dimA = A.getDimensions();
        int [] dimB = B.getDimensions();
        if (A.getType() != ImagePlus.GRAY8 || B.getType() != ImagePlus.GRAY8 ||
                dimA[0] != dimB[0] || dimA[1] != dimB[1] || 
                dimA[3] != dimB[3]) {
            IJ.error("Both images need to be masks (8-bit data type)" +
            		 " of equal size");
            return;
        }
        
        double jaccard = 0.0, dice = 0.0;
        double intersection = 0.0, union = 0.0;
        double a = 0.0, b = 0.0;
        
        // Iterate the masks and compute the intersection and the union.
        int [] dim = A.getDimensions();
        
        ImageStack stackA = A.getStack();
        ImageStack stackB = B.getStack();
        
        for (int z = 0; z < dim[3]; z++) {
            for (int x = 0; x < dim[0]; x++) {
                for (int y = 0; y < dim[1]; y++) {
                    a = stackA.getVoxel(x, y, z);
                    b = stackB.getVoxel(x, y, z);
                    if (a != 0 || b != 0) union += 1.0;
                    if (a != 0 && b != 0) intersection += 1.0;
                }
            }
        }
       
        // Compute both indexes
        jaccard = intersection / union;
        dice = 2 * jaccard / (1 + jaccard);
        
        // Display them on screen (use a ResultsTable)
        ResultsTable rt = ResultsTable.getResultsTable();
        rt.incrementCounter();
        rt.addValue("Images", A.getTitle() + "-" + B.getTitle());
        rt.addValue("Jaccard", jaccard);
        rt.addValue("Dice", dice);    
        rt.showRowNumbers(false);
        rt.show("Results");
    }

}
