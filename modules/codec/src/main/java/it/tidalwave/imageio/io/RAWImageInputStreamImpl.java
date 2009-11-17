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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAWImageInputStreamImpl extends ImageInputStreamImpl implements RAWImageInputStream
  {
    private final static String CLASS = RAWImageInputStreamImpl.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnegative
    private long baseOffset;

    @Nonnull
    // Not final: CRWImageInputStream needs to switch it.
    protected ImageInputStream delegate;

    @CheckForNull
    private BitReader bitReader;

    private boolean dontCloseDelegate;

    /*******************************************************************************************************************
     *
     * Create a new instance of this class with a given delegate.
     *
     * @param delegate  delegate
     * 
     ******************************************************************************************************************/
    public RAWImageInputStreamImpl (@Nonnull final ImageInputStream delegate)
      {
        this.delegate = delegate;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public void setBaseOffset (final @Nonnegative long baseOffset)
      {
        this.baseOffset = baseOffset;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnegative 
    public long getBaseOffset()
      {
        return baseOffset;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
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
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public void setSkipZeroAfterFF (final boolean skipZeroAfterFF)
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.setSkipZeroAfterFF(skipZeroAfterFF);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public final void skipBits (@Nonnegative final int bitCount)
      throws IOException
      {
        assert (bitReader != null) : "null bitReader";
        bitReader.skipBits(bitCount);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void setDontCloseDelegate()
      {
        dontCloseDelegate = true;
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
    public final int readBit()
      throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(1) : delegate.readBit();
      }

    @Override
    public final long readBits (final int bitCount)
      throws IOException
      {
        return (bitReader != null) ? bitReader.readBits(bitCount) : delegate.readBits(bitCount);
      }

    @Override
    public void seek (final long position)
      throws IOException
      {
        if (bitReader != null)
          {
            bitReader.seek(position + baseOffset);
          }

        else
          {
            delegate.seek(position + baseOffset);
          }
      }

    @Override
    public long getStreamPosition()
      throws IOException
      {
        return ((bitReader != null) ? bitReader.getStreamPosition() : delegate.getStreamPosition()) - baseOffset;
      }

    @Override
    public int getBitOffset()
      throws IOException
      {
        return (bitReader != null) ? bitReader.getBitOffset() : delegate.getBitOffset();
      }

    @Override
    public void setBitOffset (final int bitOffset)
      throws IOException
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

    public int read()
      throws IOException
      {
        return delegate.read();
      }

    public int read (final byte[] b, final int off, final int len)
      throws IOException
      {
        return delegate.read(b, off, len);
      }

    @Override
    @Nonnegative
    public long length()
      {
        try
          {
            return delegate.length() - baseOffset;
          }
        catch (IOException e)
          {
            return -1; // FIXME: why don't just rethrow?
          }
      }

    @Override
    public int skipBytes (final @Nonnegative int byteCount)
      throws IOException
      {
        return delegate.skipBytes(byteCount); // FIXME: bitReader?
      }

    @Override
    public long skipBytes (final @Nonnegative long byteCount)
      throws IOException
      {
        return delegate.skipBytes(byteCount); // FIXME: bitReader?
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
    public void flushBefore (final long position)
      throws IOException
      {
        delegate.flushBefore(position + baseOffset);
      }

    @Override
    public void flush()
      throws IOException
      {
        delegate.flush();
      }

    @Override
    public long getFlushedPosition()
      {
        return delegate.getFlushedPosition();
      }

    @Override
    public boolean isCached()
      {
        return delegate.isCached();
      }

    @Override
    public boolean isCachedMemory()
      {
        return delegate.isCachedMemory();
      }

    @Override
    public boolean isCachedFile()
      {
        return delegate.isCachedFile();
      }

    @Override
    public void close()
      throws IOException
      {
//        if (!dontCloseDelegate) FIXME
//          {
//            delegate.close();
//          }
      }
  }
