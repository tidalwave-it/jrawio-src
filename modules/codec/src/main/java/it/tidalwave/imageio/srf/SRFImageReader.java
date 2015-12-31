/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
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
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.srf;

import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SRFImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger(SRFImageReader.class.getName());

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected SRFImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, SonyMakerNote.class, SRFMetadata.class);
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected Object wrapInput (Object input)
      {
        // FIXME: should use the superclass to check if input is a good object
        return new SRFImageInputStream((ImageInputStream)input);
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
        logger.fine("loadRAWRaster() - iis: %s", iis);

        final long time = System.currentTimeMillis();
        final SRFRasterReader rasterReader = new SRFRasterReader();
        initializeRasterReader(rasterReader);
        final SonyMakerNote sonyMakerNote = (SonyMakerNote)makerNote;

        if (sonyMakerNote.getSRF1() != null)
          {
            rasterReader.setRasterKey(sonyMakerNote.getSRF1().getRasterKey());
            rasterReader.setRasterOffset(sonyMakerNote.getSRF2().getRasterOffset());
          }

        logger.finest(">>>> using rasterReader: %s", rasterReader);
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in %d msec", (System.currentTimeMillis() - time));

        return raster;
      }

    /*******************************************************************************************************************
     * 
     * FIXME: merge with superclass
     * 
     * @param rasterReader
     * 
     *******************************************************************************/
    protected void initializeRasterReader (final @Nonnull RasterReader rasterReader)
      {
        IFD ifd = (IFD)primaryDirectory;

        if (!ifd.isBitsPerSampleAvailable())
          {
            ifd = (IFD)ifd.getSubDirectories().iterator().next();
          }

        final int bitsPerSample = ifd.getBitsPerSample()[0];
        final int width = ifd.getImageWidth();
        final int height = ifd.getImageLength();
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME

        //// TODO        imageIFD.getCFAPattern());
        rasterReader.setCompression(ifd.getCompression().intValue());

        if (ifd.isStripByteCountsAvailable())
          {
            rasterReader.setStripByteCount(ifd.getStripByteCounts());
          }

        if (ifd.isTileWidthAvailable())
          {
            int tileWidth = ifd.getTileWidth();
            int tileLength = ifd.getTileLength();
            rasterReader.setTileWidth(tileWidth);
            rasterReader.setTileHeight(tileLength);
            rasterReader.setTilesAcross((width + tileWidth - 1) / tileWidth);
            rasterReader.setTilesDown((height + tileLength - 1) / tileLength);
            rasterReader.setTileOffsets(ifd.getTileOffsets());
            //int[] tileByteCounts = primaryIFD.getTileByteCounts();
          }

        if (ifd.isLinearizationTableAvailable())
          {
            rasterReader.setLinearizationTable(ifd.getLinearizationTable());
          }
      }
  }
