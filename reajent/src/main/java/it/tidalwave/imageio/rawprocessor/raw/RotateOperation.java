/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.Properties;
import it.tidalwave.imageio.util.Logger;
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

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RotateOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(RotateOperation.class);
        
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        
        int rotation = getCameraOrientation(image);
        
        if (rotation != 0)
          {
            image.setImage(rotateQuadrant(image.getImage(), rotation));
            image.setRotation(rotation);
          }
      }
        
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Override
    public void processMetadata (RAWImage image) 
      {
        logger.fine("processMetadata()");

        int rotation = getCameraOrientation(image);

        if (rotation != 0)
          {
            image.setRotation(rotation);
          }
      }

    /*******************************************************************************************************************
     *
     * Reads the camera embedded orientation. This method works with EXIF data:
     * RAW processors for other formats should override this method. 
     *
     ******************************************************************************************************************/
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

        logger.finer(">>>> camera orientation: %s, degrees: %d", tiffOrientation, orientation);

        return orientation;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected static BufferedImage rotateQuadrant (BufferedImage image, int degrees)
      {
        logger.finer("rotateQuadrant(%d)", degrees);
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
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
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
