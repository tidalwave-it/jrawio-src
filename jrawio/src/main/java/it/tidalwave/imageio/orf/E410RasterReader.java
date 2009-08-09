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
 * $Id: E300RasterReader.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/***********************************************************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for E-300.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: E300RasterReader.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class E410RasterReader extends ORFRasterReader
  {
    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @SuppressWarnings("empty-statement")
    protected void loadUncompressedRaster (@Nonnull final RAWImageInputStream iis,
                                           @Nonnull final WritableRaster raster,
                                           @Nonnull final RAWImageReaderSupport ir) 
      throws IOException
      {
//        logger.fine("loadUncompressedRaster()");
//        logger.finer(">>>> CFA pattern: " + cfaOffsets[0] + " " + cfaOffsets[1] + " " + cfaOffsets[2] + " " + cfaOffsets[3]);

        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME
        final int scanStride = width * pixelStride;
        selectBitReader(iis, raster, -1);
        //
        // We can rely on the fact that the array has been zeroed by the JVM,
        // so we just set nonzero samples.
        //        
        int acarry[][] = new int[2][3];

        iis.skipBytes(7);

        for (int y = 0; y < height; y++)
          {
            final int k = (y % 2) * 2;
            int ii = y * scanStride;
            
            // clear acarry
            for (int acar = 0; acar < acarry.length; acar++)
              {
                for (int acac = 0; acac < acarry[acar].length; acac++)
                  {
                    acarry[acar][acac] = 0;  
                  }
              }

            for (int x = 0; x < width; x++)
              {
                int j = x % 2;
                int carry[] = acarry[x & 1];
                int i = 2 * ((carry[2] < 3) ? 1 : 0);
              
                int nbits;
                for (nbits = 2 + i; ((carry[0] & 0xffff) >> (nbits + i) != 0); nbits++);
                
                int sign = iis.readBit() * -1;
                int low = (int)iis.readBits(2);

                int high;
                for (high = 0; high < 12; high++)
                  {
                    if (iis.readBit() != 0) 
                      {
                        break;
                      }
                  }
                
                if (high == 12)
                  {
                    high = (int)iis.readBits(16 - nbits) >> 1;
                  }

                carry[0] = (int)((high << nbits) | iis.readBits(nbits));
                int diff = (carry[0] ^ sign) + carry[1];
                carry[1] = (diff * 3 + carry[1]) >> 5;
                carry[2] = carry[0] > 16 ? 0 : carry[2] + 1;
                
                int pred;
                
                if ((y < 2) && (x < 2))
                  {
                    pred = 0;
                  }
                else if (y < 2) 
                  {
                    pred = data[ii + cfaOffsets[j + k] - 2 * pixelStride]; // BAYER(row,col-2);
                  }
                else if (x < 2) 
                  {
                    pred = data[ii + cfaOffsets[j + k] - 2 * scanStride]; // BAYER(row-2,col);
                  }
                else 
                  {
                    int w  = data[ii + cfaOffsets[j + k] - 2 * pixelStride]; // BAYER(row,col-2);
                    int n  = data[ii + cfaOffsets[j + k] - 2 * scanStride]; // BAYER(row-2,col);
                    int nw = data[ii + cfaOffsets[j + k] - 2 * scanStride -2 * pixelStride]; // BAYER(row-2,col);

                    if ((w < nw && nw < n) || (n < nw && nw < w)) 
                      {
                        if ((Math.abs(w-nw) > 32) || (Math.abs(n-nw) > 32))
                          {
                            pred = w + n - nw;
                          }
                        else
                          {
                            pred = (w + n) >> 1;
                          }
                      } 
                    else
                      {
                        pred = Math.abs(w-nw) > Math.abs(n-nw) ? w : n;
                      }
                  }
               
                final int xxx = pred + ((diff << 2) | low);
                data[ii + cfaOffsets[j + k]] = (short)xxx;
                
                if ((xxx >> 12) != 0)
                  {
                    throw new IOException("Error in decompression");
                  }
                
                ii += pixelStride;
              }
            
            ir.processImageProgress((100.0f * y) / height);
          }
      }
  }
