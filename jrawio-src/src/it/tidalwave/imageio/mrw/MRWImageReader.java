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
 * $Id: MRWImageReader.java,v 1.7 2006/02/25 16:17:10 fabriziogiudici Exp $
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
 * @version CVS $Id: MRWImageReader.java,v 1.7 2006/02/25 16:17:10 fabriziogiudici Exp $
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
