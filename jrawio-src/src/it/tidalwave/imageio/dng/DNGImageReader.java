/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: DNGImageReader.java,v 1.3 2006/02/15 13:53:54 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.dng;

import it.tidalwave.imageio.makernote.LeicaMakerNote;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DNGImageReader.java,v 1.3 2006/02/15 13:53:54 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DNGImageReader extends TIFFImageReaderSupport
  {
    private static Logger logger = Logger.getLogger("it.tidalwave.imageio.dng.DNGImageReader");

    /*******************************************************************************
     *
     ******************************************************************************/
    protected DNGImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, IFD.class, DNGMetadata.class);
      }

    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected WritableRaster loadRAWRaster() throws IOException
      {
        logger.fine("loadRaster(iis: " + iis + ")");
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
        logger.fine(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");
        
        return raster;
      }

    /*******************************************************************************
     *
     * Processes the maker note.
     *
     * @param   iis          the image input stream
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************/
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
