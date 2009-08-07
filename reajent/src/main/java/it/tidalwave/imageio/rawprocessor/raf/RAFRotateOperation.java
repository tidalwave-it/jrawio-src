/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.raf;

import java.util.Properties;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.raf.FujiRawData;
import it.tidalwave.imageio.raf.FujiTable1;
import it.tidalwave.imageio.raf.RAFMetadata;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.RotateOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFRotateOperation extends RotateOperation
  {
    private static final double SQRT05 = Math.sqrt(0.5);

    @Override
    public void process (final RAWImage image)
      {
//        fuji_width = (fuji_width - 1 + shrink) >> shrink;
        final BufferedImage oldBufferedImage = image.getImage();
        final int originalWidth = oldBufferedImage.getWidth();
        final int originalHeight = oldBufferedImage.getHeight();
        final DataBufferUShort oldDataBuffer = (DataBufferUShort)oldBufferedImage.getData().getDataBuffer();
        final short[] oldData = oldDataBuffer.getData();

        final FujiRawData fujiRawData = ((RAFMetadata)image.getRAWMetadata()).getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        final boolean fujiLayout = fujiTable1.isFujiLayout();
        final int offset = fujiTable1.getWidth() / (fujiLayout ? 1 : 2);

        final int newWidth = (int)(offset / SQRT05);
        final int newHeight = (int)((originalHeight - offset) / SQRT05);

        final int type = DataBuffer.TYPE_USHORT;
        final int[] bandOffsets = { RasterReader.R_OFFSET, RasterReader.G_OFFSET, RasterReader.B_OFFSET };
        final int bandCount = bandOffsets.length;
        final int newPixelStride = bandCount;
        final int newScanlineStride = newPixelStride * newWidth;
        final WritableRaster raster = Raster.createInterleavedRaster(type, newWidth, newHeight, newScanlineStride, newPixelStride, bandOffsets, null);
        final BufferedImage newBufferedImage = new BufferedImage(oldBufferedImage.getColorModel(), raster, false, new Properties());
        final DataBufferUShort newDataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] newData = newDataBuffer.getData();

        final int oldPixelStride = bandCount;
        final int oldScanLineStride = originalWidth * bandCount;

        for (int y = 0; y < newHeight; y++)
          {
            for (int x = 0; x < newWidth; x++)
              {
                final double x0f = (y + x) * SQRT05;
                final double y0f = offset + (y - x) * SQRT05;
                final int x0 = (int)x0f;
                final int y0 = (int)y0f;
                final double fc = x0f - x0;
                final double fr = y0f - y0;

                if ((x0 <= originalWidth - 2) && (y0 <= originalHeight - 2))
                  {
                    final int scan = x0 * oldPixelStride + y0 * oldScanLineStride;
                    final int scan2 = scan + oldScanLineStride;

                    for (int i = 0; i < bandCount; i++)
                      {
                        newData[y * newScanlineStride + x * newPixelStride + i] = (short)
                          ((oldData[scan + i] * (1-fc) + oldData[scan + oldPixelStride + i] * fc) * (1-fr) +
                           (oldData[scan2 + i] * (1-fc) + oldData[scan2 + oldPixelStride + i] * fc) * fr);
                      }
                  }
              }
          }

        image.setImage(newBufferedImage);
      }
  }
