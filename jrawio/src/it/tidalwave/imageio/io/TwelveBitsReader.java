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
 * $Id: TwelveBitsReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * Facility class to read strings of 12 bits from an ImageInputStream, it is 
 * much faster than ImageInputStream.readBits(). 
 *
 * Warning: on speed purposes, this class doesn't perform any check about:
 *
 * - reading past the end of file;
 * - skipToBits() presumes that bytePointer stays within the buffer.
 *
 * So, the bufferSize MUST be a multiple of the scan line size in bytes, or
 * an ArrayIndexOutOfBounds will occur.
 *
 * @author Fabrizio Giudici
 * @version CVS $Id: TwelveBitsReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
/* package */class TwelveBitsReader extends BitReader
  {
    private int[] buffer;

    private int bufferPointer;

    /*******************************************************************************
     *
     * Creates a new <code>TwelveBitsReader</code> linking to an existing input stream.
     * This version allows to specify the buffer size to use.
     * 
     * @param  iis         the input stream
     * @param  bufferSize  the bufferSize
     * 
     * 
     ******************************************************************************/
    public TwelveBitsReader (ImageInputStream iis, int bufferSize)
      {
        this.iis = iis;
        buffer = new int[2];
        byteBuffer = new byte[bufferSize];
        bufferPointer = buffer.length;
        bytePointer = byteBuffer.length;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public void resync ()
      {
        bufferPointer = buffer.length; // force a reload next time
        bytePointer = buffer.length;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     * This specialized version can be used to read only 12 bits at a time.
     * 
     * @throws IllegalArgumentException  if bitsToGet is not equal to 12
     * 
     ******************************************************************************/
    public int readBits (int bitsToGet) throws IOException
      {
        if (bitsToGet != 12)
          {
            throw new IllegalArgumentException("TwelveBitReader only reads 12 bits");
          }

        if (bufferPointer >= buffer.length)
          {
            bufferPointer = 0;

            if (bytePointer >= byteBuffer.length)
              {
                bytePointer = 0;
                iis.readFully(byteBuffer);
              }

            int b0 = byteBuffer[bytePointer++] & 0xff;
            int b1 = byteBuffer[bytePointer++] & 0xff;
            int b2 = byteBuffer[bytePointer++] & 0xff;
            buffer[0] = (b0 << 4) | (b1 >>> 4);
            buffer[1] = ((b1 & 0xf) << 8) | b2;
          }

        bitPosition = (bufferPointer == 0) ? 4 : 0;
        
        return buffer[bufferPointer++];
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     * This specialized version can be used to skip only multiple of 8 bits at a time.
     * 
     * @throws IllegalArgumentException  if bitsToGet is not a multiple of 8
     * 
     ******************************************************************************/
    public void skipBits (int bitsToSkip)
      {
        if ((bitsToSkip % 8) != 0)
          {
            throw new IllegalArgumentException("TwelveBitReader.skipBits(): only multiple of 8");
          }

        int bytes = bitsToSkip / 8;
        bytePointer += bytes;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void setBitOffset (int bitPosition)
      {
        if ((bitPosition != 0) && (bitPosition != 4))
          {
            throw new IllegalArgumentException("bitPosition can be only 0 or 4");
          }

        throw new UnsupportedOperationException(); // FIXME
//        this.bitPosition = bitPosition;
      }


    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public long getStreamPosition () throws IOException
      {
        long p = iis.getStreamPosition() - bufferSize + bytePointer + 1;

        if (bufferPointer == 1)
          {
            p++;
          }

        return p;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void setSkipZeroAfterFF (boolean skipZeroAfterFF)
      {
        throw new UnsupportedOperationException(); // FIXME
      }
  }
