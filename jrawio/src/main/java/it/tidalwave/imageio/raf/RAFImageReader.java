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
package it.tidalwave.imageio.raf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import java.io.IOException;
import it.tidalwave.imageio.craw.Directory;
import it.tidalwave.imageio.craw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = RAFImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected RAFImageReader (@Nonnull final ImageReaderSpi originatingProvider,
                              @CheckForNull final Object extension)
      {
        super(originatingProvider, FujiMakerNote.class, RAFMetadata.class);
        headerProcessor = new RAFHeaderProcessor();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     * FIXME: merge with super implementation
     *
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     * Fuji has 4 bands, not a RGB, so we can't use CS_LINEAR_RGB.
     *
     ******************************************************************************************************************/
//    @Override
//    protected ColorSpace getColorSpace()
//      {
//        return new ColorSpace(ColorSpace.TYPE_4CLR, 4)
//          {
//            @Override
//            public float[] toRGB(float[] arg0)
//              {
//                throw new UnsupportedOperationException("Not supported yet.");
//              }
//
//            @Override
//            public float[] fromRGB(float[] arg0)
//              {
//                throw new UnsupportedOperationException("Not supported yet.");
//              }
//
//            @Override
//            public float[] toCIEXYZ(float[] arg0)
//              {
//                throw new UnsupportedOperationException("Not supported yet.");
//              }
//
//            @Override
//            public float[] fromCIEXYZ(float[] arg0)
//              {
//                throw new UnsupportedOperationException("Not supported yet.");
//              }
//          };
//      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ***************************************************************************/
    protected void initializeRasterReader (@Nonnull final RasterReader rasterReader)
      {
        final FujiRawData fujiRawData = ((RAFHeaderProcessor) headerProcessor).getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        final boolean fujiLayout = fujiTable1.isFujiLayout();
        rasterReader.setWidth(fujiTable1.getRotatedWidth());
        rasterReader.setHeight(fujiTable1.getRotatedHeight());
        final int topMargin = (fujiTable1.getRawHeight() - fujiTable1.getHeight())/2;
        final int leftMargin = (fujiTable1.getRawWidth() - fujiTable1.getWidth())/2;
        ((RAFRasterReader)rasterReader).setCFAWidth(fujiTable1.getRawWidth());
        ((RAFRasterReader)rasterReader).setCFAHeight(fujiTable1.getRawHeight());
        ((RAFRasterReader)rasterReader).setFujiLayout(fujiLayout);
        ((RAFRasterReader)rasterReader).setOffset(fujiTable1.getWidth() / (fujiLayout ? 1 : 2));
        ((RAFRasterReader)rasterReader).setTopMargin(topMargin);
        ((RAFRasterReader)rasterReader).setLeftMargin(leftMargin);
        rasterReader.setBitsPerSample(12); // FIXME

        final IFD exif = ((RAFMetadata) metadata).getExifIFD();
        rasterReader.setRasterOffset(fujiRawData.getCFAOffset());
        rasterReader.setStripByteCount(fujiRawData.getCFALength());
        rasterReader.setCFAPattern(new byte[]{ 1, 2, 0, 1});
//        rasterReader.setCFAPattern(exif.getComponentConfiguration());
        rasterReader.setCompression(0); // FIXME
//        rasterReader.setCompression(primaryIFD.getCompression().intValue());
      }
  }
