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
 * $Id: DNGCurveOperation.java,v 1.1 2006/02/17 15:32:14 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DNGCurveOperation.java,v 1.1 2006/02/17 15:32:14 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DNGCurveOperation extends CurveOperation  
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.dng.DNGCurveOperation";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected int[] getBlackLevel (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        double blackLevel = 0;

        if (primaryIFD.isBlackLevelAvailable())
          {
            blackLevel += getMeanValue(primaryIFD.getBlackLevel());
          }

        if (primaryIFD.isBlackLevelDeltaHAvailable())
          {
            blackLevel += getMeanValue(primaryIFD.getBlackLevelDeltaH());
          }

        if (primaryIFD.isBlackLevelDeltaVAvailable())
          {
            blackLevel += getMeanValue(primaryIFD.getBlackLevelDeltaV());
          }

        return new int[] { (int)blackLevel, (int)blackLevel, (int)blackLevel };
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        logger.fine("getWhiteLevel()");
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        IFD rasterIFD = metadata.getRasterIFD();
        
        double whiteLevel;
        
        if (primaryIFD.isWhiteLevelAvailable())
          {
            whiteLevel = primaryIFD.getWhiteLevel()[0];  
            logger.finer(">>>> whiteLevel from WhiteLevel in primaryIFD: " + whiteLevel);
          }
        
        else if (rasterIFD.isWhiteLevelAvailable())
          {
            whiteLevel = rasterIFD.getWhiteLevel()[0];  
            logger.finer(">>>> whiteLevel from WhiteLevel in rasterIFD: " + whiteLevel);
          }
        
        else
          {
            int bitsPerSample = rasterIFD.getBitsPerSample()[0];
            whiteLevel = (1 << bitsPerSample) - 1;
            logger.finer(">>>> whiteLevel from BitsPerSample: " + whiteLevel);
          }
        
        return whiteLevel;
      }
  }
