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
 * $Id: DNGRasterReader.java,v 1.2 2006/02/08 19:52:01 fabriziogiudici Exp $
 *  
 ******************************************************************************/

package it.tidalwave.imageio.dng;

import java.util.logging.Logger;
import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.decoder.LosslessJPEGDecoder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: DNGRasterReader.java,v 1.2 2006/02/08 19:52:01 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DNGRasterReader extends RasterReader
  {
    private static final int BUFFER_SIZE = 128 * 1024;

    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.dng.DNGRasterReader");

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected boolean isCompressedRaster ()
      {
        return compression == IFD.Compression.JPEG.intValue();
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void loadCompressedRaster (RAWImageInputStream iis,
                                         WritableRaster raster,
                                         RAWImageReaderSupport ir) throws IOException
      {
        logger.finest(">>>> tileSize:    " + tileWidth + " x " + tileHeight);
        logger.finest(">>>> tileCount:   " + tilesAcross + " x " + tilesDown);
        ByteOrder byteOrderSaved = iis.getByteOrder();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        int tileIndex = 0;

        LosslessJPEGDecoder jpegDecoder = new LosslessJPEGDecoder();
        iis.selectBitReader(-1, 0); 
        iis.setSkipZeroAfterFF(true); 

        for (int tileRow = 0; tileRow < tilesDown; tileRow++)
          {
            for (int tileColumn = 0; tileColumn < tilesAcross; tileColumn++)
              {
                iis.seek(tileOffsets[tileIndex]);
                jpegDecoder.reset(iis);

                for (int tileY = 0; tileY < tileHeight; tileY++)
                  {
                    short[] rowBuffer = jpegDecoder.loadRow(iis);
                    int y = tileRow * tileHeight + tileY;

                    if (y >= height)
                      {
                        break;
                      }

                    for (int tileX = 0; tileX < tileWidth; tileX++)
                      {
                        int x = tileColumn * tileWidth + tileX;

                        if (x >= width)
                          {
                            break;
                          }

                        short value = rowBuffer[tileX];

                        if (linearizationTable != null)
                          {
                            value = (short)linearizationTable[value & 0xFFFF];
                          }

                        int cfaIndex = (y % 2) * 2 + (x % 2);
                        data[y * scanStride + x * pixelStride + cfaOffsets[cfaIndex]] = value;
                      }
                  }

                tileIndex++;
                ir.processImageProgress((100f * tileIndex) / (tilesDown * tilesAcross));
              }
          }

        iis.setByteOrder(byteOrderSaved);
      }
  }
