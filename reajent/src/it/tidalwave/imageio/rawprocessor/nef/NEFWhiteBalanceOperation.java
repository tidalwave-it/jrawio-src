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
 * $Id: NEFWhiteBalanceOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.nef.NEFWhiteBalanceInfo;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: NEFWhiteBalanceOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(NEFWhiteBalanceOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();

        if (isNCESettingAvailable(nceMetadata))
          {
            applyNCESettings(image, nceMetadata);
          }
        
        else
          {
            applyCameraSettings(image, makerNote);
          }
      }    

    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    private void applyCameraSettings (RAWImage image, NikonMakerNote3 makerNote)
      {
        NikonMakerNote3.WhiteBalance cameraWhiteBalance = makerNote.getWhiteBalance();       
        TagRational[] whiteBalanceCoefficients = makerNote.getWhiteBalanceRBCoefficients();
        NEFWhiteBalanceInfo whiteBalanceInfo = makerNote.getWhiteBalanceInfo();
        
        if ((whiteBalanceInfo != null) && whiteBalanceInfo.isValid())
          {
            int[] coefficients = whiteBalanceInfo.getCoefficients();
            logger.finer(">>>> NEFWhiteBalanceInfo: %s %s v%s %s %s %s %s",
                         cameraWhiteBalance, image.getCFAPatternAsString(), Integer.toHexString(whiteBalanceInfo.getVersion()),
                         coefficients[0],  coefficients[1], coefficients[2], coefficients[3]);
            image.multiplyRedCoefficient(whiteBalanceInfo.getRedCoefficient());
            image.multiplyGreenCoefficient(whiteBalanceInfo.getGreen1Coefficient());
            image.multiplyBlueCoefficient(whiteBalanceInfo.getBlueCoefficient());
          }
        
        else if (whiteBalanceCoefficients != null)
          {
            logger.finer(">>>> using WhiteBalanceRB coefficients: %s", cameraWhiteBalance);
            image.multiplyRedCoefficient(whiteBalanceCoefficients[0].doubleValue());    
            image.multiplyBlueCoefficient(whiteBalanceCoefficients[1].doubleValue());    
          }
      }
  
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    private boolean isNCESettingAvailable (NikonCaptureEditorMetadata nceMetadata)
      {
      //   
      // The check on temperature is due since photos rotated by Nikon View has erroneously WhiteBalanceEnabled == true
      //
        return (nceMetadata != null)
             && nceMetadata.isWhiteBalanceEnabled()
             && ((nceMetadata.getWhiteBalanceTemperature() != 0) 
              || (nceMetadata.getWhiteBalanceWhitePoint() == NikonCaptureEditorMetadata.WHITE_BALANCE_USE_GRAY_POINT));
      }
        
    /*******************************************************************************
     *
     *
     *
     ******************************************************************************/
    private void applyNCESettings (RAWImage image, NikonCaptureEditorMetadata nceMetadata)
      {
        logger.finer(">>>> using NCE coefficients");
        image.multiplyRedCoefficient(nceMetadata.getWhiteBalanceRedCoeff());
        image.multiplyBlueCoefficient(nceMetadata.getWhiteBalanceBlueCoeff());
      }
  }
