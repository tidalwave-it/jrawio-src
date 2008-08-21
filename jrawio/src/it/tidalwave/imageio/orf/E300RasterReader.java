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
 * $Id: E300RasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import java.io.IOException;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for E-300.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: E300RasterReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class E300RasterReader extends ORFRasterReader
  {
    protected void loadUncompressedRaster (RAWImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir) throws IOException      
      {
//        logger.fine("loadUncompressedRaster()");
//        logger.finer(">>>> CFA pattern: " + cfaOffsets[0] + " " + cfaOffsets[1] + " " + cfaOffsets[2] + " " + cfaOffsets[3]);

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        setBitsPerSample(12);
        selectBitReader(iis, raster, -1);
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
                int b0 = iis.readByte() & 0xff;
                int b1 = iis.readByte() & 0xff;
                int b2 = iis.readByte() & 0xff;
                
                int sample1 = ((b1 << 8) | b0) & 0xfff;
                int sample2 = ((b2 << 4) | (b1 >>> 4)) & 0xfff;
                
                if (linearizationTable != null)
                  {
                    sample1 = linearizationTable[sample1];
                    sample2 = linearizationTable[sample2];
                  }

                int j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample1;
                x++;
                i += pixelStride;
                j = x % 2;
                data[i + cfaOffsets[j + k]] = (short)sample2;
                i += pixelStride;
                
                if (((x+1) % 10) == 0)
                  {
                    iis.readByte();  
                  }
              }

            ir.processImageProgress((100.0f * y) / height);
          }
      }
  }
