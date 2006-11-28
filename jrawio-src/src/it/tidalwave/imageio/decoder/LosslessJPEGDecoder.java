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
 * $Id: LosslessJPEGDecoder.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.decoder;

import java.util.logging.Logger;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * This class implements a 16-bit Lossless JPEG decoder. This kind of encoding is
 * widely used by some RAW formats which include blocks of encoded data.
 * 
 * @author  fritz
 * @version CVS $Id: LosslessJPEGDecoder.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class LosslessJPEGDecoder
  {
    private final static String CLASS = "it.tidalwave.imageio.decoder.LosslessJPEGDecoder";

    private final static Logger logger = Logger.getLogger(CLASS);

    private final static int BYTE_MASK = 0xff;

    private final static int SHORT_MASK = 0xffff;

    /** The size of a bit sample. */
    private int bitsPerSample;

    /** The height of the encoded block. */
    private int height;

    /** The width of the encoded block. */
    private int width;

    /** The number of channels. */
    private int channelCount;

    /** The size of a row (it's the width * channelCount) */
    private int rowSize;

    private int[] vPredictors;

    private HuffmannDecoder[] decoders = new HuffmannDecoder[4];

    private short[] rowBuffer;

    /*******************************************************************************
     * 
     * Creates an empty decoder. Prior using, the decoder must be bound to an input
     * stream by means of the {@link #reset(ImageInputStream)} method. The same 
     * instance can be reused more than once by resetting it multiple times. 
     * 
     *******************************************************************************/
    public LosslessJPEGDecoder()
      { 
      }
    
    /*******************************************************************************
     * 
     * Links the decoder to a given input stream. This method also parses the 
     * Lossless JPEG header.
     * 
     * @param   iis          the input stream
     * @throws  IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    public void reset (ImageInputStream iis) throws IOException
      {
        short magic = iis.readShort();

        if (magic != (short)0xffd8)
          {
            throw new RuntimeException("Bad magic: " + Integer.toHexString(magic & SHORT_MASK));
          }

        loop: for (;;)
          {
            short tag = iis.readShort();
            int length = iis.readShort() - 2;

            logger.finer("TAG:" + Integer.toHexString(tag & SHORT_MASK) + " LEN: " + length);

            if (((tag & SHORT_MASK) <= 0xff00) || (length > 255))
              {
                throw new RuntimeException("Bad tag:" + Integer.toHexString(tag & SHORT_MASK));
              }

            switch (tag)
              {
                case (short)0xffc3:
                  bitsPerSample = iis.readByte() & BYTE_MASK;
                  height = iis.readShort() & SHORT_MASK;
                  width = iis.readShort() & SHORT_MASK;
                  channelCount = iis.readByte() & BYTE_MASK;
                  rowSize = width * channelCount;
                  iis.skipBytes(length - 6);
                  rowBuffer = new short[rowSize];
                  vPredictors = new int[channelCount];

                  for (int i = 0; i < channelCount; i++)
                    {
                      vPredictors[i] = 1 << (bitsPerSample - 1);
                    }

                  logger.fine("bitsPerSample: " + bitsPerSample + " height: " + height + " width: " + rowSize
                      + " channelCount: " + channelCount);
                  break;

                case (short)0xffc4:
                  byte[] data = new byte[length];
                  iis.read(data);

                  for (int scan = 0; (scan < length) && (data[scan] < 4);)
                    {
                      int channel = data[scan++];
                      int decoderLen = 16;

                      for (int q = scan; q < scan + 16; q++)
                        {
                          decoderLen += data[q];
                        }

                      byte[] temp = new byte[decoderLen];
                      System.arraycopy(data, scan, temp, 0, temp.length);
                      decoders[channel] = HuffmannDecoder.createDecoderWithJpegHack(temp, 0);
                      logger.fine("Decoder[" + channel + "] = " + decoders[channel]);
                      scan += decoderLen;
                    }

                  break;

                case (short)0xffda:
                  iis.skipBytes(length);
                  break loop;
              }
          }
      }

    /*******************************************************************************
     * 
     * Loads and decodes another row of data from this encoder.
     * 
     * @param  iis           the bit reader to read data from
     * @throws IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    public short[] loadRow (RAWImageInputStream iis) throws IOException
      {
        int scan = 0;

        for (int x = 0; x < width; x++)
          {
            for (int c = 0; c < channelCount; c++)
              {
                int bitCount = decoders[c].decode(iis);
                int diff = iis.readComplementedBits(bitCount);
                rowBuffer[scan] = (short)((x == 0) ? (vPredictors[c] += diff)
                    : (rowBuffer[scan - channelCount] + diff));
                scan++;
              }
          }

        return rowBuffer;
      }
  }
