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
 * $Id: RotateOperation.java,v 1.1 2006/02/17 15:31:52 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.Properties;
import java.util.logging.Logger;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RotateOperation.java,v 1.1 2006/02/17 15:31:52 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class RotateOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(RotateOperation.class);
        
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image) throws Exception
      {
        logger.fine("process()");
        
        int rotation = getCameraOrientation(image);
        
        if (rotation != 0)
          {
           image.setImage(rotateQuadrant(image.getImage(), rotation));
          }
      }
        
    /*******************************************************************************
     *
     * Reads the camera embedded orientation. This method works with EXIF data:
     * RAW processors for other formats should override this method. 
     *
     ******************************************************************************/
    protected int getCameraOrientation (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        IFD exifIFD = metadata.getExifIFD();
        int orientation = 0;
        IFD.Orientation tiffOrientation = null;

        if (primaryIFD.isOrientationAvailable())
          {
            tiffOrientation = primaryIFD.getOrientation();
          }
          
        else if (exifIFD.isOrientationAvailable())
          {
            tiffOrientation = exifIFD.getOrientation();
          }
                    
        if (tiffOrientation == IFD.Orientation.LEFT_BOTTOM)
          {
            orientation = -90;
          }

        if (tiffOrientation == IFD.Orientation.RIGHT_TOP)
          {
            orientation = 90;
          }

        logger.finer(">>>> camera orientation: " + tiffOrientation + ", degrees: "+ orientation);

        return orientation;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static BufferedImage rotateQuadrant (BufferedImage image, int degrees)
      {
        logger.finer("rotateQuadrant(" + degrees + ")");
        logImage(logger, ">>>> image: ", image);
        SampleModel sampleModel = image.getSampleModel();
        
        if (degrees < 0)
          {
            degrees += 360;  
          }

        if ((degrees == 90) || (degrees == 270))
          {
            sampleModel = sampleModel.createCompatibleSampleModel(image.getHeight(), image.getWidth());
          }

        WritableRaster newRaster = Raster.createWritableRaster(sampleModel, null);
        ColorModel colorModel = image.getColorModel();
        BufferedImage result = new BufferedImage(colorModel, newRaster, false, getProperties(image));

        Graphics2D g2d = (Graphics2D)result.getGraphics();

        try
          {
            double radians = Math.toRadians(degrees);
            g2d.transform(AffineTransform.getRotateInstance(radians));

            int x = 0;
            int y = 0;

            switch (degrees)
              {
                case 90:
                  y = -image.getHeight();

                  break;

                case 180:
                  x = -image.getWidth();
                  y = -image.getHeight();

                  break;

                case 270:
                  x = -image.getWidth();

                  break;
              }

            g2d.drawImage(image, x, y, null);
          }

        finally
          {
            g2d.dispose();
          }

        logImage(logger, ">>>> rotateQuadrant() returning ", result);
        
        return result;
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static Properties getProperties (BufferedImage image)
      {
        Properties properties = new Properties();
        String[] propertyNames = image.getPropertyNames();

        if (propertyNames != null)
          {
            for (int i = 0; i < propertyNames.length; i++)
              {
                String propertyName = propertyNames[i];
                Object propertyValue = image.getProperty(propertyName);
                properties.setProperty(propertyName, propertyValue.toString());
              }
          }

        return properties;
      }  
  }
