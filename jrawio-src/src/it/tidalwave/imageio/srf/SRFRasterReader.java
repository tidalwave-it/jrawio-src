/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: SRFRasterReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
 * @version CVS $Id: SRFRasterReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
