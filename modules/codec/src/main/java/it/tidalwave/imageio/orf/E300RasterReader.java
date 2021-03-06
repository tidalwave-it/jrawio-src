/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for E-300.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class E300RasterReader extends RasterReader
  {
    private final static String CLASS = E300RasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    // This is different than Packed12RasterReader for the ordering of nibbles; TODO: perhaps can be merged?
    @Override
    protected void loadUncompressedRaster (@Nonnull final RAWImageInputStream iis,
                                           @Nonnull final WritableRaster raster,
                                           @Nonnull final RAWImageReaderSupport ir)
      throws IOException
      {
        logger.fine("loadUncompressedRaster(%s, %s, %s)", iis, raster, ir);

        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME
        final int scanStride = width * pixelStride;
        setBitsPerSample(12);
        selectBitReader(iis, raster, -1);
        //
        // We can rely on the fact that the array has been zeroed by the JVM,  so we just set nonzero samples.
        //
        for (int y = 0; y < height; y++)
          {
            final int row = getRow(y, height);
            final int k = (row % 2) * 2;
            int i = row * scanStride;

            for (int x = 0; x < width; x++)
              {
                final int b0 = iis.readByte() & 0xff;
                final int b1 = iis.readByte() & 0xff;
                final int b2 = iis.readByte() & 0xff;

                int sample1 = ((b1 << 8) | b0) & 0xfff;
                int sample2 = ((b2 << 4) | (b1 >>> 4)) & 0xfff;

                if (linearizationTable != null)
                  {
                    sample1 = linearizationTable[sample1];
                    sample2 = linearizationTable[sample2];
                  }

                int j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample1;
                endOfColumn(x, iis);
                i += pixelStride;
                x++;
                j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample2;
                endOfColumn(x, iis);
                i += pixelStride;
              }

            ir.processImageProgress((100.0f * y) / height);
          }
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    protected void endOfColumn (final @Nonnegative int x, final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        if (((x + 1) % 10) == 0)
          {
            iis.readByte();
//            iis.skipBits(8);
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
        return String.format("E300RasterReader@%x", System.identityHashCode(this));
      }
  }
