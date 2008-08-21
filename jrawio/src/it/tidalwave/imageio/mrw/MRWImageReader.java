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
 * $Id: MRWImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import java.nio.ByteOrder;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWImageReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.mrw.MRWImageReader");
    
    /*******************************************************************************
     *
     ******************************************************************************/
    protected MRWImageReader (ImageReaderSpi originatingProvider, Object extension)
      {
        super(originatingProvider, MinoltaMakerNote.class, MRWMetadata.class);
        headerProcessor = new MRWHeaderProcessor();
      }

    /*******************************************************************************
     *
     * {@inheritDoc}
     * FIXME: merge with super implementation
     ******************************************************************************/
    protected Directory loadPrimaryDirectory() throws IOException
      {
        logger.info("loadPrimaryDirectory(" + iis + ")");
        headerProcessor.process(iis);
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        iis.seek(headerProcessor.getOffset());
        long directoryOffset = processHeader(iis, headerProcessor); // FIXME: refactor so that processHeader doe snot use headerSkipper

        IFD primaryIFD = new IFD();
        primaryIFD.loadAll(iis, directoryOffset);
        iis.setBaseOffset(0);
        
        return primaryIFD;
      }

    /*******************************************************************************
     *
     * {@inheritDoc}
     * FIXME: merge with super implementation
     ******************************************************************************/
    protected void processEXIFAndMakerNote (Directory directory) throws IOException
      {
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        super.processEXIFAndMakerNote(directory);
        iis.setBaseOffset(0);
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
        MRWRasterReader rasterReader = new MRWRasterReader();
        initializeRasterReader(rasterReader);

        MRWHeaderProcessor mrwHeaderProcessor = (MRWHeaderProcessor) headerProcessor;
        long rasterOffset = mrwHeaderProcessor.getRasterOffset();
        int sensorWidth = mrwHeaderProcessor.getRasterWidth();
        int sensorHeight = mrwHeaderProcessor.getRasterHeight();
        iis.seek(rasterOffset); // FIXME: set prop in rasterReader, seek in the rasterreader
        logger.finest(">>>> imageDataOffset: " + rasterOffset + ", size: " + sensorWidth + " x " + sensorHeight);
        rasterReader.setWidth(sensorWidth);
        rasterReader.setHeight(sensorHeight);
        int rasterDataSize = 0;
        int bitsPerSample = 16;
        rasterReader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        
        MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
        
//        if (minoltaMakerNote.isRasterDataSizeAvailable())
//          {
//            rasterDataSize = minoltaMakerNote.getRasterDataSize();
//            bitsPerSample = (rasterDataSize * 8) / (sensorWidth * sensorHeight);
//          }
//        
//        else
          {
            IFD primaryIFD = (IFD)primaryDirectory;
            String model = primaryIFD.getModel();
        
            if ((model != null) && ((model.indexOf("A200") > 0) || (model.indexOf("DYNAX 5D") >= 0)
                || (model.indexOf("A2") > 0) || (model.indexOf("A1") > 0)) || (model.indexOf("7D") >= 0))
              {
                bitsPerSample = 12;  
              }
        
            rasterDataSize = (sensorWidth * sensorHeight) / bitsPerSample;
            logger.finest("MODEL " + model + " BITS " + bitsPerSample);
          }
            
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setStripByteCount(rasterDataSize);
        
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
        IFD exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
        
//        rasterReader.setCFAPattern(exifIFD.getEXIFCFAPattern());
        rasterReader.setCFAPattern(new byte[]{0,1,1,2});
        rasterReader.setCompression(primaryIFD.getCompression().intValue());

        MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
        
        if (minoltaMakerNote.isRasterDataSizeAvailable())
          {
            rasterReader.setStripByteCount(minoltaMakerNote.getRasterDataSize());
          }
      }
  }
