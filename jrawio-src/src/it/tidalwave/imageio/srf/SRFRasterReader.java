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
 * $Id: SRFRasterReader.java,v 1.2 2006/02/08 20:21:17 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteOrder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/*******************************************************************************
 *
 * This class implements the SRF (Sony Raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: SRFRasterReader.java,v 1.2 2006/02/08 20:21:17 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class SRFRasterReader extends RasterReader
  {
    private int rasterKey;

    /*******************************************************************************
     * 
     * @param rasterKey
     * 
     *******************************************************************************/
    public void setRasterKey (int rasterKey)
      {
        this.rasterKey = rasterKey;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    protected boolean isCompressedRaster ()
      {
        return false;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    protected void loadUncompressedRaster (RAWImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir) throws IOException
      {
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        //        int typeBits = DataBuffer.getDataTypeSize(dataBuffer.getDataType());
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME

        iis.seek(rasterOffset);
        int scan = 0;
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        ((SRFImageInputStream)iis).setDecryptionKey(rasterKey);        

        for (int y = 0; y < height; y++)
          {
            ((SRFImageInputStream)iis).startEncryptedSection(width * 2);

            for (int x = 0; x < width; x++)
              {
                int cfaIndex = (2 * (y & 1)) + (x & 1);
                data[scan + cfaOffsets[cfaIndex]] = iis.readShort();
                scan += pixelStride;
              }

            ir.processImageProgress((100.0f * y) / height);
            ((SRFImageInputStream)iis).stopEncryptedSection();
          }
      }
  }
