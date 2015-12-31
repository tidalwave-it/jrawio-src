/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.srf;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStreamImpl;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SRFImageInputStream extends RAWImageInputStreamImpl
  {
    private SonyDecipher decipher;

    private ImageInputStream delegate;

    private ImageInputStream originalDelegate;

    private ByteBuffer byteBuffer;

    private long startOfEncryptedSection;

    /*******************************************************************************************************************
     * 
     * @param delegate
     * 
     *******************************************************************************/
    public SRFImageInputStream (ImageInputStream delegate)
      {
        super(delegate);
        this.delegate = originalDelegate = delegate;
      }

    /*******************************************************************************************************************
     * 
     * @param key
     * 
     *******************************************************************************/
    public void setDecryptionKey (int key)
      {
        decipher = new SonyDecipher(key);
      }

    /*******************************************************************************************************************
     * 
     * @param size
     * 
     *******************************************************************************/
    public void startEncryptedSection (int size) throws IOException
      {
        if (decipher == null)
          {
            throw new IllegalArgumentException("The decryption key has not been set");
          }

        startOfEncryptedSection = delegate.getStreamPosition();
        decipher.decrypt(originalDelegate, size);
        byteBuffer = decipher.getByteBuffer();
        //System.err.println("encrypted section started at " + startOfEncryptedSection);
      }

    public void startEncryptedSection (long pos,
                                       ByteBuffer byteBuffer)
      {
        startOfEncryptedSection = pos;
        this.byteBuffer = byteBuffer;
      }

    /*******************************************************************************************************************
     * 
     * @throws IOException 
     * 
     *******************************************************************************/
    public void stopEncryptedSection () throws IOException
      {
        originalDelegate.seek(getStreamPosition());
        byteBuffer = null;
        delegate = originalDelegate;
        //System.err.println("encrypted section ended at " + getStreamPosition());
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public int read () throws IOException
      {
        if (byteBuffer != null)
          {
            return byteBuffer.get() & 0xff;
          }

        return delegate.read();
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public int read (byte[] b,
                     int off,
                     int len) throws IOException
      {
        if (byteBuffer != null)
          {
            byteBuffer.get(b, off, len);
            return len;
          }

        return delegate.read(b, off, len);
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public int skipBytes (int n) throws IOException
      {
        if (byteBuffer != null)
          {
            byteBuffer.position(byteBuffer.position() + n);
            return n;
          }

        return delegate.skipBytes(n);
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public long skipBytes (long n) throws IOException
      {
        if (byteBuffer != null)
          {
            byteBuffer.position(byteBuffer.position() + (int)n);
            return n;
          }

        return delegate.skipBytes(n);
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public long getStreamPosition () throws IOException
      {
        if (byteBuffer != null)
          {
            return startOfEncryptedSection + byteBuffer.position();
          }

        return delegate.getStreamPosition();
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void seek (long pos) throws IOException
      {
        if (byteBuffer != null)
          {
            byteBuffer.position((int)(pos - startOfEncryptedSection));
            return;
          }

        delegate.seek(pos);
      }
  }
