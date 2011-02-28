/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.arw;

import javax.annotation.Nonnull;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/***********************************************************************************************************************
 *
 * This class implements the ARW raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWRasterReader extends RasterReader
  {
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    protected boolean isCompressedRaster()
      {
        return true;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    protected void loadCompressedRaster (@Nonnull final RAWImageInputStream iis,
                                         @Nonnull final WritableRaster raster,
                                         @Nonnull final RAWImageReaderSupport ir) 
      throws IOException
      {
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME
        final int scanStride = pixelStride * width;

        selectBitReader(iis, raster, bitsPerSample);
        iis.seek(rasterOffset);
//        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        
        int sum = 0;
//        int kount = 0;
        
        for (int x = width; x-- > 0; )
          {
            for (int y = 0; y < height + 1; y += 2) 
              {
                if (y == height) 
                  {
                    y = 1;
                  }
              
                final int k = (y % 2) * 2;
                int len = 4 - (int)iis.readBits(2);
              
                if ((len == 3) && (iis.readBits(1) != 0))
                  {
                    len = 0;
                  }
              
                if (len == 4)
                  {
                    while ((len < 17) && (iis.readBit() == 0)) 
                      {
                        len++;
                      }
                  }
                
                int diff = (int)iis.readBits(len);
              
                if ((diff & (1 << (len-1))) == 0)
                  {
                    diff -= (1 << len) - 1;
                  }
              
                if (((sum += diff) >> 12) != 0)
                  {
                    throw new IOException("sums += diff >> 12 != 0");
                  }
              
                int i = x * pixelStride + y * scanStride; // FIXME optimize, compute incrementally

                if (y < height)   
                  {
                    int j = x % 2;
                    data[i + cfaOffsets[j + k]] = (short)sum;
                    
//                    if (kount++ < 10)
//                      System.err.printf("BAYER[%d,%d] = %x\n", x, y, sum);
                  }
              }
              
            ir.processImageProgress((100.0f * (width - x)) / width);
          }
      }
    
    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        return String.format("ARWRasterReader@%x", System.identityHashCode(this));
      }
  }
