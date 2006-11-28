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
 * $Id: RAWMetadataSupport.java,v 1.3 2006/02/25 00:03:19 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RAWMetadataSupport.java,v 1.3 2006/02/25 00:03:19 fabriziogiudici Exp $
 *
 ******************************************************************************/
public abstract class RAWMetadataSupport extends IIOMetadata
  {
    protected Directory realPrimaryIFD;

    protected HeaderProcessor headerProcessor;

    private String nativeFormatName;

    /*******************************************************************************
     * 
     * @param tweakedPrimaryIFD
     * @param nativeFormatName
     * 
     *******************************************************************************/
    public RAWMetadataSupport (Directory primaryDirectory, String nativeFormatName, HeaderProcessor headerProcessor)
      {
        this.nativeFormatName = nativeFormatName;
        this.realPrimaryIFD = primaryDirectory;
        this.headerProcessor = headerProcessor;
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public HeaderProcessor getHeaderProcessor()
      {
        return headerProcessor;  
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public Node getAsTree (String formatName)
      {
        if (formatName == null)
          {
            throw new IllegalArgumentException("Null formatName");
          }

        if (nativeFormatName.equals(formatName))
          {
            return getNativeTree(formatName);
          }

        if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName))
          {
            return getStandardTree();
          }

        throw new IllegalArgumentException("Not a recognized format!");
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public abstract Directory getMakerNote ();

    /*******************************************************************************
     * 
     * @param formatName
     * @return
     * 
     *******************************************************************************/
    protected abstract Node getNativeTree (String formatName);

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public boolean isStandardMetadataFormatSupported ()
      {
        return true;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public String getNativeMetadataFormatName ()
      {
        return nativeFormatName;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public String[] getExtraMetadataFormatNames ()
      {
        return new String[] { };
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public boolean isReadOnly ()
      {
        return true;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public void mergeTree (String formatName,
                           Node root)
      {
        throw new UnsupportedOperationException("RAWMetadata.mergeTree()");
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public void reset ()
      {
        throw new UnsupportedOperationException("RAWMetadata.reset()");
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName() + "[");
        buffer.append("\n****primaryDirectory: " + realPrimaryIFD);

        return buffer.toString();
      }
  }
