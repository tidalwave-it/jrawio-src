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
package it.tidalwave.imageio.pef;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.ThumbnailLoader;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PEFMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public PEFMetadata (@Nonnull final Directory primaryIFD, 
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected void postInit (@Nonnull final RAWImageInputStream iis)
      {
        final PentaxMakerNote makerNote = getPentaxMakerNote ();
        
        if (makerNote.isPreviewThumbnailDimensionsAvailable())
          {
            final int thumbnailOffset = makerNote.getPreviewThumbnailOffset();
            final int thumbnailSize = makerNote.getPreviewThumbnailSize();
            final int thumbnailWidth = makerNote.getPreviewThumbnailDimensions()[0];
            final int thumbnailHeight = makerNote.getPreviewThumbnailDimensions()[1];
            thumbnailLoaders.add(new ThumbnailLoader(iis, thumbnailOffset, thumbnailSize, thumbnailWidth, thumbnailHeight));
          }
      }
 
    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public PentaxMakerNote getPentaxMakerNote()
      {
        return (PentaxMakerNote)getMakerNote();
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isPhotometricInterpretationAvailable();
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isThumbnailIFD (@Nonnull final IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }
  }
