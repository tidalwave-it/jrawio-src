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
 * $Id: DNGSizeOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

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
 * @version $Id: DNGSizeOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class DNGSizeOperation extends SizeOperation
  {
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD rasterIFD = metadata.getRasterIFD();
        TagRational[] cropOrigin = rasterIFD.getDefaultCropOrigin();
        TagRational[] cropSize = rasterIFD.getDefaultCropSize();

        int left = (int)Math.round(cropOrigin[0].doubleValue());
        int top = (int)Math.round(cropOrigin[1].doubleValue());
        int cropWidth = (int)Math.round(cropSize[0].doubleValue());
        int cropHeight = (int)Math.round(cropSize[1].doubleValue());
        
        return new Insets(top, 
                          left, 
                          image.getImage().getHeight() - top - cropHeight,
                          image.getImage().getWidth() - left - cropWidth);
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Dimension getSize (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD rasterIFD = metadata.getRasterIFD();
        TagRational[] cropOrigin = rasterIFD.getDefaultCropOrigin();
        TagRational[] cropSize = rasterIFD.getDefaultCropSize();
        TagRational[] scale = rasterIFD.isDefaultScaleAvailable() ? rasterIFD.getDefaultScale() : SCALE_UNCHANGED;

        int left = (int)Math.round(cropOrigin[0].doubleValue());
        int top = (int)Math.round(cropOrigin[1].doubleValue());
        int cropWidth = (int)Math.round(cropSize[0].doubleValue());
        int cropHeight = (int)Math.round(cropSize[1].doubleValue());
        int width = (int)Math.round(cropWidth * scale[0].doubleValue());
        int height = (int)Math.round(cropHeight * scale[1].doubleValue());
                
        return new Dimension(width, height);
      }
  }
