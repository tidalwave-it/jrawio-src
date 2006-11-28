/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: CSeriesRasterReader.java,v 1.3 2006/02/25 18:53:33 fabriziogiudici Exp $
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
 * @version CVS $Id: CSeriesRasterReader.java,v 1.3 2006/02/25 18:53:33 fabriziogiudici Exp $
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
