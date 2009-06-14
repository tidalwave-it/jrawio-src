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
 * $Id: MRWMetadata.java 140 2008-09-07 12:48:37Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.InputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.ThumbnailHelper;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 140 2008-09-07 12:48:37Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /***************************************************************************
     *
     * Constructs this object.
     * 
     * @param   primaryIFD        the primary IFD
     * @param   iis               the input stream
     * @param   headerProcessor   the header processor
     * 
     **************************************************************************/
    public MRWMetadata (@Nonnull final Directory primaryIFD, 
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);

        final MinoltaMakerNote minoltaMakerNote = getMinoltaMakerNote();
        
        if (minoltaMakerNote != null)
          {
            if (minoltaMakerNote.isJpegThumbnailLengthAvailable() &&
                minoltaMakerNote.isJpegThumbnailOffsetAvailable())
              {
                final MRWHeaderProcessor mrwhp = (MRWHeaderProcessor)headerProcessor;        
                final int offset = minoltaMakerNote.getJpegThumbnailOffset() + mrwhp.getBaseOffset();
                final int length = minoltaMakerNote.getJpegThumbnailLength();
                thumbnailHelperList.add(new ThumbnailHelper(iis, offset, length)
                  {
                    // The embedded JPEG thumbnail lacks the JPEG header
                    @Override
                    protected InputStream createInputStream (final byte[] buffer)
                      {
                        final byte[] result = new byte[buffer.length + 2];
                        result[0] = (byte)0xff;
                        result[1] = (byte)0xd8;
                        System.arraycopy(buffer, 0, result, 2, buffer.length);
                        return super.createInputStream(result);
                      }
                  });
              }
          }
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    public int getWidth()
      {
        return getPrimaryIFD().getImageWidth();
      }
    
    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    public int getHeight()
      {
        return getPrimaryIFD().getImageLength();
      }
    
    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @CheckForNull
    public MinoltaMakerNote getMinoltaMakerNote()
      {
        return (MinoltaMakerNote)getMakerNote();
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    @CheckForNull
    public MinoltaRawData getMinoltaRawData()
      {
        return ((MRWHeaderProcessor)headerProcessor).getMinoltaRawData();
      }
    
    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isImageWidthAvailable();
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
  }
