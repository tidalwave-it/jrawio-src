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
 * $Id: CR2ImageReader.java,v 1.5 2006/06/05 11:38:20 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.cr2;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.cr2.CanonCR2MakerNote;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CR2ImageReader.java,v 1.5 2006/06/05 11:38:20 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class CR2ImageReader extends TIFFImageReaderSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.crw.CR2ImageReader");

    /*******************************************************************************
     * 
     * @param originatingProvider
     * 
     *******************************************************************************/
    protected CR2ImageReader (ImageReaderSpi originatingProvider)
      {
        super(originatingProvider, CanonCR2MakerNote.class, CR2Metadata.class);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected WritableRaster loadRAWRaster() throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");
        long time = System.currentTimeMillis();
        
        CR2RasterReader rasterReader = new CR2RasterReader();
        IFD rasterIFD = ((CR2Metadata)metadata).getRasterIFD();
        CanonCR2MakerNote cr2MakerNote = (CanonCR2MakerNote)makerNote;
        rasterReader.setWidth(cr2MakerNote.getSensorWidth());
        rasterReader.setHeight(cr2MakerNote.getSensorHeight());
        rasterReader.setBitsPerSample(12); // FIXME - gets from the model
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME RGGB - gets from the model
        iis.seek(rasterIFD.getStripOffsets());
        rasterReader.setStripByteCount(rasterIFD.getStripByteCounts()); 
        rasterReader.setCompression(rasterIFD.getCompression().intValue()); 
        
        if (rasterIFD.isCanonTileInfoAvailable())
          {
            int[] tileInfo = rasterIFD.getCanonTileInfo();
            rasterReader.setCanonTileWidth(tileInfo[1]);
            rasterReader.setCanonLastTileWidth(tileInfo[2]);
          }
        
        WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");
        
        return raster;
      }
  }
