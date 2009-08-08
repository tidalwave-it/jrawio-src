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
package it.tidalwave.imageio.rawprocessor.raw;

import javax.annotation.Nonnull;
import java.util.Arrays;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CurveOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(CurveOperation.class);
    
    protected final static double MAX_LEVEL = (1 << 16) - 1;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void process (@Nonnull final RAWImage image)
      {
        logger.fine("process(%s)", image);
        final double[] normalizationFactor = getNormalizationFactor(image);
        
        image.multiplyRedCoefficient(normalizationFactor[0]);
        image.multiplyGreenCoefficient(normalizationFactor[1]);
        image.multiplyBlueCoefficient( normalizationFactor[2]);
      }    

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected double[] getNormalizationFactor (@Nonnull final RAWImage image)
      {
        logger.fine("getNormalizationFactor(%s)", image);
        final int[] blackLevel = getBlackLevel(image);
        final double whiteLevel = getWhiteLevel(image);
        final double[] normalizationFactor = new double[3];
        logger.finer(">>>> blackLevel: %s", Arrays.toString(blackLevel));
        logger.finer(">>>> whiteLevel: %f", whiteLevel);
        
        for (int i = 0; i < normalizationFactor.length; i++)
          {
            normalizationFactor[i] = MAX_LEVEL / (whiteLevel - blackLevel[i]);
          }

        image.setBlackLevel((blackLevel[0] + blackLevel[1] + blackLevel[2]) / 3);
        logger.finer(">>>> normalizationFactor: %f %f %f", normalizationFactor[0], normalizationFactor[1], normalizationFactor[2]);
        
        return normalizationFactor;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected int[] getBlackLevel (@Nonnull final RAWImage image)
      { 
        return new int[] {0, 0, 0, 0, 0, 0, 0, 0};  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected double getWhiteLevel (@Nonnull final RAWImage image)
      {
        logger.fine("getWhiteLevel(%s)", image);
        final TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        final IFD rasterIFD = metadata.getRasterIFD();
        final int bitsPerSample = rasterIFD.getBitsPerSample()[0];
        final double whiteLevel = (1 << bitsPerSample) - 1;
        logger.finer(">>>> whiteLevel from bitsPerSample: %f", whiteLevel);
        
        return whiteLevel;
      }
    
    /*******************************************************************************************************************
     * 
     * @param primaryIFD
     * @return
     * 
     *******************************************************************************/
    protected static double getMeanValue (@Nonnull final TagRational[] black)
      {
        double v = 0;

        for (int i = 0; i < black.length; i++)
          {
            v += black[i].doubleValue();
          }

        return v / black.length;
      }
  }
