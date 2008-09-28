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

    private int offset;

    private int topMargin;

    private int leftMargin;

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setCFAHeight (final int cfaHeight)
      {
        this.cfaHeight = cfaHeight;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setCFAWidth (final int cfaWidth)
      {
        this.cfaWidth = cfaWidth;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setFujiLayout (final boolean fujiLayout)
      {
        this.fujiLayout = fujiLayout;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setOffset (final int offset)
      {
        this.offset = offset;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setLeftMargin (final int leftMargin)
      {
        this.leftMargin = leftMargin;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    public void setTopMargin (final int topMargin)
      {
        this.topMargin = topMargin;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    @Override
    protected boolean isCompressedRaster()
      {
        return false;
      }

    /***************************************************************************
     *
     *
     ***************************************************************************/
    @Override
    protected void loadUncompressedRaster (final RAWImageInputStream iis,
                                           final WritableRaster raster,
                                           final RAWImageReaderSupport ir)
      throws IOException
      {
        iis.seek(this.rasterOffset);
        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
//        iis.skipBytes((topMargin * cfaWidth + leftMargin) * 2);
        final int width = offset * (fujiLayout ? 1 : 2);
        final int pixelStride = 3;
        final int scanStride = pixelStride * raster.getWidth();

        for (int y = 0; y < cfaHeight - 1; y++) // FIXME: - 1
          {
            if (fujiLayout)
              {
                for (int x = 0; x < width; x++)
                  {
                    // TODO: refactor as the case below
                    int y0 = offset - 1 - x + (y >> 1);
                    int x0 = x + ((y + 1) >> 1);
                    final int cfaIndex = (2 * (y0 & 1)) + (x0 & 1);
                    data[x0 * pixelStride + y0 * scanStride + cfaOffsets[cfaIndex]] = iis.readShort();
                  }
              }

            else
              {
                int y0 = offset + y;
                int x0 = y;
                int scan = x0 * pixelStride + y0 * scanStride;
                
                for (int col = 0; col < width; col++)
                  {
                    if ((col % 2) == 0)
                      {
                        y0--;
                        scan -= scanStride;
                      }
                    else 
                      {
                        x0++;
                        scan += pixelStride;
                      }

                    final int cfaIndex = (2 * (y0 & 1)) + (x0 & 1);
                    data[scan + cfaOffsets[cfaIndex]] = iis.readShort();
                  }
              }

            iis.skipBytes(2 * (cfaWidth - width));
          }
      }

    /***************************************************************************
     *
     * Fuji seems to be GMYC, so we need four bands.
     *
     ***************************************************************************/
//    @Override
//    protected int[] getBandOffsets()
//      {
//        return new int[] { 0, 1, 2, 3};
//      }
  }
