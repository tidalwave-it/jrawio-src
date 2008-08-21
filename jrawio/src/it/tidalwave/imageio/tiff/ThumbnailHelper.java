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
 * $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 * @version $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ThumbnailHelper
  {
    public IFD ifd;
    
    private int width;
    
    private int height;
    
    private int offset;
    
    private int byteCount;
    
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public ThumbnailHelper (RAWImageInputStream iis, IFD ifd)
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

    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public ThumbnailHelper (RAWImageInputStream iis, int offset, int byteCount)
      {
        this.offset = offset;
        this.byteCount = byteCount;
        getSizeFromEmbeddedJPEG(iis);
      }
       
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public ThumbnailHelper (RAWImageInputStream iis, int offset, int byteCount, int width, int height)
      {
        this.offset = offset;
        this.byteCount = byteCount;
        this.width = width;
        this.height = height;
      }
       
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public int getWidth()
      {
        return width;
      }
    
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public int getHeight()
      {
        return height;
      }

    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    public BufferedImage load (ImageInputStream iis) throws IOException
      {
        return load(iis, offset, byteCount);
      }
    
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    private BufferedImage load (ImageInputStream iis, int offset, int byteCount) throws IOException
      {
        byte[] buffer = new byte[byteCount];
        iis.seek(offset);
        iis.read(buffer);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
        
        if ((image == null))
          {
//            image = loadPlainImage(iis, ifd.getImageWidth(), ifd.getImageLength(), offset, byteCount);   
            image = loadPlainImage(iis, width, height, offset, byteCount);   
          }

        return image;
      }

    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    private void getSizeFromEmbeddedJPEG (RAWImageInputStream iis)
      {           
        try
          {
            byte[] buffer = new byte[byteCount];
            long save = iis.getStreamPosition(); // TEMP FIX for a bug
            iis.seek(offset);
            iis.read(buffer);
            ImageReader ir = (ImageReader)(ImageIO.getImageReadersByFormatName("JPEG").next());
            ir.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(buffer)));
            width = ir.getWidth(0);
            height = ir.getHeight(0);
            iis.seek(save);
          } 
        
        catch (IOException e)
          {
            e.printStackTrace(); // FIXME
          }
      }
    
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    protected BufferedImage loadPlainImage (ImageInputStream iis, int width, int height, int offset, int length) throws IOException
      {
        //  logger.fine("loadPlainImage(iis: " + iis + ", offset: " + offset + ")");          
        int pixelStride = 3;
        int scanlineStride = pixelStride * width;
        int[] bandOffsets = { 0, 1, 2 }; // FIXME
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, scanlineStride,
            pixelStride, bandOffsets, null);
        iis.seek(offset); // FIXME: does not support multiple strips
        DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
        iis.readFully(dataBuffer.getData(), 0, scanlineStride * height);
  
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        Properties properties = new Properties();

        BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, properties);  
        
        //logger.fine(">>>> loadPlainImage() completed ok in " + (System.currentTimeMillis() - time) + " msec.");

        return bufferedImage;
      }
  }
