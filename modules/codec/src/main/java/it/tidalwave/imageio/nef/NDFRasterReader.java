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
package it.tidalwave.imageio.nef;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NDFRasterReader extends NEFRasterReader
  {
    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void loadUncompressedRaster (ImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir,
                                           int whiteLevel) 
      throws IOException
      {
        /*
         //logger.fine("loadUncompressedImage()");
         DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
         short[] data = dataBuffer.getData();
         int typeBits = DataBuffer.getDataTypeSize(dataBuffer.getDataType());
         int width = raster.getWidth();
         int height = raster.getHeight();
         BitReader fbr = createBitReader(iis, ifd, raster, bitsPerSample);
         int shift = (typeBits - bitsPerSample);

         while (whiteLevel < ((1 << bitsPerSample) - 1)) // assumes whiteLevel is 2**n - 1
         {
         shift++;
         whiteLevel = (whiteLevel << 1) | 1;
         }

         int mask = (1 << shift) - 1;
         int pixelStride = 3;
         int scanStride = width * pixelStride;
         //
         // We can rely on the fact that the array has been zeroed by the JVM,
         // so we just set nonzero samples.
         //
         for (int y = 0; y < height; y++)
         {
         int row = getRow(y, height);
         int i = row * scanStride;
         int k = (row % 2) * 2;

         for (int x = 0; x < width; x++)
         {
         fbr.skipBits(getSkipCountAtColumn(x));
         int sample = fbr.readBits(bitsPerSample);
         sample = (sample << shift) | ((sample >> (bitsPerSample - shift)) & mask);
         data[i] = (short)sample;
         i += pixelStride;
         }

         fbr.skipBits(getSkipCountAtEndOfRow());

         if (ir != null)
         {
         ir.processImageProgress((100.0f * y) / height);
         }
         }
         */
      }
  }
