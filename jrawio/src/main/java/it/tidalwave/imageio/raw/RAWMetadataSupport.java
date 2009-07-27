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
 * $Id: RAWMetadataSupport.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: RAWMetadataSupport.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public abstract class RAWMetadataSupport extends IIOMetadata
  {
    @Nonnull
    protected final Directory realPrimaryIFD;

    @CheckForNull
    protected final HeaderProcessor headerProcessor;

    @Nonnull
    private final String nativeFormatName;

    /*******************************************************************************************************************
     * 
     * @param tweakedPrimaryIFD
     * @param nativeFormatName
     * 
     *******************************************************************************/
    public RAWMetadataSupport (@Nonnull final Directory primaryDirectory, 
                               @Nonnull final String nativeFormatName, 
                               @CheckForNull final HeaderProcessor headerProcessor)
      {
        this.nativeFormatName = nativeFormatName;
        this.realPrimaryIFD = primaryDirectory;
        this.headerProcessor = headerProcessor;
      }
    
    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    @CheckForNull
    public HeaderProcessor getHeaderProcessor()
      {
        return headerProcessor;  
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Nonnull
    public Node getAsTree (final String formatName)
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

    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public abstract Directory getMakerNote ();

    /*******************************************************************************************************************
     * 
     * @param formatName
     * @return
     * 
     *******************************************************************************/
    protected abstract Node getNativeTree (String formatName);

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Override
    public boolean isStandardMetadataFormatSupported()
      {
        return true;
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String getNativeMetadataFormatName()
      {
        return nativeFormatName;
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String[] getExtraMetadataFormatNames()
      {
        return new String[] { };
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    public boolean isReadOnly ()
      {
        return true;
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    public void mergeTree (final String formatName,
                           final Node root)
      {
        throw new UnsupportedOperationException("RAWMetadata.mergeTree()");
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    public void reset()
      {
        throw new UnsupportedOperationException("RAWMetadata.reset()");
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName() + "[");
        buffer.append("\n****primaryDirectory: " + realPrimaryIFD);

        return buffer.toString();
      }
  }
