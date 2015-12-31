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
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/***********************************************************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for C-series.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CSeriesRasterReader extends ORFRasterReader
  {
    private final static int BITS_PER_PIXEL = 8;
    
    private final static int BITS_COUNT = 11;
    
    private final static int MASK = (1 << (BITS_COUNT + 1)) - 1;
    
    /** Size of a row in bytes. */
    private int rowByteCount;
    
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
        rowByteCount = (raster.getWidth() * bitsPerSample) / 8;
        super.loadUncompressedRaster(iis, raster, ir);  
      }
    
    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     * C-series rasters are interlaced.
     * 
     ******************************************************************************************************************/
    @Override
    protected int getRow (@Nonnegative final int y, 
                          @Nonnegative final int height)
      {
        return (y <= (height / 2)) ? (y * 2) : ((y - height / 2) * 2 - 1);
      }
    
    /*******************************************************************************************************************
     * 
     * The second set of interlaced rows starts at an offset with the BITS_COUNT
     * least significant bits to zero. Pad appropriately.
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnegative
    protected int getSkipCountAtEndOfRow (@Nonnegative final int y,
                                          @Nonnegative final int height)
      {
        if (y != (height / 2))
          {
            return 0;  
          }
        
        final int delta = MASK + 1 - (((y + 1) * rowByteCount) & MASK);
        
        return delta * BITS_PER_PIXEL;
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
        return String.format("CSeriesRasterReader@%x", System.identityHashCode(this));
      }
  }
