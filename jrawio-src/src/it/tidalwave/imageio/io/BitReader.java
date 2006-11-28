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
 * $Id: BitReader.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * This is the base class for a number of specialized, high performance bit readers.
 * The class javax.imageio.stream.ImageInputStream is able to read string of
 * bits, but it's surprisingly slow. Subclasses of this class, such as
 * {@link FastBitReader}, {@link TwelveBitsReader} and {@link SixteenBitsReader}, 
 * are much faster.
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: BitReader.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *
 ******************************************************************************/
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

    /*******************************************************************************
     *
     * Read another string of bits. If bitsToGet is zero, nothing is read from the
     * stream and zero is returned.
     *
     * @param  bitsToGet                 how many bits to read
     * @return                           the bits as an integer
     * @throws IOException               if any I/O error occurs
     * @throws IllegalArgumentException  if bitsToGet is negative
     *
     ******************************************************************************/
    public abstract int readBits (int bitsToGet) throws IOException;

    /*******************************************************************************
     *
     * Skip another string of bits.
     *
     * @param  bitsToSkip   how many bits to skip
     * @throws IOException  if any I/O error occurs
     *
     ******************************************************************************/
    public void skipBits (int bitsToSkip) throws IOException
      {
        readBits(bitsToSkip);
      }

    /*******************************************************************************
     * 
     * @return
     * @throws IOException 
     * 
     *******************************************************************************/
    public long getStreamPosition () throws IOException
      {
        return iis.getStreamPosition() - bufferSize + bytePointer;
      }

    /*******************************************************************************
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

    /*******************************************************************************
     * 
     * @param bitPosition
     * 
     *******************************************************************************/
    public void setBitOffset (int bitPosition)
      {
        this.bitPosition = bitPosition;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getBitOffset ()
      {
        return bitPosition;
      }

    /*******************************************************************************
     * 
     * This method should be called whenever a seek() is performed on the associated
     * stream to flush the internal buffer and re-sync.
     * 
     *******************************************************************************/
    protected abstract void resync ();

    /*******************************************************************************
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
