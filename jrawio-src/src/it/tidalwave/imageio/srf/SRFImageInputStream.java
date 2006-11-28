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
 * $Id: SRFImageInputStream.java,v 1.2 2006/02/08 20:21:17 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: SRFImageInputStream.java,v 1.2 2006/02/08 20:21:17 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class SRFImageInputStream extends RAWImageInputStream
  {
    private SonyDecipher decipher;

    private ImageInputStream delegate;

    private ImageInputStream originalDelegate;

    private ByteBuffer byteBuffer;

    private long startOfEncryptedSection;

    /*******************************************************************************
     * 
     * @param delegate
     * 
     *******************************************************************************/
    public SRFImageInputStream (ImageInputStream delegate)
      {
        super(delegate);
        this.delegate = originalDelegate = delegate;
      }

    /*******************************************************************************
     * 
     * @param key
     * 
     *******************************************************************************/
    public void setDecryptionKey (int key)
      {
        decipher = new SonyDecipher(key);
      }

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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

    /*******************************************************************************
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
