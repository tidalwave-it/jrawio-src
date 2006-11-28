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
 * $Id: NEFWhiteBalanceOperation.java,v 1.1 2006/02/17 15:32:11 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.util.logging.Logger;
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
 * @version CVS $Id: NEFWhiteBalanceOperation.java,v 1.1 2006/02/17 15:32:11 fabriziogiudici Exp $
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
            logger.finer(">>>> NEFWhiteBalanceInfo : " + cameraWhiteBalance + " " + image.getCFAPatternAsString() + " "
                         + " v" + Integer.toHexString(whiteBalanceInfo.getVersion()) 
                         + " " + coefficients[0] + " " + coefficients[1] + " " + coefficients[2] + " " + coefficients[3]);
            image.multiplyRedCoefficient(whiteBalanceInfo.getRedCoefficient());
            image.multiplyGreenCoefficient(whiteBalanceInfo.getGreen1Coefficient());
            image.multiplyBlueCoefficient(whiteBalanceInfo.getBlueCoefficient());
          }
        
        else if (whiteBalanceCoefficients != null)
          {
            logger.finer(">>>> using WhiteBalanceRB coefficients: " + cameraWhiteBalance);
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
