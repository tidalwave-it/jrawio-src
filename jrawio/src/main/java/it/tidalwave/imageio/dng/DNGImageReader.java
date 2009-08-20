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
package it.tidalwave.imageio.dng;

import javax.annotation.Nonnull;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.makernote.LeicaMakerNote;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = DNGImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected DNGImageReader (final @Nonnull ImageReaderSpi originatingProvider, final @Nonnull Object extension)
      {
        super(originatingProvider, IFD.class, DNGMetadata.class);
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Nonnull
    protected WritableRaster loadRAWRaster()
      throws IOException
      {
        logger.fine("loadRaster() - iis: %s", iis);
        final long time = System.currentTimeMillis();
        final IFD rasterIFD = ((TIFFMetadataSupport)metadata).getRasterIFD();
        final DNGRasterReader rasterReader = new DNGRasterReader();
        final int width = rasterIFD.getImageWidth();
        final int height = rasterIFD.getImageLength();
        final int bitsPerSample = rasterIFD.getBitsPerSample()[0];
        initializeRasterReader(width, height, bitsPerSample, rasterReader);

        if (!rasterIFD.isTileWidthAvailable())
          {
            iis.seek(rasterIFD.getStripOffsets()); // FIXME: move, it's responsibility of the rreader to seek
          }

        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.fine(">>>> loadRAWRaster() completed ok in %d msec", (System.currentTimeMillis() - time));
        
        return raster;
      }

    /*******************************************************************************************************************
     *
     * Processes the maker note.
     *
     * @param   iis          the image input stream
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    @Override
    protected void processMakerNote()
      throws IOException
      {
        String make = ((IFD)primaryDirectory).getMake();

        if (make != null)
          {
            make = make.trim();

            if ("Leica Camera AG".equals(make)) // FIXME: put in table, add other formats
              {
                makerNoteClass = LeicaMakerNote.class;
              }
          }

        super.processMakerNote();
      }
  }
