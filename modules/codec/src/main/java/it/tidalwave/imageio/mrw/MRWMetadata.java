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
package it.tidalwave.imageio.mrw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.io.InputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.minolta.MinoltaRawData;
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
public class MRWMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /*******************************************************************************************************************
     *
     * Constructs this object.
     * 
     * @param   primaryIFD        the primary IFD
     * @param   iis               the input stream
     * @param   headerProcessor   the header processor
     * 
     ******************************************************************************************************************/
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
                thumbnailLoaders.add(new ThumbnailLoader(iis, offset, length)
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

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public MinoltaMakerNote getMinoltaMakerNote()
      {
        return (MinoltaMakerNote)getMakerNote();
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public MinoltaRawData getMinoltaRawData()
      {
        return ((MRWHeaderProcessor)headerProcessor).getMinoltaRawData();
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getImageSize()
      {
        return getMinoltaRawData().getPRD().getCcdSize();
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isImageWidthAvailable();
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
