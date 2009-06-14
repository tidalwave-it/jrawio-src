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
 * $Id: PEFMetadata.java 82 2008-08-24 08:46:20Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.ThumbnailHelper;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFMetadata.java 82 2008-08-24 08:46:20Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /***************************************************************************
     *
     **************************************************************************/
    public RAFMetadata (@Nonnull final Directory primaryIFD,
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected void postInit (@Nonnull final RAWImageInputStream iis)
      {
        final FujiRawData fujiRawData = ((RAFHeaderProcessor) headerProcessor).getFujiRawData();
        final IFD exif = getExifIFD();
        thumbnailHelperList.add(new ThumbnailHelper(iis, 
                                    fujiRawData.getJPEGImageOffset(),
                                    fujiRawData.getJPEGImageLength(),
                                    exif.getPixelXDimension(),
                                    exif.getPixelYDimension()));
      }
 
    /***************************************************************************
     * 
     * @return
     * 
     **************************************************************************/
    @CheckForNull
    public FujiMakerNote getFujiMakerNote()
      {
        return (FujiMakerNote)getMakerNote();
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isPhotometricInterpretationAvailable();
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected boolean isThumbnailIFD (@Nonnull final IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    public FujiRawData getFujiRawData()
      {
        return ((RAFHeaderProcessor)headerProcessor).getFujiRawData();
      }
  }
