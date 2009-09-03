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
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
 *
 * This is the base class for a number of specialized, high performance bit readers.
 * The class javax.imageio.stream.ImageInputStream is able to read string of
 * bits, but it's surprisingly slow. Subclasses of this class, such as
 * {@link FastBitReader}, {@link TwelveBitsReader} and {@link SixteenBitsReader}, 
 * are much faster.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
/* package */abstract class BitReader
  {
    /** The linked input stream. */
    protected ImageInputStream iis;

    /** The byte buffer. */
    protected byte[] byteBuffer;

    /** The buffer size (how many valid bytes are inside). */
    protected int bufferSize;

    /** The pointer to the current byte in the buffer. */
    protected int bytePointer;

    /** The pointer to the next unread bit in the current byte.*/
    protected int bitPosition;

    /*******************************************************************************************************************
     *
     * Read another string of bits. If bitsToGet is zero, nothing is read from the
     * stream and zero is returned.
     *
     * @param  bitsToGet                 how many bits to read
     * @return                           the bits as an integer
     * @throws IOException               if any I/O error occurs
     * @throws IllegalArgumentException  if bitsToGet is negative
     *
     ******************************************************************************************************************/
    public abstract int readBits (int bitsToGet) throws IOException;

    /*******************************************************************************************************************
     *
     * Skip another string of bits.
     *
     * @param  bitsToSkip   how many bits to skip
     * @throws IOException  if any I/O error occurs
     *
     ******************************************************************************************************************/
    public void skipBits (int bitsToSkip) throws IOException
      {
        readBits(bitsToSkip);
      }

    /*******************************************************************************************************************
     * 
     * @return
     * @throws IOException 
     * 
     *******************************************************************************/
    public long getStreamPosition () throws IOException
      {
        return iis.getStreamPosition() - bufferSize + bytePointer;
      }

    /*******************************************************************************************************************
     * 
     * @param position
     * @throws IOException
     * 
     *******************************************************************************/
    public void seek (long position) throws IOException
      {
        resync();
        iis.seek(position);
      }

    /*******************************************************************************************************************
     * 
     * @param bitPosition
     * 
     *******************************************************************************/
    public void setBitOffset (int bitPosition)
      {
        this.bitPosition = bitPosition;
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getBitOffset ()
      {
        return bitPosition;
      }

    /*******************************************************************************************************************
     * 
     * This method should be called whenever a seek() is performed on the associated
     * stream to flush the internal buffer and re-sync.
     * 
     *******************************************************************************/
    protected abstract void resync ();

    /*******************************************************************************************************************
     * 
     * If this property is set to true, a byte is skipped whenever a 0xFF byte is
     * read. This is a requirement of many RAW formats - usually the skipped byte is
     * zero.
     * 
     * @param skipZeroAfterFF
     * 
     *******************************************************************************/
    public abstract void setSkipZeroAfterFF (boolean skipZeroAfterFF);
  }
