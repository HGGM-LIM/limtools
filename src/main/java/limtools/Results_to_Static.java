package limtools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

/**
 * <p>
 * This plugin creates a new 3D image with the values extracted from a 
 * {@link ResultsTable} in a format very similar to that employed by the
 * {@link Dynamic_to_Results} plugin.
 * </p>
 * 
 * @author José María Mateos - jmmateos@hggm.es
 * 
 */
public class Results_to_Static implements PlugIn {

    @Override
    public void run(String args) {

        ResultsTable rt = Analyzer.getResultsTable();
        
        // Is the number of columns right?
        int nCols = rt.getLastColumn();
        if (nCols == -1) { 
            IJ.error("The Results table is empty.");
            return;
        } else if (nCols < 3) {
            IJ.error("The Results table does not contain enough columns.");
            return;
        }
        
        // Are the headings the right ones?
        String [] headings = rt.getHeadings();
        if (!headings[0].equals("x") || !headings[1].equals("y") 
                || !headings[2].equals("slice")) {
            IJ.error("The headings for the first three columns are wrong.");
            return;
        }            
        
        int [] x = _float2int(rt.getColumn(0));
        int [] y = _float2int(rt.getColumn(1));
        int [] slices = _float2int(rt.getColumn(2));
        float [] param = rt.getColumn(3);
        
        int max_x = _getMax(x);
        int max_y = _getMax(y);
        int max_slice = _getMax(slices);
        
        // We need to ask the user for the image dimensions, as there is no
        // way of guessing them from the coordinate values (the voxels have
        // probably been masked beforehand). In any case, use the maximum
        // read value for each dimension.
        GenericDialog gd = new GenericDialog("Please set the image dimensions");
        gd.addNumericField("X", max_x, 0);
        gd.addNumericField("Y", max_y, 0);
        gd.addNumericField("Slices", max_slice, 0);
        gd.showDialog();
        
        if (gd.wasCanceled())
            return;
        
        int dim_x = (int)Math.round(gd.getNextNumber());
        int dim_y = (int)Math.round(gd.getNextNumber());
        int dim_slice = (int)Math.round(gd.getNextNumber());
        
        // Check that the user provided values are correct
        if (dim_x < max_x || dim_y < max_y || dim_slice < max_slice) {
            IJ.error("The provided values are lower than some of the " +
                     "coordinates read from the Results table.");
            return;
        }
        
        // Build the image
        ImagePlus imp = IJ.createImage("Results to Static image", dim_x, dim_y, 
                                       dim_slice, 32);
        ImageStack is = imp.getStack();
        // Populate the stack
        for (int i = 0; i < x.length; i++) {
            is.setVoxel(x[i], y[i], slices[i] - 1, param[i]);
        }
        
        // Display the image
        imp.show(); 
    }
    
    // Converts a float array into an integer array. This is used to cast the
    // coordinate values.
    private int [] _float2int(float [] data) {
        int [] res = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = Math.round(data[i]);
        }
        return res;
    }
    
    // Gets the maximum integer value from an array.
    private int _getMax(int [] data) {
        int res = -Integer.MAX_VALUE;
        for (int i : data) {
            if (i > res)
                res = i;
        }
        return res;
    }
}
