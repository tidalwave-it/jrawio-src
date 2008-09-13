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
 * $Id: NEFCurveOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: NEFCurveOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFCurveOperation extends CurveOperation  
  {
    private final static Logger logger = getLogger(NEFCurveOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        logger.fine("getWhiteLevel()");
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        double whiteLevel;
        
        if (makerNote.isCompressionDataAvailable())
          {
            int[] linearizationTable = makerNote.getLinearizationTable();  
            whiteLevel = linearizationTable[linearizationTable.length - 1];
            logger.finer(">>>> whiteLevel from linearizationTable: %s", whiteLevel);
          }
        
        else
          {
            whiteLevel = super.getWhiteLevel(image);
          }
        
        return whiteLevel;
      }
  }
