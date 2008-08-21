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
 * $Id: NEFExposureOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.util.logging.Logger;
import it.tidalwave.imageio.rawprocessor.raw.ExposureOperation;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFExposureOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFExposureOperation extends ExposureOperation
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.nef.NEFExposureOperation";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();
        
        if ((nceMetadata != null) && nceMetadata.isAdvancedRawEnabled())
          {
            double exposureCompensation = nceMetadata.getEVCompensation() / 100.0;
            logger.finer (">>>> NCE exposure compensation: " + exposureCompensation);
            double coefficient = Math.pow(2, exposureCompensation);
            image.multiplyRedCoefficient(coefficient);
            image.multiplyGreenCoefficient(coefficient);
            image.multiplyBlueCoefficient(coefficient);
          }
      }    
  }
