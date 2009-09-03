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
package it.tidalwave.imageio.mrw;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.craw.Directory;
import it.tidalwave.imageio.craw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.minolta.MinoltaRawData;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MRWImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = MRWImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
   
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected MRWImageReader (@Nonnull final ImageReaderSpi originatingProvider,
                              final Object extension)
      {
        super(originatingProvider, MinoltaMakerNote.class, MRWMetadata.class);
        headerProcessor = new MRWHeaderProcessor();
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
        iis.setBaseOffset(0);
        
        return primaryIFD;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     * FIXME: merge with super implementation
     * 
     ******************************************************************************************************************/
    @Override
    protected void processEXIFAndMakerNote (@Nonnull final Directory directory) 
      throws IOException
      {
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        super.processEXIFAndMakerNote(directory);
        iis.setBaseOffset(0);
      }
    
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
        final MRWRasterReader rasterReader = new MRWRasterReader();
        initializeRasterReader(rasterReader);

        final MRWHeaderProcessor mrwHeaderProcessor = (MRWHeaderProcessor)headerProcessor;
        final MinoltaRawData minoltaRawData = mrwHeaderProcessor.getMinoltaRawData();
        final long rasterOffset = minoltaRawData.getRasterOffset();
        final int sensorWidth = minoltaRawData.getPRD().getCcdSize().width;
        final int sensorHeight = minoltaRawData.getPRD().getCcdSize().height;
        iis.seek(rasterOffset); // FIXME: set prop in rasterReader, seek in the rasterreader
        logger.finest(">>>> rasterOffset: %d, size: %d x %d", rasterOffset, sensorWidth, sensorHeight);
        rasterReader.setWidth(sensorWidth);
        rasterReader.setHeight(sensorHeight);
        rasterReader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        final int dataSize = minoltaRawData.getPRD().getDataSize();
        
////        MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
////        int rasterDataSize = 0;
//        int bitsPerSample = 16;
////        if (minoltaMakerNote.isRasterDataSizeAvailable())
////          {
////            rasterDataSize = minoltaMakerNote.getRasterDataSize();
////            bitsPerSample = (rasterDataSize * 8) / (sensorWidth * sensorHeight);
////          }
////
////        else
//          {
//            final IFD primaryIFD = (IFD)primaryDirectory;
//            final String model = primaryIFD.getModel();
//
//            if ((model != null) && ((model.indexOf("A200") > 0) || (model.indexOf("DYNAX 5D") >= 0)
//                || (model.indexOf("A2") > 0) || (model.indexOf("A1") > 0)) || (model.indexOf("7D") >= 0))
//              {
//                bitsPerSample = 12;
//              }
//
//            rasterDataSize = (sensorWidth * sensorHeight) / bitsPerSample;
//            logger.finest("MODEL " + model + " BITS " + bitsPerSample);
//          }
//
//        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setBitsPerSample(dataSize);
        final int rasterDataSize = (sensorWidth * sensorHeight) / dataSize;
        rasterReader.setStripByteCount(rasterDataSize);
        
        logger.finest(">>>> using rasterReader: %s", rasterReader);
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in %d msec.", (System.currentTimeMillis() - time));

        return raster;
      }

    /*******************************************************************************************************************
     * 
     * FIXME: merge with superclass
     * 
     * @param rasterReader
     * 
     ******************************************************************************************************************/
    protected void initializeRasterReader (@Nonnull final RasterReader rasterReader)
      {
        final IFD primaryIFD = (IFD)primaryDirectory;
        final IFD exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
        
//        rasterReader.setCFAPattern(exifIFD.getEXIFCFAPattern());
        rasterReader.setCFAPattern(new byte[]{0,1,1,2});
        rasterReader.setCompression(primaryIFD.getCompression().intValue());

        final MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
        
        if (minoltaMakerNote.isRasterDataSizeAvailable())
          {
            rasterReader.setStripByteCount(minoltaMakerNote.getRasterDataSize());
          }
      }
  }
