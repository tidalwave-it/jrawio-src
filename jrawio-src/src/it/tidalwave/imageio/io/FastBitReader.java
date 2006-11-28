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
 * $Id: FastBitReader.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * Facility class to read bits from an ImageInputStream, it is much faster
 * than ImageInputStream.readBits(). It also supports the behaviour of skipping
 * a byte after a 0xff is encountered, which is a requirement of many RAW formats.
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: FastBitReader.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *
 ******************************************************************************/

/* package */class FastBitReader extends BitReader
  {
    private final static int BITS_PER_BYTE = 8;

    /** The default byte buffer size. */
    public final static int DEFAULT_BUFFER_SIZE = 65536;

    /** If true, a zero byte is skipped whenever a FF byte is read. */
    private boolean skipZeroAfterFF;

    /*******************************************************************************
     *
     * Creates a new <code>FastBitReader</code> linking to an existing input stream.
     * A default buffer size of 64k is used.
     * 
     * @param  iis  the input stream
     * 
     ******************************************************************************/
    public FastBitReader (ImageInputStream iis)
      {
        this(iis, DEFAULT_BUFFER_SIZE);
      }

    /*******************************************************************************
     *
     * Creates a new <code>FastBitReader</code> linking to an existing input stream.
     * This version allows to specify the buffer size to use.
     * 
     * @param  iis         the input stream
     * @param  bufferSize  the bufferSize
     * 
     ******************************************************************************/
    public FastBitReader (ImageInputStream iis, int bufferSize)
      {
        this.iis = iis;
        byteBuffer = new byte[bufferSize];
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void setSkipZeroAfterFF (boolean skipZeroAfterFF)
      {
        this.skipZeroAfterFF = skipZeroAfterFF;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void resync ()
      {
        bytePointer = bitPosition = bufferSize = 0;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public int readBits (int bitsToGet) throws IOException
      {
        if (bitsToGet < 0)
          {
            throw new IllegalArgumentException("bitsToGet: " + bitsToGet);
          }

        if (bitsToGet == 0) // ImageInputStream behaviour
          {
            return 0;
          }

        int r = 0;
        int mask = (int)((1L << bitsToGet) - 1);

        while (bitsToGet > 0)
          {
            int bitsToGetNow = BITS_PER_BYTE - bitPosition;

            if (bitsToGetNow > bitsToGet)
              {
                bitsToGetNow = bitsToGet;
              }

            if (bytePointer >= bufferSize)
              {
                bufferSize = iis.read(byteBuffer);
                // FIXME: if bufferSize == 0 throw EOF
                bytePointer = 0;
              }

            int currentByte = byteBuffer[bytePointer] & 0xff;
            int shift = (bitPosition + bitsToGet) - BITS_PER_BYTE;
            r |= ((shift >= 0) ? (currentByte << shift) : (currentByte >>> (-shift)));
            bitsToGet -= bitsToGetNow;
            bitPosition += bitsToGetNow;

            if (bitPosition >= BITS_PER_BYTE)
              {
                bitPosition -= BITS_PER_BYTE;
                bytePointer++;

                if (skipZeroAfterFF && (currentByte == 0xff))
                  {
                    if (bytePointer >= bufferSize)
                      {
                        byte b = iis.readByte();
                        //assert b == 0;
                      }

                    else
                      {
                        //assert byteBuffer[bytePointer] == 0;
                        bytePointer++;
                      }
                  }
              }
          }

        return r &= mask;
      }
  }
