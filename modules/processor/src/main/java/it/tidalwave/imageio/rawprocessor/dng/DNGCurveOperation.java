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
package it.tidalwave.imageio.rawprocessor.dng;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGCurveOperation extends CurveOperation  
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.dng.DNGCurveOperation";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected int[] getBlackLevel (PipelineArtifact artifact)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)artifact.getRAWMetadata();
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
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected double getWhiteLevel (PipelineArtifact artifact)
      {
        logger.fine("getWhiteLevel()");
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)artifact.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        IFD rasterIFD = metadata.getRasterIFD();
        
        double whiteLevel;
        
        if (primaryIFD.isWhiteLevelAvailable())
          {
            whiteLevel = primaryIFD.getWhiteLevel()[0];  
            logger.finer(">>>> whiteLevel from WhiteLevel in primaryIFD: %f", whiteLevel);
          }
        
        else if (rasterIFD.isWhiteLevelAvailable())
          {
            whiteLevel = rasterIFD.getWhiteLevel()[0];  
            logger.finer(">>>> whiteLevel from WhiteLevel in rasterIFD: %f", whiteLevel);
          }
        
        else
          {
            int bitsPerSample = rasterIFD.getBitsPerSample()[0];
            whiteLevel = (1 << bitsPerSample) - 1;
            logger.finer(">>>> whiteLevel from BitsPerSample: %f", whiteLevel);
          }
        
        return whiteLevel;
      }
  }
