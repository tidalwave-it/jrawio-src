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
 * $Id: CurveOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
 * @version $Id: CurveOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
