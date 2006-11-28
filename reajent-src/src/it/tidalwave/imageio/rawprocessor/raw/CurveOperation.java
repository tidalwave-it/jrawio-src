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
 * $Id: CurveOperation.java,v 1.2 2006/02/25 16:58:36 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import it.tidalwave.imageio.raw.TagRational;
import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CurveOperation.java,v 1.2 2006/02/25 16:58:36 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class CurveOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(CurveOperation.class);
    
    protected final static double MAX_LEVEL = (1 << 16) - 1;
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        double[] normalizationFactor = getNormalizationFactor(image);
        
        image.multiplyRedCoefficient(normalizationFactor[0]);
        image.multiplyGreenCoefficient(normalizationFactor[1]);
        image.multiplyBlueCoefficient( normalizationFactor[2]);
      }    

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected double[] getNormalizationFactor (RAWImage image)
      {
        logger.fine("getNormalizationFactor()");
        int[] blackLevel = getBlackLevel(image);
        double whiteLevel = getWhiteLevel(image);        
        double[] normalizationFactor = new double[3];
        logger.finer(">>>> blackLevel: " + blackLevel[0] + " " + blackLevel[1] + " " + blackLevel[2]);
        logger.finer(">>>> whiteLevel: " + whiteLevel);
        
        for (int i = 0; i < normalizationFactor.length; i++)
          {
            normalizationFactor[i] = MAX_LEVEL / (whiteLevel - blackLevel[i]);
          }

        image.setBlackLevel((blackLevel[0] + blackLevel[1] + blackLevel[2]) / 3);
        logger.finer(">>>> normalizationFactor: " + normalizationFactor[0] + " " + normalizationFactor[1] + " " + normalizationFactor[2]);
        
        return normalizationFactor;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected int[] getBlackLevel (RAWImage image)
      { 
        return new int[] {0, 0, 0, 0, 0, 0, 0, 0};  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        logger.fine("getWhiteLevel()");
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD rasterIFD = metadata.getRasterIFD();
        int bitsPerSample = rasterIFD.getBitsPerSample()[0];
        double whiteLevel = (1 << bitsPerSample) - 1;
        logger.finer(">>>> whiteLevel from bitsPerSample: " + whiteLevel);
        
        return whiteLevel;
      }
    
    /*******************************************************************************
     * 
     * @param primaryIFD
     * @return
     * 
     *******************************************************************************/
    protected static double getMeanValue (TagRational[] black)
      {
        double v = 0;

        for (int i = 0; i < black.length; i++)
          {
            v += black[i].doubleValue();
          }

        return v / black.length;
      }
  }
