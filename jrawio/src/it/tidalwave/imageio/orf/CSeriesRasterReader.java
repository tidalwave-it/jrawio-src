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
 * $Id: CSeriesRasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import java.awt.image.WritableRaster;
import java.io.IOException;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for C-series.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: CSeriesRasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class CSeriesRasterReader extends ORFRasterReader
  {
    /** Size of a row in bytes. */
    private int rowByteCount;
    
    private final static int BITS_PER_PIXEL = 8;
    
    private final static int BITS_COUNT = 11;
    
    private final static int MASK = (1 << (BITS_COUNT + 1)) - 1;
    
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void loadUncompressedRaster (RAWImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir) throws IOException
      {
        rowByteCount = (raster.getWidth() * bitsPerSample) / 8;
        super.loadUncompressedRaster(iis, raster, ir);  
      }
    
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     * C-series rasters are interlaced.
     * 
     *******************************************************************************/
    protected int getRow (int y, int height)
      {
        return (y <= (height / 2)) ? (y * 2) : ((y - height / 2) * 2 - 1);
      }
    
    /*******************************************************************************
     * 
     * The second set of interlaced rows starts at an offset with the BITS_COUNT
     * least significant bits to zero. Pad appropriately.
     * 
     *******************************************************************************/
    protected int getSkipCountAtEndOfRow (int y, int height)
      {
        if (y != (height / 2))
          {
            return 0;  
          }
        
        int delta = MASK + 1 - (((y + 1) * rowByteCount) & MASK);
        
        return delta * BITS_PER_PIXEL;
      }
  }
