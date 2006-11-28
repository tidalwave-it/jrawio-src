/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: SizeOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: SizeOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public abstract class SizeOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(SizeOperation.class);
    
    protected final static TagRational ONE = new TagRational(1, 1);

    protected final static TagRational[] SCALE_UNCHANGED = new TagRational[] { ONE, ONE };
        
    private Properties properties = new Properties();

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public SizeOperation()
      {
        try
          {
            String fileName = "/" + getClass().getName().replace('.', '/') + ".properties";
            InputStream is = getClass().getResourceAsStream(fileName);

            if (is != null)
              {
                properties.load(is);
                is.close();
              }
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }        
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image) throws Exception
      {
        logger.fine("process()");
        Insets crop = getCrop(image);
        
        if (crop != null)
          {
            logger.finer(">>>> crop: " + crop);
            image.setImage(crop(image.getImage(), crop));
          }
        
        Dimension size = getSize(image);
        
        if ((size != null)) // && ((cropRectangle == null) || !cropRectangle.getSize().equals(size)))
          {
            logger.finer(">>>> size: " + size);
            image.setImage(resample(image.getImage(), size));  
          }
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        logger.fine("getCrop()");
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        return getStandardCrop(primaryIFD.getModel());
      }
      
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected Dimension getSize (RAWImage image)
      {
        logger.fine("getSize()");
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        return getStandardSize(primaryIFD.getModel());
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected BufferedImage resample (BufferedImage image, Dimension dimension)
      {
        logger.warning("resample(" + dimension + ") - NOT IMPLEMENTED");
        logImage(logger, ">>>> image: ", image);
        logImage(logger, ">>>> resample returning: ", image);
        return image; // FIXME: RESAMPLE
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected BufferedImage crop (BufferedImage image, Insets crop)
      {
        logger.finer("crop(" + crop + ")");
        logImage(logger, ">>>> image: ", image);
        image = image.getSubimage(crop.left, 
                                  crop.top, 
                                  image.getWidth() - crop.left - crop.right,
                                  image.getHeight() - crop.top - crop.bottom);  
        logImage(logger, ">>>> crop returning: ", image);
        return image;
      }
    
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public Dimension getStandardSize (String model)
      {
        model = model.trim();
        logger.fine("getStandardSize(" + model + ")");
        String string = properties.getProperty(model);
        Dimension size = null;
        
        if (string != null)
          {
            StringTokenizer st = new StringTokenizer(string, " \t");
            int width = Integer.parseInt(st.nextToken());
            int height = Integer.parseInt(st.nextToken());
            size = new Dimension(width, height);
          }
        
        logger.finer(">>>> size: " + size);
        
        return size;
      }

    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public Insets getStandardCrop (String model)
      {
        model = model.trim();
        logger.fine("getStandardCrop(" + model + ")");
        String string = properties.getProperty(model);
        Insets cropInsets = null;
        
        if (string != null)
          {
            StringTokenizer st = new StringTokenizer(string, " \t");
            st.nextToken();
            st.nextToken();
            int l = Integer.parseInt(st.nextToken());
            int r = Integer.parseInt(st.nextToken());
            int t = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            cropInsets = new Insets(t, l, b, r);
          }

        logger.finer(">>>> cropInsets: " + cropInsets);
        
        return cropInsets;
      }
  }
