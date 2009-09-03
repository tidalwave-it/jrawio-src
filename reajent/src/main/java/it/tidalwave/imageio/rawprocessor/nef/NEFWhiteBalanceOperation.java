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
package it.tidalwave.imageio.rawprocessor.nef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.craw.TagRational;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.nef.NEFWhiteBalanceInfo;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(NEFWhiteBalanceOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (PipelineArtifact artifact)
      {
        logger.fine("process()");
        NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
        NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();

        if (isNCESettingAvailable(nceMetadata))
          {
            applyNCESettings(artifact, nceMetadata);
          }
        
        else
          {
            applyCameraSettings(artifact, makerNote);
          }
      }    

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void applyCameraSettings (PipelineArtifact artifact, NikonMakerNote3 makerNote)
      {
        NikonMakerNote3.WhiteBalance cameraWhiteBalance = makerNote.getWhiteBalance();       
        TagRational[] whiteBalanceCoefficients = makerNote.getWhiteBalanceRBCoefficients();
        NEFWhiteBalanceInfo whiteBalanceInfo = makerNote.getWhiteBalanceInfo();
        
        if ((whiteBalanceInfo != null) && whiteBalanceInfo.isValid())
          {
            int[] coefficients = whiteBalanceInfo.getCoefficients();
            logger.finer(">>>> NEFWhiteBalanceInfo: %s %s v%s %s %s %s %s",
                         cameraWhiteBalance, artifact.getCFAPatternAsString(), Integer.toHexString(whiteBalanceInfo.getVersion()),
                         coefficients[0],  coefficients[1], coefficients[2], coefficients[3]);
            artifact.multiplyRedCoefficient(whiteBalanceInfo.getRedCoefficient());
            artifact.multiplyGreenCoefficient(whiteBalanceInfo.getGreen1Coefficient());
            artifact.multiplyBlueCoefficient(whiteBalanceInfo.getBlueCoefficient());
          }
        
        else if (whiteBalanceCoefficients != null)
          {
            logger.finer(">>>> using WhiteBalanceRB coefficients: %s", cameraWhiteBalance);
            artifact.multiplyRedCoefficient(whiteBalanceCoefficients[0].doubleValue());
            artifact.multiplyBlueCoefficient(whiteBalanceCoefficients[1].doubleValue());
          }
      }
  
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
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
        
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private void applyNCESettings (PipelineArtifact artifact, NikonCaptureEditorMetadata nceMetadata)
      {
        logger.finer(">>>> using NCE coefficients");
        artifact.multiplyRedCoefficient(nceMetadata.getWhiteBalanceRedCoeff());
        artifact.multiplyBlueCoefficient(nceMetadata.getWhiteBalanceBlueCoeff());
      }
  }
