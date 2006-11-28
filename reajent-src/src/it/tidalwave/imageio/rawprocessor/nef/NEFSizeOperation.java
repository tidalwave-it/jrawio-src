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
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sellz
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
 * $Id: NEFSizeOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.awt.Dimension;
import java.util.logging.Logger;
import java.awt.Insets;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFSizeOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(NEFSizeOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        logger.fine("getCrop()");
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();
        Insets crop = super.getCrop(image);
        
        if (crop == null) // e.g. a new camera not descripted in NEFSizeOperation.properties
          {
            crop = new Insets(0, 0, 0, 0);
          }

        logger.finer(">>>> Standard crop - left: " + crop.left + ", top: " + crop.top + ", right: " + crop.right + ", bottom: " + crop.bottom);

        if (nceMetadata != null)
          {
            double scale = 0.5;
            int left = (int)Math.round(nceMetadata.getCropLeft() * scale);
            int top = (int)Math.round(nceMetadata.getCropTop() * scale);
            int right = (int)Math.round(nceMetadata.getCropWidth() * scale);
            int bottom = (int)Math.round(nceMetadata.getCropHeight() * scale);
            logger.fine(">>>> NCE crop - left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
            
            if (metadata.getPrimaryIFD().getModel().trim().equals("NIKON D1X"))
              {
                bottom /= 2; // ??  
              } 

            Dimension size = getSize(image);
            logger.fine(">>>> Standard size: " + size.width + " x " + size.height);

            crop.left += left;
            crop.top += top;
            crop.right += size.width - right;
            crop.bottom += size.height - bottom;
          }
        
        logger.fine(">>>> returning: " + crop);
        
        return crop;
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
/*    protected Dimension getSize (RAWImage image)
      {
        logger.fine("getSize()");
                
        return new Dimension(width, height);
      }*/
  }
