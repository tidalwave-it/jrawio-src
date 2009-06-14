/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: NEFSizeOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: NEFSizeOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(NEFSizeOperation.class);
    
    /*******************************************************************************
     *
     * @{@inheritDoc}
     *
     ******************************************************************************/
    @Override
    @Nonnull 
    protected Insets getCrop (@Nonnull final RAWImage image)
      {
        logger.fine("getCrop()");
        Insets crop = super.getCrop(image);
        final int rotation = normalizedAngle(image.getRotation());
        crop = rotate(crop, rotation);
        logger.finer(String.format(">>>> rotation: %d, crop: %s", rotation, crop));

        final NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        final NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();

        if (nceMetadata != null)
          {
            double scale = 0.5;
            Rectangle nceCrop = new Rectangle(0, 0, 0, 0);
            //
            // NCE crop settings are relative to the rotated image
            //
            nceCrop.x = (int)Math.round(nceMetadata.getCropLeft() * scale);
            nceCrop.y = (int)Math.round(nceMetadata.getCropTop() * scale);
            nceCrop.width = (int)Math.round(nceMetadata.getCropWidth() * scale);
            nceCrop.height = (int)Math.round(nceMetadata.getCropHeight() * scale);
            
            if (metadata.getPrimaryIFD().getModel().trim().equals("NIKON D1X"))
              {
                nceCrop.height /= 2; // ??  
              } 

            final Dimension size = getSize(image);
            logger.fine(String.format(">>>> original size: %s, original NCE crop: %s", size, nceCrop));
 
            // Some images needs to rotate the NCE crop (e.g. ccw90.nef)
            // others don't (e.g. Nikon_D70s_0001.NEF)
            // FIXME: but we don't know how to guess. See the trick below.
            boolean shouldRotateNCECrop = true;
            
            if (shouldRotateNCECrop)
              {  
                final Rectangle save = (Rectangle)nceCrop.clone();
                nceCrop = rotate(nceCrop, size, rotation);
                //
                // FIXME: trick to recover NCE crop quirks - see above.
                //
                if ((nceCrop.x < 0) || (nceCrop.y < 0))
                  {
                    logger.warning("Bad crop, NCE crop hadn't to be rotated");
                    nceCrop = save;  
                  }
              }
            
            if ((rotation == 90) || (rotation == 270)) // FIXME: refactor into rotateDimension
              {
                int tmp = size.width;
                size.width = size.height;
                size.height = tmp;
              }
            
            logger.fine(String.format(">>>> size: %s, NCE crop: %s", size, nceCrop));

            crop.left   += nceCrop.x;
            crop.top    += nceCrop.y;
            crop.right  += size.width - nceCrop.width;
            crop.bottom += size.height - nceCrop.height;
          }
        
        logger.fine(">>>> returning crop: %s", crop);
        
        return crop;
      }
  }
