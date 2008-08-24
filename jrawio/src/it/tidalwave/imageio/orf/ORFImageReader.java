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
 * $Id: ORFImageReader.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ORFImageReader.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = ORFImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /***************************************************************************
     *
     **************************************************************************/
    protected ORFImageReader (@Nonnull final ImageReaderSpi originatingProvider, 
                              @CheckForNull final Object extension)
      {
        super(originatingProvider, OlympusMakerNote.class, ORFMetadata.class);
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    protected WritableRaster loadRAWRaster() 
      throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");

        final long time = System.currentTimeMillis();
        final String model = ((IFD)primaryDirectory).getModel();
        final ORFRasterReader rasterReader = ORFRasterReader.getInstance(model);
        initializeRasterReader(rasterReader);

        logger.finest(">>>> using rasterReader: " + rasterReader);
        final IFD primaryIFD = (IFD)primaryDirectory;
        iis.seek(primaryIFD.getStripOffsets()); // FIXME: set attribute in raster reader, seek done in rasterreader
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");

        return raster;
      }

    /***************************************************************************
     * 
     * FIXME: merge with superclass
     * 
     * @param rasterReader
     * 
     **************************************************************************/
    protected void initializeRasterReader (@Nonnull final RasterReader rasterReader)
      {
        final IFD primaryIFD = (IFD)primaryDirectory;
        final IFD exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
        
        final int bitsPerSample =  primaryIFD.getBitsPerSample()[0]; 
        final int width = primaryIFD.getImageWidth();
        final int height = primaryIFD.getImageLength();
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(exifIFD.getEXIFCFAPattern());
        rasterReader.setCompression(primaryIFD.getCompression().intValue());

        if (primaryIFD.isStripByteCountsAvailable())
          {
            rasterReader.setStripByteCount(primaryIFD.getStripByteCounts());
          }

        if (primaryIFD.isTileWidthAvailable())
          {
            final int tileWidth = primaryIFD.getTileWidth();
            final int tileLength = primaryIFD.getTileLength();
            rasterReader.setTileWidth(tileWidth);
            rasterReader.setTileHeight(tileLength);
            rasterReader.setTilesAcross((width + tileWidth - 1) / tileWidth);
            rasterReader.setTilesDown((height + tileLength - 1) / tileLength);
            rasterReader.setTileOffsets(primaryIFD.getTileOffsets());
            //int[] tileByteCounts = primaryIFD.getTileByteCounts();
          }

        if (primaryIFD.isLinearizationTableAvailable())
          {
            rasterReader.setLinearizationTable(primaryIFD.getLinearizationTable());
          }
      }
  }
