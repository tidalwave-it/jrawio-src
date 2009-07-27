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
 * $Id: RAWImageInputStream.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.io;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id: RAWImageInputStream.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class RAWImageInputStream extends ImageInputStreamImpl
  {
    private final static String CLASS = RAWImageInputStream.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnegative
    private long baseOffset;

    @Nonnull
    // Not final: CRWImageInputStream needs to switch it.
    protected ImageInputStream delegate;

    @CheckForNull
    private BitReader bitReader;

    private boolean dontClose;

    /*******************************************************************************************************************
     * 
     * @param delegate
     * 
     ******************************************************************************************************************/
    public RAWImageInputStream (@Nonnull final ImageInputStream delegate)
      {
        this.delegate = delegate;
      }

    /*******************************************************************************************************************
     * 
     * @param baseOffset
     * 
     ******************************************************************************************************************/
    public void setBaseOffset (final long baseOffset)
      {
        this.baseOffset = baseOffset;
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    public long getBaseOffset()
      {
        return baseOffset;
      }

    /*******************************************************************************************************************
     * 
     * @param bitCount
     * @param bufferSize
     * 
     ******************************************************************************************************************/
    public void selectBitReader (@Nonnegative final int bitCount,
                                 @Nonnegative int bufferSize)
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

        logger.finest(">>>> Using bitReader: %s", bitReader);
      }

    /*******************************************************************************************************************
     * 
     * @param b
     * 
     ******************************************************************************************************************/
    public void setSkipZeroAfterFF (final boolean b)
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.setSkipZeroAfterFF(b);
      }

    /*******************************************************************************************************************
     * 
     * Read another string of bits. If the most significant bits of the read string
     * is 1, the 2-complement value is returned. This operation is frequently needed
     * by some RAW formats.
     *
     * @param  bitsToGet    how many bits to read
     * @return              the bits as an integer
     * @throws IOException  if any I/O error occurs
     *
     ***************************************************************************/
    public final int readComplementedBits (@Nonnegative final int bitsToGet) 
      throws IOException
      {
        int value = (int)readBits(bitsToGet);

        if ((value & (1 << (bitsToGet - 1))) == 0)
          {
            value -= (1 << bitsToGet) - 1;
          }

        return value;
      }

    /*******************************************************************************************************************
     * 
     * @param bitsToSkip
     * @throws IOException
     * 
     ******************************************************************************************************************/
    public final void skipBits (@Nonnegative final int bitsToSkip) 
      throws IOException
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.skipBits(bitsToSkip);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    public String toString() 
      {
        return String.format("RAWImageInputStream[%s, %s]", bitReader, delegate);
      }
    
    ////////// Decorated methods follow ////////////////////////////////////////////

    @Override
    public final int readBit () throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(1) : delegate.readBit();
      }

    @Override
    public final long readBits (int numBits) throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(numBits) : delegate.readBits(numBits);
      }

    @Override
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

    @Override
    public long getStreamPosition () throws IOException
      {
        return ((bitReader != null) ? bitReader.getStreamPosition() : delegate.getStreamPosition()) - baseOffset;
      }

    @Override
    public int getBitOffset () throws IOException
      {
        return (bitReader != null) ? bitReader.getBitOffset() : delegate.getBitOffset();
      }

    @Override
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

    public int read() throws IOException
      {
        return delegate.read();
      }

    public int read (byte[] b,
                     int off,
                     int len) throws IOException
      {
        return delegate.read(b, off, len);
      }

    @Override
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

    @Override
    public int skipBytes (int n) throws IOException
      {
        return delegate.skipBytes(n); // FIXME: bitReader?
      }

    @Override
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
    @Override
    public void flushBefore (long pos) throws IOException
      {
        delegate.flushBefore(pos + baseOffset);
      }

    @Override
    public void flush () throws IOException
      {
        delegate.flush();
      }

    @Override
    public long getFlushedPosition ()
      {
        return delegate.getFlushedPosition();
      }

    @Override
    public boolean isCached ()
      {
        return delegate.isCached();
      }

    @Override
    public boolean isCachedMemory ()
      {
        return delegate.isCachedMemory();
      }

    @Override
    public boolean isCachedFile ()
      {
        return delegate.isCachedFile();
      }

    @Override
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
