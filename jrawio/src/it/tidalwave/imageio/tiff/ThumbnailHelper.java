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
 * $Id: ThumbnailHelper.java 76 2008-08-23 22:03:28Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * This abstract class is provided as a support for implementing, by subclassing,
 * an ImageReader for any TIFF-based image format.
 *
 * @author Fabrizio Giudici
 * @version $Id: ThumbnailHelper.java 76 2008-08-23 22:03:28Z fabriziogiudici $
 *
 ******************************************************************************/
public class ThumbnailHelper
  {
    private final static String CLASS = ThumbnailHelper.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnull
    public final IFD ifd; // FIXME: make it private
    
    private int width;
    
    private int height;
    
    private int offset;
    
    private int byteCount;
    
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            @Nonnull final IFD ifd)
      {
        this.ifd = ifd; 

        if (ifd.isImageWidthAvailable())
          {
            width = ifd.getImageWidth();  
            height = ifd.getImageLength();  
          }        
        else
          {
            offset = ifd.getJPEGInterchangeFormat();
            byteCount = ifd.getJPEGInterchangeFormatLength();
            getSizeFromEmbeddedJPEG(iis);
          }
        // 
        // Try first JPEG, since some formats have all information (raster+thumbnail) in the same IFD.
        //
        if (ifd.isJPEGInterchangeFormatAvailable())
          {
            offset = ifd.getJPEGInterchangeFormat();
            byteCount = ifd.getJPEGInterchangeFormatLength();
          }
        
        if (ifd.isStripOffsetsAvailable())
          {
            offset = ifd.getStripOffsets();
            byteCount = ifd.getStripByteCounts();
          }
      }

    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            final int offset, 
                            @Nonnegative final int byteCount)
      {
        this.ifd = null;
        this.offset = offset;
        this.byteCount = byteCount;
        getSizeFromEmbeddedJPEG(iis);
      }
       
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            final int offset, 
                            @Nonnegative final int byteCount, 
                            final int width, 
                            final int height)
      {
        this.ifd = null;
        this.offset = offset;
        this.byteCount = byteCount;
        this.width = width;
        this.height = height;
      }
       
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public int getWidth()
      {
        return width;
      }
    
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public int getHeight()
      {
        return height;
      }

    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    public BufferedImage load (@Nonnull final ImageInputStream iis) 
      throws IOException
      {
        return load(iis, offset, byteCount);
      }
    
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    private BufferedImage load (@Nonnull final ImageInputStream iis, 
                                final int offset, 
                                @Nonnegative int byteCount) 
      throws IOException
      {
        logger.fine(String.format("load(%s, %d, %d", iis, offset, byteCount));
        final byte[] buffer = new byte[byteCount];
        iis.seek(offset);
        iis.read(buffer);
        BufferedImage image = ImageIO.read(createInputStream(buffer));
        
        if ((image == null))
          {
            image = loadPlainImage(iis, width, height, offset, byteCount);   
          }

        return image;
      }

    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    private void getSizeFromEmbeddedJPEG (@Nonnull final RAWImageInputStream iis)
      {           
        ImageInputStream is = null;
        ImageReader ir = null;
        
        try
          {
            final byte[] buffer = new byte[byteCount];
            final long save = iis.getStreamPosition(); // TEMP FIX for a bug
            iis.seek(offset);
            iis.read(buffer);
            ir = ImageIO.getImageReadersByFormatName("JPEG").next();
            is = ImageIO.createImageInputStream(createInputStream(buffer));
            ir.setInput(is);
            width = ir.getWidth(0);
            height = ir.getHeight(0);
            iis.seek(save);
          } 
        catch (IOException e)
          {
            e.printStackTrace(); // FIXME
          }
        finally
          {
            if (is != null)
              {
                try 
                  {
                    is.close();
                  }
                catch (IOException e)
                  {
                    logger.warning("While closing stream " + e);
                  }
              }
            
            if (ir != null)
              {
                ir.dispose();
              }
          }
      }
    
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    @Nonnull
    protected InputStream createInputStream (@Nonnull final byte[] buffer)
      {
        return new ByteArrayInputStream(buffer);
      }
    
    /***************************************************************************
     *
     *
     *
     **************************************************************************/
    protected BufferedImage loadPlainImage (@Nonnull final ImageInputStream iis, 
                                            @Nonnull final int width, 
                                            @Nonnull final int height, 
                                            final int offset, 
                                            @Nonnegative int length) 
      throws IOException
      {
        //  logger.fine("loadPlainImage(iis: " + iis + ", offset: " + offset + ")");          
        final int pixelStride = 3;
        final int scanlineStride = pixelStride * width;
        final int[] bandOffsets = { 0, 1, 2 }; // FIXME
        final WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, scanlineStride,
                                                                     pixelStride, bandOffsets, null);
        iis.seek(offset); // FIXME: does not support multiple strips
        final DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
        iis.readFully(dataBuffer.getData(), 0, scanlineStride * height);
  
        final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        final ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        final Properties properties = new Properties();
        final BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, properties);  
        
        //logger.fine(">>>> loadPlainImage() completed ok in " + (System.currentTimeMillis() - time) + " msec.");

        return bufferedImage;
      }
  }
