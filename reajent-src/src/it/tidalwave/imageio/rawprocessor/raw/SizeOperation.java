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
 * $Id: SizeOperation.java,v 1.1 2006/02/17 15:31:55 fabriziogiudici Exp $
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
 * @version CVS $Id: SizeOperation.java,v 1.1 2006/02/17 15:31:55 fabriziogiudici Exp $
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
