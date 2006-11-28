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
 * $Id: SRFImageReader.java,v 1.4 2006/02/15 13:53:51 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.srf.SonyMakerNote;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: SRFImageReader.java,v 1.4 2006/02/15 13:53:51 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class SRFImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.srf.SRFImageReader");

    /*******************************************************************************
     *
     ******************************************************************************/
    protected SRFImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, SonyMakerNote.class, SRFMetadata.class);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected Object wrapInput (Object input)
      {
        // FIXME: should use the superclass to check if input is a good object
        return new SRFImageInputStream((ImageInputStream)input);
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    protected WritableRaster loadRAWRaster() throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");

        long time = System.currentTimeMillis();
        SRFRasterReader rasterReader = new SRFRasterReader();
        initializeRasterReader(rasterReader);
        SonyMakerNote sonyMakerNote = (SonyMakerNote)makerNote;
        rasterReader.setRasterKey(sonyMakerNote.getSRF1().getRasterKey());
        rasterReader.setRasterOffset(sonyMakerNote.getSRF2().getRasterOffset());

        logger.finest(">>>> using rasterReader: " + rasterReader);
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

        int bitsPerSample = primaryIFD.getBitsPerSample()[0];
        int width = primaryIFD.getImageWidth();
        int height = primaryIFD.getImageLength();
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME

        //// TODO        imageIFD.getCFAPattern());
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
