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
 * $Id: RAWImageInputStream.java,v 1.3 2006/02/15 00:00:53 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: RAWImageInputStream.java,v 1.3 2006/02/15 00:00:53 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class RAWImageInputStream extends ImageInputStreamImpl
  {
    private long baseOffset;

    protected ImageInputStream delegate;

    private BitReader bitReader;

    private boolean dontClose;

    /*******************************************************************************
     * 
     * @param delegate
     * 
     *******************************************************************************/
    public RAWImageInputStream (ImageInputStream delegate)
      {
        this.delegate = delegate;
      }

    /*******************************************************************************
     * 
     * @param baseOffset
     * 
     *******************************************************************************/
    public void setBaseOffset (long baseOffset)
      {
        this.baseOffset = baseOffset;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public long getBaseOffset ()
      {
        return baseOffset;
      }

    /*******************************************************************************
     * 
     * @param bitCount
     * @param bufferSize
     * 
     *******************************************************************************/
    public void selectBitReader (int bitCount,
                                 int bufferSize)
      {
        if (bufferSize == 0)
          {
            bufferSize = FastBitReader.DEFAULT_BUFFER_SIZE;
          }

        if (bitCount == 12)
          {
            bitReader = new TwelveBitsReader(delegate, bufferSize);
          }

        else if (bitCount == 16)
          {
            bitReader = new SixteenBitsReader(delegate, bufferSize);
          }

        else
          {
            bitReader = new FastBitReader(delegate, bufferSize);
          }

        //        logger.fine("Using bitReader: " + bitReader.getClass());
      }

    /*******************************************************************************
     * 
     * @param b
     * 
     *******************************************************************************/
    public void setSkipZeroAfterFF (boolean b)
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.setSkipZeroAfterFF(b);
      }

    /*******************************************************************************
     * 
     * Read another string of bits. If the most significant bits of the read string
     * is 1, the 2-complement value is returned. This operation is frequently needed
     * by some RAW formats.
     *
     * @param  bitsToGet    how many bits to read
     * @return              the bits as an integer
     * @throws IOException  if any I/O error occurs
     * 
     *******************************************************************************/
    public final int readComplementedBits (int bitsToGet) throws IOException
      {
        int value = (int)readBits(bitsToGet);

        if ((value & (1 << (bitsToGet - 1))) == 0)
          {
            value -= (1 << bitsToGet) - 1;
          }

        return value;
      }

    /*******************************************************************************
     * 
     * @param bitsToSkip
     * @throws IOException
     * 
     *******************************************************************************/
    public final void skipBits (int bitsToSkip) throws IOException
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.skipBits(bitsToSkip);
      }

    ////////// Decorated methods follow ////////////////////////////////////////////

    public final int readBit () throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(1) : delegate.readBit();
      }

    public final long readBits (int numBits) throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(numBits) : delegate.readBits(numBits);
      }

    public void seek (long pos) throws IOException
      {
        if (bitReader != null)
          {
            bitReader.seek(pos + baseOffset);
          }

        else
          {
            delegate.seek(pos + baseOffset);
          }
      }

    public long getStreamPosition () throws IOException
      {
        return ((bitReader != null) ? bitReader.getStreamPosition() : delegate.getStreamPosition()) - baseOffset;
      }

    public int getBitOffset () throws IOException
      {
        return (bitReader != null) ? bitReader.getBitOffset() : delegate.getBitOffset();
      }

    public void setBitOffset (int bitOffset) throws IOException
      {
        if (bitReader != null)
          {
            bitReader.setBitOffset(bitOffset);
          }

        else
          {
            delegate.setBitOffset(bitOffset);
          }
      }

    ////////// Delegate methods follow /////////////////////////////////////////////

    public int read () throws IOException
      {
        return delegate.read();
      }

    public int read (byte[] b,
                     int off,
                     int len) throws IOException
      {
        return delegate.read(b, off, len);
      }

    public long length ()
      {
        try
          {
            return delegate.length() - baseOffset;
          }
        catch (IOException e)
          {
            return -1;
          }
      }

    public int skipBytes (int n) throws IOException
      {
        return delegate.skipBytes(n); // FIXME: bitReader?
      }

    public long skipBytes (long n) throws IOException
      {
        return delegate.skipBytes(n); // FIXME: bitReader?
      }

    /*
     public void mark ()
     {
     delegate.mark();
     }

     public void reset () throws IOException
     {
     delegate.reset();
     }
     */
    public void flushBefore (long pos) throws IOException
      {
        delegate.flushBefore(pos + baseOffset);
      }

    public void flush () throws IOException
      {
        delegate.flush();
      }

    public long getFlushedPosition ()
      {
        return delegate.getFlushedPosition();
      }

    public boolean isCached ()
      {
        return delegate.isCached();
      }

    public boolean isCachedMemory ()
      {
        return delegate.isCachedMemory();
      }

    public boolean isCachedFile ()
      {
        return delegate.isCachedFile();
      }

    public void close () throws IOException
      {
//        if (!dontClose)
//          {
//            delegate.close();
//          }
      }

    public void setDontClose ()
      {
        dontClose = true;
      }
  }
