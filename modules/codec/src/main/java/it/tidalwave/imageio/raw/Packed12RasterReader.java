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
package it.tidalwave.imageio.raw;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class Packed12RasterReader extends RasterReader
  {
    private final static String CLASS = Packed12RasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
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
        selectBitReader(iis, raster, 12);
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
                int sample = (int)iis.readBits(12);

                if (linearizationTable != null)
                  {
                    sample = linearizationTable[sample];
                  }

                final int j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample;
                endOfColumn(x, iis);
                i += pixelStride;
              }

            ir.processImageProgress((100.0f * y) / height);
            endOfRow(y, iis);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected void endOfColumn (final @Nonnegative int x,
                                final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected void endOfRow (final @Nonnegative int y,
                             final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
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
        return String.format("Packed12RasterReader@%x", System.identityHashCode(this));
      }
  }
