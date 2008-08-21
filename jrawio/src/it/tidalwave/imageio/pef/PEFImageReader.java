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
 * $Id: PEFImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.pef;

import java.util.logging.Logger;
import java.io.IOException;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: PEFImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class PEFImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.pef.PEFImageReader");

    /*******************************************************************************
     *
     ******************************************************************************/
    protected PEFImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, PentaxMakerNote.class, PEFMetadata.class);
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    protected WritableRaster loadRAWRaster () throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");

        long time = System.currentTimeMillis();
        PEFRasterReader rasterReader = new PEFRasterReader();
        initializeRasterReader(rasterReader);

        logger.finest(">>>> using rasterReader: " + rasterReader);
        IFD primaryIFD = (IFD)primaryDirectory;
        iis.seek(primaryIFD.getStripOffsets()); // FIXME: set attribute in raster reader, seek done in rasterreader
        WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");

        return raster;
      }

    /*******************************************************************************
     * 
     * FIXME: merge with superclass
     * 
     * @param rasterReader
     * 
     *******************************************************************************/
    protected void initializeRasterReader (RasterReader rasterReader)
      {
        IFD primaryIFD = (IFD)primaryDirectory;
        IFD exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
        
        int bitsPerSample = (primaryIFD.getCompression().intValue() == 1) ? 16 : 12; // packed in words primaryIFD.getBitsPerSample()[0];
        int width = primaryIFD.getImageWidth();
        int height = primaryIFD.getImageLength();
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
            int tileWidth = primaryIFD.getTileWidth();
            int tileLength = primaryIFD.getTileLength();
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
