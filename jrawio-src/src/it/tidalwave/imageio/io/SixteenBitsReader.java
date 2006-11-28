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
 * $Id: SixteenBitsReader.java,v 1.2 2006/02/08 19:54:44 fabriziogiudici Exp $
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
 * @version CVS $Id: SixteenBitsReader.java,v 1.2 2006/02/08 19:54:44 fabriziogiudici Exp $
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
