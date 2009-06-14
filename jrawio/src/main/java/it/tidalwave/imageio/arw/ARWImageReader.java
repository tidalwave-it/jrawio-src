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
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * The ARW samples we have use the SubIFD field in IFD0 to store the offset of
 * the raster.
 *
 ******************************************************************************/
class ARWPrimaryIFD extends IFD
  {
    private static final long serialVersionUID = -7810975852445063637L;

    public int getRasterOffset()
      {
        return super.getSubIFDs()[0];
      }
    
    @Override
    public boolean isSubIFDsAvailable() 
      {
        return false;
      }
    
    @Override
    public int[] getSubIFDs() 
      {
        return new int[0];
//        final int[] original = super.getSubIFDs();
//        final int bad = 0x10000;
//        
//        int count = 0;
//        
//        for (int i = 0; i < original.length; i++)
//          {
//            if (original[i] != bad)
//              {
//                count++;  
//              } 
//          }
//        
//        final int[] result = new int[count];
//        
//        int j = 0;
//        for (int i = 0; i < original.length; i++)
//          {
//            if (original[i] != bad)
//              {
//                result[j++] = original[i];  
//              } 
//          }
//        
//        return result;
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
     **************************************************************************/
    @Override
    @Nonnull
    protected IFD createPrimaryIFD() 
      {
        return new ARWPrimaryIFD();
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

        long time = System.currentTimeMillis();
        final ARWRasterReader rasterReader = new ARWRasterReader();
        initializeRasterReader(rasterReader);

        logger.finest(">>>> using rasterReader: %s", rasterReader);
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in %d msec.", (System.currentTimeMillis() - time));

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
        final ARWMetadata arwMetadata = (ARWMetadata)metadata;
        final int bitsPerSample = arwMetadata.getMinoltaRawData().getPRD().getPixelSize();
        final int width = arwMetadata.getWidth();
        final int height = arwMetadata.getHeight();
        rasterReader.setRasterOffset(((ARWPrimaryIFD)primaryDirectory).getRasterOffset()); 
        rasterReader.setTileOffsets(new int[1]); // FIXME: useless, but otherwise an assertion fails
        rasterReader.setCompression(1); // FIXME
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME
      }
  }
