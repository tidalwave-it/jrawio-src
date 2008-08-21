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
 * $Id: SixteenBitsReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * Facility class to read strings of 16 bits from an ImageInputStream, it is 
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
 * @version $Id: SixteenBitsReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
/* package */class SixteenBitsReader extends BitReader
  {
    /*******************************************************************************
     *
     * Creates a new <code>SixteenBitsReader</code> linking to an existing input stream.
     * This version allows to specify the buffer size to use.
     * 
     * @param  iis         the input stream
     * @param  bufferSize  the bufferSize
     * 
     * 
     ******************************************************************************/
    public SixteenBitsReader (ImageInputStream iis, int bufferSize)
      {
        this.iis = iis;
        byteBuffer = new byte[bufferSize];
        bytePointer = byteBuffer.length;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public void resync ()
      {
        bytePointer = byteBuffer.length; // force a reload next time
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     * This specialized version can be used to read only 16 bits at a time.
     * 
     * @throws IllegalArgumentException  if bitsToGet is not equal to 16
     * 
     ******************************************************************************/
    public int readBits (int bitsToGet) throws IOException
      {
        if (bitsToGet != 16)
          {
            throw new IllegalArgumentException("SixteenBitsReader only reads 16 bits");
          }

        if (bytePointer >= bufferSize)
          {
            bytePointer = 0;
            bufferSize = iis.read(byteBuffer);
          }

        int b1 = byteBuffer[bytePointer++] & 0xff;
        int b0 = byteBuffer[bytePointer++] & 0xff;

        return (b0 << 8) | (b1 & 0xff);
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
            throw new IllegalArgumentException("SixteenBitsReader.skipBits(): only multiple of 8");
          }

        int bytes = bitsToSkip / 8;
        bytePointer += bytes;
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

    /*******************************************************************************
     * 
     * @param bitPosition
     * 
     *******************************************************************************/
    public void setBitOffset (int bitPosition)
      {
        if (bitPosition != 0)
          {
            throw new UnsupportedOperationException(); // FIXME
          }
      }
  }
