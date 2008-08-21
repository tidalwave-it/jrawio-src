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
 * $Id: CRWRasterReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.crw;

import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.decoder.HuffmannDecoder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;

/*******************************************************************************
 *
 * This class loads raster data for Canon cameras.
 * 
 * Thanks to Dave Coffin for providing through his dcraw information about the
 * compression scheme.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: CRWRasterReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class CRWRasterReader extends RasterReader
  {
    private final static String CLASS = "it.tidalwave.imageio.crw.CRWRasterReader";

    private final static Logger logger = Logger.getLogger(CLASS);

    /** The size of the CRW header. */
    private final static int HEADER_SIZE = 26; // FIXME

    /** The width of a pixel block. */
    private final static int BLOCK_WIDTH = 8;

    /** The height of a pixel block. */
    private final static int BLOCK_HEIGHT = 8;

    private int rasterOffset = -1;

    private int decoderPairIndex = -1;

    /** Decompression decoders. */
    private final static HuffmannDecoder[][] canonDecoders;

    private final static short[][] firstDecoderConfiguration = {
      {
        0, 1, 4, 2, 3, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x04, 0x03, 0x05, 0x06, 0x02, 0x07, 0x01, 0x08, 0x09, 0x00,
        0x0a, 0x0b, 0xff },

      {
        0, 2, 2, 3, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0x03, 0x02, 0x04, 0x01, 0x05, 0x00, 0x06, 0x07, 0x09, 0x08,
        0x0a, 0x0b, 0xff },

      {
        0, 0, 6, 3, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x06, 0x05, 0x07, 0x04, 0x08, 0x03, 0x09, 0x02, 0x00, 0x0a,
        0x01, 0x0b, 0xff }, };

    private final static short[][] secondDecoderConfiguration = {
      {
        0, 2, 2, 2, 1, 4, 2, 1, 2, 5, 1, 1, 0, 0, 0, 139, 0x03, 0x04, 0x02, 0x05, 0x01, 0x06, 0x07, 0x08, 0x12, 0x13,
        0x11, 0x14, 0x09, 0x15, 0x22, 0x00, 0x21, 0x16, 0x0a, 0xf0, 0x23, 0x17, 0x24, 0x31, 0x32, 0x18, 0x19, 0x33,
        0x25, 0x41, 0x34, 0x42, 0x35, 0x51, 0x36, 0x37, 0x38, 0x29, 0x79, 0x26, 0x1a, 0x39, 0x56, 0x57, 0x28, 0x27,
        0x52, 0x55, 0x58, 0x43, 0x76, 0x59, 0x77, 0x54, 0x61, 0xf9, 0x71, 0x78, 0x75, 0x96, 0x97, 0x49, 0xb7, 0x53,
        0xd7, 0x74, 0xb6, 0x98, 0x47, 0x48, 0x95, 0x69, 0x99, 0x91, 0xfa, 0xb8, 0x68, 0xb5, 0xb9, 0xd6, 0xf7, 0xd8,
        0x67, 0x46, 0x45, 0x94, 0x89, 0xf8, 0x81, 0xd5, 0xf6, 0xb4, 0x88, 0xb1, 0x2a, 0x44, 0x72, 0xd9, 0x87, 0x66,
        0xd4, 0xf5, 0x3a, 0xa7, 0x73, 0xa9, 0xa8, 0x86, 0x62, 0xc7, 0x65, 0xc8, 0xc9, 0xa1, 0xf4, 0xd1, 0xe9, 0x5a,
        0x92, 0x85, 0xa6, 0xe7, 0x93, 0xe8, 0xc1, 0xc6, 0x7a, 0x64, 0xe1, 0x4a, 0x6a, 0xe6, 0xb3, 0xf1, 0xd3, 0xa5,
        0x8a, 0xb2, 0x9a, 0xba, 0x84, 0xa4, 0x63, 0xe5, 0xc5, 0xf3, 0xd2, 0xc4, 0x82, 0xaa, 0xda, 0xe4, 0xf2, 0xca,
        0x83, 0xa3, 0xa2, 0xc3, 0xea, 0xc2, 0xe2, 0xe3, 0xff, 0xff },

      {
        0, 2, 2, 1, 4, 1, 4, 1, 3, 3, 1, 0, 0, 0, 0, 140, 0x02, 0x03, 0x01, 0x04, 0x05, 0x12, 0x11, 0x06, 0x13, 0x07,
        0x08, 0x14, 0x22, 0x09, 0x21, 0x00, 0x23, 0x15, 0x31, 0x32, 0x0a, 0x16, 0xf0, 0x24, 0x33, 0x41, 0x42, 0x19,
        0x17, 0x25, 0x18, 0x51, 0x34, 0x43, 0x52, 0x29, 0x35, 0x61, 0x39, 0x71, 0x62, 0x36, 0x53, 0x26, 0x38, 0x1a,
        0x37, 0x81, 0x27, 0x91, 0x79, 0x55, 0x45, 0x28, 0x72, 0x59, 0xa1, 0xb1, 0x44, 0x69, 0x54, 0x58, 0xd1, 0xfa,
        0x57, 0xe1, 0xf1, 0xb9, 0x49, 0x47, 0x63, 0x6a, 0xf9, 0x56, 0x46, 0xa8, 0x2a, 0x4a, 0x78, 0x99, 0x3a, 0x75,
        0x74, 0x86, 0x65, 0xc1, 0x76, 0xb6, 0x96, 0xd6, 0x89, 0x85, 0xc9, 0xf5, 0x95, 0xb4, 0xc7, 0xf7, 0x8a, 0x97,
        0xb8, 0x73, 0xb7, 0xd8, 0xd9, 0x87, 0xa7, 0x7a, 0x48, 0x82, 0x84, 0xea, 0xf4, 0xa6, 0xc5, 0x5a, 0x94, 0xa4,
        0xc6, 0x92, 0xc3, 0x68, 0xb5, 0xc8, 0xe4, 0xe5, 0xe6, 0xe9, 0xa2, 0xa3, 0xe3, 0xc2, 0x66, 0x67, 0x93, 0xaa,
        0xd4, 0xd5, 0xe7, 0xf8, 0x88, 0x9a, 0xd7, 0x77, 0xc4, 0x64, 0xe2, 0x98, 0xa5, 0xca, 0xda, 0xe8, 0xf3, 0xf6,
        0xa9, 0xb2, 0xb3, 0xf2, 0xd2, 0x83, 0xba, 0xd3, 0xff, 0xff },

      {
        0, 0, 6, 2, 1, 3, 3, 2, 5, 1, 2, 2, 8, 10, 0, 117, 0x04, 0x05, 0x03, 0x06, 0x02, 0x07, 0x01, 0x08, 0x09, 0x12,
        0x13, 0x14, 0x11, 0x15, 0x0a, 0x16, 0x17, 0xf0, 0x00, 0x22, 0x21, 0x18, 0x23, 0x19, 0x24, 0x32, 0x31, 0x25,
        0x33, 0x38, 0x37, 0x34, 0x35, 0x36, 0x39, 0x79, 0x57, 0x58, 0x59, 0x28, 0x56, 0x78, 0x27, 0x41, 0x29, 0x77,
        0x26, 0x42, 0x76, 0x99, 0x1a, 0x55, 0x98, 0x97, 0xf9, 0x48, 0x54, 0x96, 0x89, 0x47, 0xb7, 0x49, 0xfa, 0x75,
        0x68, 0xb6, 0x67, 0x69, 0xb9, 0xb8, 0xd8, 0x52, 0xd7, 0x88, 0xb5, 0x74, 0x51, 0x46, 0xd9, 0xf8, 0x3a, 0xd6,
        0x87, 0x45, 0x7a, 0x95, 0xd5, 0xf6, 0x86, 0xb4, 0xa9, 0x94, 0x53, 0x2a, 0xa8, 0x43, 0xf5, 0xf7, 0xd4, 0x66,
        0xa7, 0x5a, 0x44, 0x8a, 0xc9, 0xe8, 0xc8, 0xe7, 0x9a, 0x6a, 0x73, 0x4a, 0x61, 0xc7, 0xf4, 0xc6, 0x65, 0xe9,
        0x72, 0xe6, 0x71, 0x91, 0x93, 0xa6, 0xda, 0x92, 0x85, 0x62, 0xf3, 0xc5, 0xb2, 0xa4, 0x84, 0xba, 0x64, 0xa5,
        0xb3, 0xd2, 0x81, 0xe5, 0xd3, 0xaa, 0xc4, 0xca, 0xf2, 0xb1, 0xe4, 0xd1, 0x83, 0x63, 0xea, 0xc3, 0xe2, 0x82,
        0xf1, 0xa3, 0xc2, 0xa1, 0xc1, 0xe3, 0xa2, 0xe1, 0xff, 0xff } };

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    static
      {
        canonDecoders = new HuffmannDecoder[firstDecoderConfiguration.length][];

        for (int i = 0; i < canonDecoders.length; i++)
          {
            canonDecoders[i] = new HuffmannDecoder[2];
            canonDecoders[i][0] = HuffmannDecoder.createDecoder(firstDecoderConfiguration[i]);
            canonDecoders[i][1] = HuffmannDecoder.createDecoder(secondDecoderConfiguration[i]);
          }
      }

    /*******************************************************************************
     * 
     * @param rasterOffset
     * 
     *******************************************************************************/
    public void setRasterOffset (int rasterOffset) // makerNote.getDecoderTable()[2];
      {
        this.rasterOffset = rasterOffset;
      }

    /*******************************************************************************
     * 
     * @param decoderPairIndex
     * 
     *******************************************************************************/
    public void setDecoderPairIndex (int decoderPairIndex) // makerNote.getDecoderTable()[0]; // FIXME
      {
        this.decoderPairIndex = decoderPairIndex;
      }

    /*******************************************************************************
     * 
     * Returns the most appropriate RasterReader for the given Canon model.
     * 
     * @param model		the camera model
     * @return			the RasterReader
     * 
     *******************************************************************************/
    public static CRWRasterReader getInstance (String model)
      {
        if (!model.startsWith("Canon EOS") && !model.startsWith("Canon PowerShot"))
          {
            throw new IllegalArgumentException("Unsupported model: " + model);
          }

        return new CRWRasterReader();
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * CRW files are always compressed.
     * 
     *******************************************************************************/
    protected boolean isCompressedRaster ()
      {
        return true;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     * This method implements raster data loading for compressed CRW.
     *
     ******************************************************************************/
    protected void loadCompressedRaster (RAWImageInputStream iis,
                                         WritableRaster raster,
                                         RAWImageReaderSupport ir) throws IOException
      {
        assert rasterOffset >= 0 : "rasterOffset not set";
        assert decoderPairIndex >= 0 : "decoderPairIndex not set";

        // Warning, this relies upon the fact that RGB offsets share the same value as TIFF CFA_PATTERN.
        // FIXME: decouple them
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME dataBuffer.getNumBanks();
        int scanStride = width * pixelStride;

        iis.selectBitReader(-1, 0);
        int lowBitsOffset = HEADER_SIZE + rasterOffset;
        int rawOffset = lowBitsOffset;
        boolean hasLowBits = hasLowBits(iis, rawOffset);

        if (hasLowBits)
          {
            rawOffset += height * width / 4;
            iis.setSkipZeroAfterFF(true);
          }

        iis.seek(rawOffset);
        HuffmannDecoder[] decoderPair = canonDecoders[decoderPairIndex];
        logger.finest(">>>> decoderTable: " + decoderPairIndex + ", lowBitsOffset: " + lowBitsOffset + ", rawOffset: "
            + rawOffset);
        logger.finest("firstDecoder: " + decoderPair[0]);
        logger.finest("secondDecoder: " + decoderPair[1]);

        int[] diffBlock = new int[BLOCK_WIDTH * BLOCK_HEIGHT];
        short[] pixel = new short[width * BLOCK_HEIGHT];
        int[] base = new int[2];

        int carry = 0;
        int pixelCounter = 0;

        for (int y = 0; y < height; y += BLOCK_HEIGHT)
          {
            for (int block = 0; block < width / BLOCK_WIDTH; block++)
              {
                loadBlock(iis, diffBlock, decoderPair);
                diffBlock[0] += carry;
                carry = diffBlock[0];

                for (int i = 0; i < diffBlock.length; i++)
                  {
                    if ((pixelCounter++ % width) == 0)
                      {
                        base[0] = base[1] = 512;
                      }

                    pixel[(block * diffBlock.length) + i] = (short)(base[i & 1] += diffBlock[i]);
                  }
              }

            if (hasLowBits)
              {
                loadLowBits(iis, width, pixel, y);
              }

            for (int y2 = y; y2 < Math.min(y + BLOCK_HEIGHT, height); y2++)
              {
                int i = y2 * scanStride;

                for (int x = 0; x < width; x++)
                  {
                    int cfaIndex = (2 * (y2 & 1)) + (x & 1);
                    data[i + cfaOffsets[cfaIndex]] = pixel[(y2 - y) * width + x];
                    i += pixelStride;
                  }
              }

            ir.processImageProgress((100.0f * y) / height);
          }
      }

    /*******************************************************************************
     * 
     * @param iis
     * @param diffBlock
     * @param decoderPair
     * @throws IOException
     * 
     *******************************************************************************/
    private void loadBlock (RAWImageInputStream iis,
                            int[] diffBlock,
                            HuffmannDecoder[] decoderPair) throws IOException
      {
        for (int i = 0; i < diffBlock.length; i++)
          {
            diffBlock[i] = 0;
          }

        for (int i = 0; i < diffBlock.length; i++)
          {
            int value = decoderPair[(i == 0) ? 0 : 1].decode(iis);

            if ((value == 0) && (i != 0))
              {
                break;
              }

            if (value != 0xff)
              {
                i += value >> 4;
                int length = value & 15;

                if (length > 0)
                  {
                    int diff = iis.readComplementedBits(length);

                    if (i < diffBlock.length)
                      {
                        diffBlock[i] = diff;
                      }
                  }
              }
          }
      }

    /*******************************************************************************
     * 
     * @param iis
     * @param width
     * @param pixel
     * @param row
     * @throws IOException
     * 
     *******************************************************************************/
    private void loadLowBits (ImageInputStream iis,
                              int width,
                              short[] pixel,
                              int row) throws IOException
      {
        iis.mark();
        iis.seek(HEADER_SIZE + row * width / 4); // FIXME: shouldn't it be lowBitsOffset?

        for (int p = 0, i = 0; i < width * 2; i++)
          {
            byte c = iis.readByte();

            for (int r = 0; r < 8; r += 2, p++)
              {
                int val = (pixel[p] << 2) + ((c >> r) & 3);

                if (width == 2672 && val < 512) // FIXME
                  {
                    val += 2;
                  }

                pixel[p] = (short)val;
              }
          }

        iis.reset();
      }

    /*******************************************************************************
     * 
     * Return true if the image starts with compressed data. In Canon compressed 
     * data, 0xff is always followed by 0x00.
     * 
     * @param  iis          the image input stream
     * @param  offset       the offset to read at
     * @return              true if the image starts with compressed data 
     * @throws IOException
     * 
     *******************************************************************************/
    private static boolean hasLowBits (ImageInputStream iis,
                                       int offset) throws IOException
      {
        byte[] buffer = new byte[16 * 1024];
        boolean compressed = true;

        iis.seek(0);
        iis.read(buffer);

        for (int i = offset; i < buffer.length - 1; i++)
          {
            if (buffer[i] == 0xff)
              {
                if (buffer[i + 1] != 0)
                  {
                    return true;
                  }

                compressed = false;
              }
          }

        return compressed;
      }
  }
