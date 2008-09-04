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
 * $Id: ARWImageReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.arw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.srf.SonyMakerNote;
import it.tidalwave.imageio.srf.SRFImageInputStream;
import it.tidalwave.imageio.srf.SRFRasterReader;

class PatchedIFD extends IFD
  {
    /***************************************************************************
     *
     * The ARW samples we have declare a subifd at 0x10000, but it not coded as
     * a regular IFD and triggers an error, so we are ignoring it.
     * 
     * FIXME: try to understand what's there.
     *
     **************************************************************************/
    @Override
    public int[] getSubIFDs() 
      {
        final int[] original = super.getSubIFDs();
        final int bad = 0x10000;
        
        int count = 0;
        
        for (int i = 0; i < original.length; i++)
          {
            if (original[i] != bad)
              {
                count++;  
              } 
          }
        
        final int[] result = new int[count];
        
        int j = 0;
        for (int i = 0; i < original.length; i++)
          {
            if (original[i] != bad)
              {
                result[j++] = original[i];  
              } 
          }
        
        return result;
      }
  }

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ARWImageReader.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = ARWImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /***************************************************************************
     *
     **************************************************************************/
    protected ARWImageReader (@Nonnull final ImageReaderSpi originatingProvider, 
                              @CheckForNull final Object extension)
      {
        super(originatingProvider, ARWMakerNote.class, ARWMetadata.class);
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
//     **************************************************************************/
//    @Override
//    protected Object wrapInput (@Nonnull final Object input)
//      {
//        // FIXME: SRFImageInputStream provides decription, I don't know if it's needed for ARW
//        // FIXME: should use the superclass to check if input is a good object
//        return new SRFImageInputStream((ImageInputStream)input);
//      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    protected IFD createPrimaryIFD() 
      {
        return new PatchedIFD();
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
//        logger.fine("loadRAWRaster(iis: " + iis + ")");
//
//        long time = System.currentTimeMillis();
//        SRFRasterReader rasterReader = new SRFRasterReader();
//        initializeRasterReader(rasterReader);
////        SonyMakerNote sonyMakerNote = (SonyMakerNote)makerNote;
////        rasterReader.setRasterKey(sonyMakerNote.getSRF1().getRasterKey());
////        rasterReader.setRasterOffset(sonyMakerNote.getSRF2().getRasterOffset());
//
//        logger.finest(">>>> using rasterReader: " + rasterReader);
//        WritableRaster raster = rasterReader.loadRaster(iis, this);
//        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");
//
//        return raster;
        return null;
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
        IFD exifIFD = ((ARWMetadata)metadata).getExifIFD();

//        int bitsPerSample = exifIFD.getBitsPerSample()[0];
        int width = exifIFD.getPixelXDimension();
        int height = exifIFD.getPixelYDimension();
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
//        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME

//        //// TODO        imageIFD.getCFAPattern());
//        rasterReader.setCompression(primaryIFD.getCompression().intValue());
//
//        if (primaryIFD.isStripByteCountsAvailable())
//          {
//            rasterReader.setStripByteCount(primaryIFD.getStripByteCounts());
//          }
//
//        if (primaryIFD.isTileWidthAvailable())
//          {
//            int tileWidth = primaryIFD.getTileWidth();
//            int tileLength = primaryIFD.getTileLength();
//            rasterReader.setTileWidth(tileWidth);
//            rasterReader.setTileHeight(tileLength);
//            rasterReader.setTilesAcross((width + tileWidth - 1) / tileWidth);
//            rasterReader.setTilesDown((height + tileLength - 1) / tileLength);
//            rasterReader.setTileOffsets(primaryIFD.getTileOffsets());
//            //int[] tileByteCounts = primaryIFD.getTileByteCounts();
//          }
//
//        if (primaryIFD.isLinearizationTableAvailable())
//          {
//            rasterReader.setLinearizationTable(primaryIFD.getLinearizationTable());
//          }
      }
  }
