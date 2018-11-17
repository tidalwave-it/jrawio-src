/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
 *
 * Facility class to read bits from an ImageInputStream, it is much faster than ImageInputStream.readBits(). It also
 * supports the behaviour of skipping a byte after a 0xff is encountered, which is a requirement of many RAW formats.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
/* package */class FastBitReader extends BitReader
  {
    private final static int BITS_PER_BYTE = 8;

    /** The default byte buffer size. */
    public final static int DEFAULT_BUFFER_SIZE = 65536;

    /** If true, a zero byte is skipped whenever a FF byte is read. */
    private boolean skipZeroAfterFF;

    /*******************************************************************************************************************
     *
     * Creates a new <code>FastBitReader</code> linking to an existing input stream.
     * A default buffer size of 64k is used.
     * 
     * @param  iis  the input stream
     * 
     ******************************************************************************************************************/
    public FastBitReader (final @Nonnull ImageInputStream iis)
      {
        this(iis, DEFAULT_BUFFER_SIZE);
      }

    /*******************************************************************************************************************
     *
     * Creates a new <code>FastBitReader</code> linking to an existing input stream.
     * This version allows to specify the buffer size to use.
     * 
     * @param  iis         the input stream
     * @param  bufferSize  the bufferSize
     * 
     ******************************************************************************************************************/
    public FastBitReader (final @Nonnull ImageInputStream iis, final @Nonnegative int bufferSize)
      {
        this.iis = iis;
        byteBuffer = new byte[bufferSize];
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public void setSkipZeroAfterFF (final boolean skipZeroAfterFF)
      {
        this.skipZeroAfterFF = skipZeroAfterFF;
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    protected void resync()
      {
        bytePointer = bitPosition = bufferSize = 0;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnegative
    public int readBits (@Nonnegative int bitsToGet)
      throws IOException
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
        final int mask = (int)((1L << bitsToGet) - 1);

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

            final int currentByte = byteBuffer[bytePointer] & 0xff;
            final int shift = (bitPosition + bitsToGet) - BITS_PER_BYTE;
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

        return r & mask;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public String toString()
      {
        return String.format("FastBitReader@%06x", System.identityHashCode(this));
      }
  }
