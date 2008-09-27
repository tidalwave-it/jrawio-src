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
        iis.skipBytes((topMargin * cfaWidth + leftMargin) * 2);
        final int width = offset * (fujiLayout ? 1 : 2);
        final int pixelStride = 3;
        final int scanStride = pixelStride * raster.getWidth();
        final short[] pixel = new short[width];

        for (int row = 0; row < cfaHeight - 1; row++) // FIXME: - 1
          {
            for (int ii = 0; ii < width; ii++)
              {
                pixel[ii] = iis.readShort();
              }

            iis.skipBytes(2 * (cfaWidth - width));

            if (fujiLayout)
              {
                for (int col = 0; col < width; col++)
                  {
                    // TODO: refactor as the case below
                    int r = offset - 1 - col + (row >> 1);
                    int c = col + ((row+1) >> 1);
                    final int cfaIndex = (2 * (r & 1)) + (c & 1);
                    data[c * pixelStride + r * scanStride + cfaOffsets[cfaIndex]] = (short)pixel[col];
                  }
              }

            else
              {
                int r = offset + row;
                int c = row;
                int scan = c * pixelStride + r * scanStride;
                
                for (int col = 0; col < width; col++)
                  {
                    if ((col % 2) == 0)
                      {
                        r--;
                        scan -= scanStride;
                      }
                    else 
                      {
                        c++;
                        scan += pixelStride;
                      }

                    final int cfaIndex = (2 * (r & 1)) + (c & 1);
                    data[scan + cfaOffsets[cfaIndex]] = (short)pixel[col];
                  }
              }
          }
      }
  }
