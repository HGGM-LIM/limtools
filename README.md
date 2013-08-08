# LIM Tools ImageJ plugins package

This plugin repository contains ImageJ plugins and macros developed by 
researchers at the Medical Imaging Laboratory, Hospital General Universitario 
Gregorio Marañón, Madrid (http://image.hggm.es).

The plugins here presented will be available under the "Plugins > LIM Tools"
menu. Below you will find a list of each one of them with a brief description.

## Average Frames

Creates a 3D image from a 4D one, averaging the frames selected by the user
in the dialog that shows up when executed.

## Dynamic Pixel Inspector

The Dynamic Pixel Inspector is useful for exploring the time-activity curves
of dynamic (2D + time or 3D + time - HyperStacks). Once activated, it will
display a plot of the gray intensity for each voxel through time (x axis)
as the mouse moves over the image. If the image has the focus and the user
presses the 'q' key, the plot stops updating in case a screenshot needs to
be taken. When the 'q' key is pressed again, the live updating resumes.

## Mask Dynamic Image

Applies a static mask to a dynamic image (with frames). All the pixels that 
have a 0 value in the mask will be set to 0 in all the frames of the dynamic
image.

## Measure Time Activity

Shows the mean time-activity values for each frame for the unmasked voxels
in the current dynamic image.
