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
 * $Id: NDFRasterReader.java,v 1.2 2006/02/08 20:08:45 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NDFRasterReader.java,v 1.2 2006/02/08 20:08:45 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NDFRasterReader extends NEFRasterReader
  {
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void loadUncompressedRaster (ImageInputStream iis,
                                          WritableRaster raster,
                                          RAWImageReaderSupport ir,
                                          int whiteLevel) throws IOException
      {
        /*
         //logger.fine("loadUncompressedImage()");
         DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
         short[] data = dataBuffer.getData();
         int typeBits = DataBuffer.getDataTypeSize(dataBuffer.getDataType());
         int width = raster.getWidth();
         int height = raster.getHeight();
         BitReader fbr = createBitReader(iis, ifd, raster, bitsPerSample);
         int shift = (typeBits - bitsPerSample);

         while (whiteLevel < ((1 << bitsPerSample) - 1)) // assumes whiteLevel is 2**n - 1
         {
         shift++;
         whiteLevel = (whiteLevel << 1) | 1;
         }

         int mask = (1 << shift) - 1;
         int pixelStride = 3;
         int scanStride = width * pixelStride;
         //
         // We can rely on the fact that the array has been zeroed by the JVM,
         // so we just set nonzero samples.
         //
         for (int y = 0; y < height; y++)
         {
         int row = getRow(y, height);
         int i = row * scanStride;
         int k = (row % 2) * 2;

         for (int x = 0; x < width; x++)
         {
         fbr.skipBits(getSkipCountAtColumn(x));
         int sample = fbr.readBits(bitsPerSample);
         sample = (sample << shift) | ((sample >> (bitsPerSample - shift)) & mask);
         data[i] = (short)sample;
         i += pixelStride;
         }

         fbr.skipBits(getSkipCountAtEndOfRow());

         if (ir != null)
         {
         ir.processImageProgress((100.0f * y) / height);
         }
         }
         */
      }
  }
