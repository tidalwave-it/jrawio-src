/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: RAWMetadataSupport.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RAWMetadataSupport.java 55 2008-08-21 19:43:51Z fabriziogiudici $
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
