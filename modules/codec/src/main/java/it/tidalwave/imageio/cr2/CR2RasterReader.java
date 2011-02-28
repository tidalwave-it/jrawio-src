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
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.decoder.LosslessJPEGDecoder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2RasterReader extends RasterReader
  {
    private static final int BUFFER_SIZE = 128 * 1024;

    private int canonTileWidth;

    private int canonLastTileWidth;

    /*******************************************************************************************************************
     * 
     * @param canonTileWidth The canonTileWidth to set.
     * 
     ******************************************************************************************************************/
    public void setCanonTileWidth (final @Nonnegative int canonTileWidth)
      {
        this.canonTileWidth = canonTileWidth;
      }

    /*******************************************************************************************************************
     * 
     * @param canonLastTileWidth The canonLastTileWidth to set.
     * 
     ******************************************************************************************************************/
    public void setCanonLastTileWidth (final @Nonnegative int canonLastTileWidth)
      {
        this.canonLastTileWidth = canonLastTileWidth;
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * CR2 files are always compressed.
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
    protected void loadCompressedRaster (final @Nonnull RAWImageInputStream iis,
                                         final @Nonnull WritableRaster raster,
                                         final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        final LosslessJPEGDecoder jpegDecoder = new LosslessJPEGDecoder();
        final ByteOrder byteOrderSaved = iis.getByteOrder();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        jpegDecoder.reset(iis);
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME
        final int scanStride = width * pixelStride;
        
        iis.selectBitReader(-1, BUFFER_SIZE); 
        iis.setSkipZeroAfterFF(true);
        final int wholeTileCount = (canonTileWidth != 0) ? (width - canonLastTileWidth) / canonTileWidth : 0;
        final int odd = height % 2;

        for (int y = 0; y < height; y++)
          {
            final short[] rowBuffer = jpegDecoder.loadRow(iis);

            for (int x = 0; x < width; x++)
              {
                int value = rowBuffer[x];

                if (linearizationTable != null)
                  {
                    value = linearizationTable[value & 0xffff];
                  }

                int xx = x;
                int yy = y;

                if (canonTileWidth != 0)
                  {
                    int scan = y * width + x;
                    final int tileIndex = scan / (canonTileWidth * height);

                    if (tileIndex < wholeTileCount)
                      {
                        xx = scan % canonTileWidth + tileIndex * canonTileWidth;
                        yy = scan / canonTileWidth % height;
                      }

                    else
                      {
                        scan -= wholeTileCount * canonTileWidth * height;
                        xx = scan % canonLastTileWidth + wholeTileCount * canonTileWidth;
                        yy = scan / canonLastTileWidth;
                      }
                  }
                //
                // This is a very strange thing. EOS 1Ds Mark II has an odd number of rows on the sensor.
                // Looks like it's a GBRG Bayer pattern, but DNG marks it as a RGGB. So we skip the first
                // row and voila', GBRG becomes RGGB.
                //
                yy -= odd;

                if ((xx < width) && (yy >= 0) && (yy < height))
                  {
                    final int cfaIndex = (yy % 2) * 2 + (xx % 2);
                    data[yy * scanStride + xx * pixelStride + cfaOffsets[cfaIndex]] = (short)value;
                  }
              }

            ir.processImageProgress((100.0f * y) / height);
          }

        iis.setByteOrder(byteOrderSaved);
      }
  }
