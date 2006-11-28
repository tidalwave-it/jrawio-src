/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: DNGSizeOperation.java,v 1.1 2006/02/17 15:32:13 fabriziogiudici Exp $
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
 * @version CVS $Id: DNGSizeOperation.java,v 1.1 2006/02/17 15:32:13 fabriziogiudici Exp $
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
