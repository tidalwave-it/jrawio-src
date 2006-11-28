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
 * $Id: E300RasterReader.java,v 1.2 2006/02/25 16:58:05 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import java.io.IOException;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for E-300.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: E300RasterReader.java,v 1.2 2006/02/25 16:58:05 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class E300RasterReader extends ORFRasterReader
  {
    protected void loadUncompressedRaster (RAWImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir) throws IOException      
      {
//        logger.fine("loadUncompressedRaster()");
//        logger.finer(">>>> CFA pattern: " + cfaOffsets[0] + " " + cfaOffsets[1] + " " + cfaOffsets[2] + " " + cfaOffsets[3]);

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        setBitsPerSample(12);
        selectBitReader(iis, raster, -1);
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
                int b0 = iis.readByte() & 0xff;
                int b1 = iis.readByte() & 0xff;
                int b2 = iis.readByte() & 0xff;
                
                int sample1 = ((b1 << 8) | b0) & 0xfff;
                int sample2 = ((b2 << 4) | (b1 >>> 4)) & 0xfff;
                
                if (linearizationTable != null)
                  {
                    sample1 = linearizationTable[sample1];
                    sample2 = linearizationTable[sample2];
                  }

                int j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample1;
                x++;
                i += pixelStride;
                j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample2;
                i += pixelStride;
                
                if (((x+1) % 10) == 0)
                  {
                    iis.readByte();  
                  }
              }

            ir.processImageProgress((100.0f * y) / height);
          }
      }
  }
