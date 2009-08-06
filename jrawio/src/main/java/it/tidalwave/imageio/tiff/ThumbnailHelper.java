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
package it.tidalwave.imageio.tiff;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Properties;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * This abstract class is provided as a support for implementing, by subclassing,
 * an ImageReader for any TIFF-based image format.
 *
 * @author Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ThumbnailHelper
  {
    private final static String CLASS = ThumbnailHelper.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnull
    public final IFD ifd; // FIXME: make it private

    @Nonnegative
    private int width;

    @Nonnegative
    private int height;
    
    private int offset;

    @Nonnegative 
    private int byteCount;

    @CheckForNull
    private byte[] buffer;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            @Nonnull final IFD ifd)
      {
        logger.finest("ThumbnailHelper(%s, %s)", iis, ifd);

        this.ifd = ifd; 

        // This is for JRW-198 - not a good solution, but at the moment can't figure out anything better.
        // TODO: find a better solution
        boolean trust = !"DSLR-A100".equals(ifd.getModel());

        if (ifd.isImageWidthAvailable() && trust)
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

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            final int offset, 
                            @Nonnegative final int byteCount)
      {
        logger.finest("ThumbnailHelper(%s, %d, %d)", iis, offset, byteCount);
        this.ifd = null;
        this.offset = offset;
        this.byteCount = byteCount;
        getSizeFromEmbeddedJPEG(iis);
      }
       
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            @Nonnull final byte[] buffer)
      {
        logger.finest("ThumbnailHelper(%s, %s)", iis, buffer);
        this.ifd = null;
        this.buffer = buffer;
        getSizeFromEmbeddedJPEG(iis);
      }
       
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    public ThumbnailHelper (@Nonnull final RAWImageInputStream iis, 
                            final int offset, 
                            @Nonnegative final int byteCount, 
                            @Nonnegative final int width,
                            @Nonnegative final int height)
      {
        logger.finest("ThumbnailHelper(%s, %d, %d, %d, %d)", iis, offset, byteCount, width, height);
        this.ifd = null;
        this.offset = offset;
        this.byteCount = byteCount;
        this.width = width;
        this.height = height;
      }
       
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getWidth()
      {
        return width;
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getHeight()
      {
        return height;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public BufferedImage load (@Nonnull final ImageInputStream iis) 
      throws IOException
      {
        logger.fine("load(%s)", iis);
        final byte[] buffer = getBuffer(iis);
        BufferedImage image = ImageIO.read(createInputStream(buffer));
        
        if ((image == null))
          {
            image = loadPlainImage(iis, width, height, offset, byteCount);   
          }

        return image;
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private byte[] getBuffer (@Nonnull final ImageInputStream iis) 
      throws IOException
      {
        logger.finest("getBuffer(%s)", iis);

        if (buffer != null)
          {
            return buffer;  
          }
        else
          {
            final byte[] buffer = new byte[byteCount];
            iis.seek(offset);
            iis.readFully(buffer);
            return buffer;
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void getSizeFromEmbeddedJPEG (@Nonnull final RAWImageInputStream iis)
      {           
        logger.finest("getSizeFromEmbeddedJPEG(%s)", iis);

        ImageInputStream is = null;
        ImageReader ir = null;
        
        try
          {
            final long save = iis.getStreamPosition(); // TEMP FIX for a bug
            final byte[] buffer = getBuffer(iis);
            final int b0 = buffer[0] & 0xff;
            final int b1 = buffer[1] & 0xff;
            final int magic = (b0 << 8) | b1;

            if ((magic != 0xffd8) && (magic != 0x02d8))
              {
                logger.warning("Ignoring thumbnail JPEG, starts with 0x%04x", magic);
              }
            else
              {
                ir = ImageIO.getImageReadersByFormatName("JPEG").next();
                is = ImageIO.createImageInputStream(createInputStream(buffer));
                ir.setInput(is);
                width = ir.getWidth(0);
                height = ir.getHeight(0);
                iis.seek(save);
                logger.finest(">>>> size found: %d x %d", width, height);
              }
          } 
        catch (IOException e)
          {
            logger.throwing(CLASS, "getSizeFromEmbeddedJPEG()", e);
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
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected InputStream createInputStream (@Nonnull final byte[] buffer)
      {
        return new ByteArrayInputStream(buffer);
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage loadPlainImage (@Nonnull final ImageInputStream iis, 
                                            @Nonnull final int width, 
                                            @Nonnull final int height, 
                                            final int offset, 
                                            @Nonnegative int length) 
      throws IOException
      {
        logger.fine("loadPlainImage(%s, %d, %d, %d, %d)", iis, width, height, offset, length);
        
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
