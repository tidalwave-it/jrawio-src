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
 * $Id: PEFRasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raf;

import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * This class implements the PEF (Pentax raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: PEFRasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFRasterReader extends RasterReader
  {
    private int cfaWidth;

    private int cfaHeight;

    private boolean fujiLayout;

    public void setCFAHeight (final int cfaHeight)
      {
        this.cfaHeight = cfaHeight;
      }

    public void setCFAWidth (final int cfaWidth)
      {
        this.cfaWidth = cfaWidth;
      }

    public void setFujiLayout (final boolean fujiLayout)
      {
        this.fujiLayout = fujiLayout;
      }

    @Override
    protected boolean isCompressedRaster()
      {
        return false;
      }

    @Override
    protected void loadUncompressedRaster (final RAWImageInputStream iis,
                                           final WritableRaster raster,
                                           final RAWImageReaderSupport ir)
      throws IOException
      {
        iis.seek(this.rasterOffset);
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();

        int top_margin = 0;
        int left_margin = 64;
        int fuji_width = 2048;

        iis.skipBytes((top_margin * cfaWidth + left_margin) * 2);
        int wide = fuji_width * (fujiLayout ? 1 : 2);
        final int pixelStride = 3;
        final int scanStride = pixelStride * raster.getWidth();
        final short[] pixel = new short[wide];

        for (int row = 0; row < cfaHeight - 1; row++) // FIXME: - 1
          {
            for (int ii = 0; ii < wide; ii++)
              {
                pixel[ii] = iis.readShort();
              }

            iis.skipBytes(2 * (cfaWidth - wide));

            for (int col = 0; col < wide; col++)
              {
                int r, c;

                if (fujiLayout)
                  {
                    r = fuji_width - 1 - col + (row >> 1);
                    c = col + ((row+1) >> 1);
                  }
                else
                  {
                    r = fuji_width - 1 + row - (col >> 1);
                    c = row + ((col+1) >> 1);
                  }

                final int cfaIndex = (2 * (r & 1)) + (c & 1);
                data[c * pixelStride + r * scanStride + cfaOffsets[cfaIndex]] = (short)pixel[col];
              }
          }
      }
  }
