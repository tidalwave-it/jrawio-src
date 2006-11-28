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
 * $Id: TwelveBitsReader.java,v 1.2 2006/02/08 19:54:44 fabriziogiudici Exp $
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
 * @version CVS $Id: TwelveBitsReader.java,v 1.2 2006/02/08 19:54:44 fabriziogiudici Exp $
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
