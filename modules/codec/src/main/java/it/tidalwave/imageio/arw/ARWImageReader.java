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
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.arw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;

/***********************************************************************************************************************
 *
 * The ARW samples we have use the SubIFD field in IFD0 to store the offset of
 * the raster.
 *
 **********************************************************************************************************************/
class ARWPrimaryIFD extends IFD
  {
    private static final long serialVersionUID = -7810975852445063637L;

    @Nonnegative
    public int getRasterOffset()
      {
        return isA100() ? super.getSubIFDs()[0] : super.getStripOffsets();
      }
    
    @Override
    public boolean isSubIFDsAvailable() 
      {
        return isA100() ? false : super.isSubIFDsAvailable();
      }
    
    @Override
    public int[] getSubIFDs() 
      {
        return isA100() ? new int[0] : super.getSubIFDs();
//        final int[] original = super.getSubIFDs(); FIXME
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

    private boolean isA100()
      {
        return "DSLR-A100".equals(getModel());
      }
  }

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = ARWImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected ARWImageReader (@Nonnull final ImageReaderSpi originatingProvider, 
                              @CheckForNull final Object extension)
      {
        super(originatingProvider, ARWMakerNote.class, ARWMetadata.class);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected IFD createPrimaryIFD() 
      {
        return new ARWPrimaryIFD();
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
        final ARWRasterReader rasterReader = new ARWRasterReader();
        initializeRasterReader(rasterReader);

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
        final ARWMetadata arwMetadata = (ARWMetadata)metadata;
        final PRD prd = arwMetadata.getMinoltaRawData().getPRD();
        final int bitsPerSample = prd.getPixelSize();
        rasterReader.setRasterOffset(((ARWPrimaryIFD)primaryDirectory).getRasterOffset()); 
        rasterReader.setTileOffsets(new int[1]); // FIXME: useless, but otherwise an assertion fails
        rasterReader.setCompression(1); // FIXME
        rasterReader.setWidth(prd.getCcdSize().width);
        rasterReader.setHeight(prd.getCcdSize().height);
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME
      }
  }
