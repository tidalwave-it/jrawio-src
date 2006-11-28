/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: PEFMetadata.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.pef;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.ThumbnailHelper;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: PEFMetadata.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class PEFMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /*******************************************************************************
     *
     ******************************************************************************/
    public PEFMetadata (Directory primaryIFD, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    protected void postInit (RAWImageInputStream iis)
      {
        PentaxMakerNote makerNote = getPentaxMakerNote ();
        
        if (makerNote.isPreviewThumbnailDimensionsAvailable())
          {
            int thumbnailOffset = makerNote.getPreviewThumbnailOffset();
            int thumbnailSize = makerNote.getPreviewThumbnailSize();
            int thumbnailWidth = makerNote.getPreviewThumbnailDimensions()[0];
            int thumbnailHeight = makerNote.getPreviewThumbnailDimensions()[1];
            thumbnailHelperList.add(new ThumbnailHelper(iis, thumbnailOffset, thumbnailSize, thumbnailWidth, thumbnailHeight));
          }
      }
 
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public PentaxMakerNote getPentaxMakerNote ()
      {
        return (PentaxMakerNote)getMakerNote();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return ifd.isPhotometricInterpretationAvailable();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }
  }
