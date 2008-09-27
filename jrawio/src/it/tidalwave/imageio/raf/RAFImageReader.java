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
 * $Id: PEFImageReader.java 156 2008-09-13 18:39:08Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raf;

import it.tidalwave.imageio.raw.Directory;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFImageReader.java 156 2008-09-13 18:39:08Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = RAFImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /***************************************************************************
     *
     **************************************************************************/
    protected RAFImageReader (@Nonnull final ImageReaderSpi originatingProvider,
                              @CheckForNull final Object extension)
      {
        super(originatingProvider, FujiMakerNote.class, RAFMetadata.class);
        headerProcessor = new RAFHeaderProcessor();
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     * FIXME: merge with super implementation
     *
     **************************************************************************/
    @Override
    @Nonnull
    protected Directory loadPrimaryDirectory()
      throws IOException
      {
        logger.fine("loadPrimaryDirectory() - %s", iis);
        headerProcessor.process(iis);
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        iis.seek(headerProcessor.getOffset());
        final long directoryOffset = processHeader(iis, headerProcessor); // FIXME: refactor so that processHeader doe snot use headerSkipper

        final IFD primaryIFD = new IFD();
        primaryIFD.loadAll(iis, directoryOffset);
//        iis.setBaseOffset(0);

        return primaryIFD;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    public int getWidth (final int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        final FujiRawData fujiRawData = ((RAFHeaderProcessor) headerProcessor).getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        return fujiTable1.getRawWidth();
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    public int getHeight (final int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        final FujiRawData fujiRawData = ((RAFHeaderProcessor) headerProcessor).getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        return fujiTable1.getRawHeight();
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnull
    protected WritableRaster loadRAWRaster() 
      throws IOException
      {
        logger.fine("loadRAWRaster() - iis: %s", iis);

        final long time = System.currentTimeMillis();
        final RAFRasterReader rasterReader = new RAFRasterReader();
        initializeRasterReader(rasterReader);

        logger.finest(">>>> using rasterReader: %s", rasterReader);
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in %d msec.", (System.currentTimeMillis() - time));

        return raster;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     ***************************************************************************/
    protected void initializeRasterReader (@Nonnull final RasterReader rasterReader)
      {
        final FujiRawData fujiRawData = ((RAFHeaderProcessor) headerProcessor).getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        final boolean fujiLayout = fujiTable1.isFujiLayout();
        final int offset = fujiTable1.getWidth() / (fujiLayout ? 1 : 2);
        final int width = offset + fujiTable1.getHeight() / (fujiLayout ? 2 : 1);
        rasterReader.setWidth(width);
        rasterReader.setHeight(width - 1);
        final int topMargin = (fujiTable1.getRawHeight() - fujiTable1.getHeight())/2;
        final int leftMargin = (fujiTable1.getRawWidth() - fujiTable1.getWidth())/2;
        ((RAFRasterReader)rasterReader).setCFAWidth(fujiTable1.getRawWidth());
        ((RAFRasterReader)rasterReader).setCFAHeight(fujiTable1.getRawHeight());
        ((RAFRasterReader)rasterReader).setFujiLayout(fujiLayout);
        ((RAFRasterReader)rasterReader).setOffset(offset);
        ((RAFRasterReader)rasterReader).setTopMargin(topMargin);
        ((RAFRasterReader)rasterReader).setLeftMargin(leftMargin);
        rasterReader.setBitsPerSample(12); // FIXME

        final IFD exif = ((RAFMetadata) metadata).getExifIFD();
        rasterReader.setRasterOffset(fujiRawData.getCFAOffset());
        rasterReader.setStripByteCount(fujiRawData.getCFALength());
        rasterReader.setCFAPattern(exif.getComponentConfiguration());
        rasterReader.setCompression(0); // FIXME
//        rasterReader.setCompression(primaryIFD.getCompression().intValue());
      }
  }
