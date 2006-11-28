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
 * $Id: RasterReader_NIKON_D100.java,v 1.3 2006/02/25 18:53:31 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

/*******************************************************************************
 *
 * This class specializes a PixelLoader for the Nikon D100. There is some 
 * trickery to understand if a D100 NEF is compressed and in some cases there
 * are bits to skip while reading the data.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: RasterReader_NIKON_D100.java,v 1.3 2006/02/25 18:53:31 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class RasterReader_NIKON_D100 extends NEFRasterReader
  {
    private final static int D100_PADDED_SIZE = 9844736;

    private final static int D100_INTERLEAVE_COUNT = 10;

    private final static int D100_INTERLEAVE_PAD_BITS = 8;

    private final static int D100_PADDED_ROW_PAD_BITS = 80;

    private boolean padded;

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     * There is some special trickery to do for D100: the compression tag sometimes
     * is wrong.
     * 
     ******************************************************************************/
    public boolean isCompressedRaster ()
      {
        padded = (compression == COMPRESSED_NEF) && (stripByteCount == D100_PADDED_SIZE);

        return (compression == COMPRESSED_NEF) && (stripByteCount != D100_PADDED_SIZE);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     * Every 10 samples (120 bits = 15 bytes) there is a padding byte to a 16-bytes block;
     * 
     *******************************************************************************/
    protected int getSkipCountAtColumn (int x) 
      {
        if (padded && (((x - 1) % D100_INTERLEAVE_COUNT) == (D100_INTERLEAVE_COUNT - 1)))
          {
            return D100_INTERLEAVE_PAD_BITS;
          }

        return 0;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     * After every row there are 10 padding bytes.
     * 
     *******************************************************************************/
    protected int getSkipCountAtEndOfRow (int par0, int height) 
      {
        return padded ? D100_PADDED_ROW_PAD_BITS : 0;
      }
  }
