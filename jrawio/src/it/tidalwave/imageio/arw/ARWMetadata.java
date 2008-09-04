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
 * $Id: ARWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.arw;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import javax.annotation.Nonnegative;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ARWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 3012868418676854749L;
    
//    private final IFD exifIFD;

    /***************************************************************************
     *
     **************************************************************************/
    public ARWMetadata (@Nonnull final Directory primaryIFD,
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
//        exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
      }

    /***************************************************************************
     * 
     * @return
     * 
     **************************************************************************/
    @Nonnull
    public ARWMakerNote getARWMakerNote()
      {
        return (ARWMakerNote)getMakerNote();
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
//    @Override
//    public IFD getExifIFD() 
//      {
//        return exifIFD;
//      }
//    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return false; // TODO: type == 0, but never happens
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
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnegative
    @Override
    public int getWidth()
      {
        return getExifIFD().getPixelXDimension();
      }
    
    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnegative
    @Override
    public int getHeight()
      {
        return getExifIFD().getPixelYDimension();
      }
  }
