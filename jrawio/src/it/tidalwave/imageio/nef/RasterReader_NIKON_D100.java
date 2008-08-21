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
 * $Id: RasterReader_NIKON_D100.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
 * @version $Id: RasterReader_NIKON_D100.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
