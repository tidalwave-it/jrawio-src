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
 * $Id: DNGSizeOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.awt.Dimension;
import java.awt.Insets;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: DNGSizeOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
public class DNGSizeOperation extends SizeOperation
  {
    private static final String CLASS = DNGSizeOperation.class.getName();
    private static final Logger logger = Logger.getLogger(CLASS);
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (@Nonnull final RAWImage image)
      {
        final TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        final IFD rasterIFD = metadata.getRasterIFD();
        final TagRational[] cropOrigin = rasterIFD.getDefaultCropOrigin();
        final TagRational[] cropSize = rasterIFD.getDefaultCropSize();
        final int imageWidth = image.getImage().getWidth();
        final int imageHeight = image.getImage().getHeight();
        int left = (int)Math.round(cropOrigin[0].doubleValue());
        int top = (int)Math.round(cropOrigin[1].doubleValue());
        int cropWidth = (int)Math.round(cropSize[0].doubleValue());
        int cropHeight = (int)Math.round(cropSize[1].doubleValue());

        final int rotation = normalizedAngle(image.getRotation());
        
        if ((rotation == 90) || (rotation == 270))
          {
            int temp = cropWidth;
            cropWidth = cropHeight;
            cropHeight = temp;
            temp = left;
            left = top;
            top = temp;
          }

        logger.finest(String.format(">>>> getCrop(): width: %d, height: %d, left: %d, top: %d, cropWidth: %d, cropHeight: %d",
                                    imageWidth, imageHeight, left, top, cropWidth, cropHeight));
        
        return new Insets(top, 
                          left, 
                          imageHeight - top - cropHeight,
                          imageWidth - left - cropWidth);
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize (@Nonnull final RAWImage image)
      {
        final TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        final IFD rasterIFD = metadata.getRasterIFD();
//        final TagRational[] cropOrigin = rasterIFD.getDefaultCropOrigin();
        final TagRational[] cropSize = rasterIFD.getDefaultCropSize();
        final TagRational[] scale = rasterIFD.isDefaultScaleAvailable() ? rasterIFD.getDefaultScale() : SCALE_UNCHANGED;

//        final int left = (int)Math.round(cropOrigin[0].doubleValue());
//        final int top = (int)Math.round(cropOrigin[1].doubleValue());
        final int cropWidth = (int)Math.round(cropSize[0].doubleValue());
        final int cropHeight = (int)Math.round(cropSize[1].doubleValue());
        final int width = (int)Math.round(cropWidth * scale[0].doubleValue());
        final int height = (int)Math.round(cropHeight * scale[1].doubleValue());
                
        return new Dimension(width, height);
      }
  }
