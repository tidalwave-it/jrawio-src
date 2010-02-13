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

import it.tidalwave.imageio.makernote.LeicaMakerNote;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.util.Logger;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGImageReader extends TIFFImageReaderSupport
  {
    private static Logger logger = Logger.getLogger("it.tidalwave.imageio.dng.DNGImageReader");

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected DNGImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, IFD.class, DNGMetadata.class);
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected WritableRaster loadRAWRaster()
      throws IOException
      {
        logger.fine("loadRaster() - iis: %s", iis);
        long time = System.currentTimeMillis();
        IFD rasterIFD = ((TIFFMetadataSupport)metadata).getRasterIFD();
        DNGRasterReader rasterReader = new DNGRasterReader();
        int width = rasterIFD.getImageWidth();
        int height = rasterIFD.getImageLength();
        int bitsPerSample = rasterIFD.getBitsPerSample()[0];
        initializeRasterReader(width, height, bitsPerSample, rasterReader);

        if (!rasterIFD.isTileWidthAvailable())
          {
            iis.seek(rasterIFD.getStripOffsets()); // FIXME: move, it's responsibility of the rreader to seek
          }

        WritableRaster raster = rasterReader.loadRaster(iis, this);
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
    protected void processMakerNote() throws IOException
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
