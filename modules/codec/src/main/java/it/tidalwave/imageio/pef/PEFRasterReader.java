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
package it.tidalwave.imageio.pef;

import javax.annotation.Nonnull;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.util.Logger;
import javax.annotation.CheckForNull;

/***********************************************************************************************************************
 *
 * This class implements the PEF (Pentax raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PEFRasterReader extends RasterReader
  {
    private final static String CLASS = PEFRasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    @CheckForNull
    private PEFDecoder decoder;

    @Override
    protected boolean isCompressedRaster()
      {
        return decoder != null;
      }

    public void setDecoder (final @Nonnull PEFDecoder decoder)
      {
        this.decoder = decoder;
      }

    @Override
    protected void loadCompressedRaster (final @Nonnull RAWImageInputStream iis,
                                         final @Nonnull WritableRaster raster,
                                         final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME

        iis.seek(rasterOffset); // FIXME
        final short vPredictor[][] = new short[2][2];
        final short hPredictor[] = new short[2];

        for (int i = 0, y = 0; y < height; y++)
          {
            for (int x = 0; x < width; x++)
              {
                final int bitCount = decoder.decode(12, true, iis, false); // TODO: 12 -> bitsPerSample

        //          if (len == 16 && (!dng_version || dng_version >= 0x1010000))
        //            return -32768;
                int diff = decoder.decode(bitCount, false, iis, false);

                if ((diff & (1 << (bitCount-1))) == 0)
                  {
                    diff -= (1 << bitCount) - 1;
                  }

                if (x < 2)
                  {
                    hPredictor[x] = vPredictor[y & 1][x] += diff;
                  }
                else
                  {
                    hPredictor[x & 1] += diff;
                  }

                if (x < width)
                  {
                    final int cfaIndex = (2 * (y & 1)) + (x & 1);
                    data[i + cfaOffsets[cfaIndex]] = hPredictor[x & 1];
                  }

                i += pixelStride;
                assert (hPredictor[x & 1] >>> 12) == 0; // TODO: 12 -> bitsPerSample
              }
          }
      }
  }
