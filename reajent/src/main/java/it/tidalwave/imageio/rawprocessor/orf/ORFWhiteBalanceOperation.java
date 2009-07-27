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
 * $Id: ORFWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.orf;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.orf.ImageProcessing;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ORFWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class ORFWhiteBalanceOperation extends OperationSupport
  {
    private final static String CLASS = ORFWhiteBalanceOperation.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void process (@Nonnull final RAWImage image)
      {
        logger.fine("process()");
        final OlympusMakerNote orfMakernote = (OlympusMakerNote)image.getRAWMetadata().getMakerNote();
        
        if (orfMakernote != null)
          {
            final ImageProcessing imageProcessing = orfMakernote.getOlympusImageProcessing();
            
            if ((imageProcessing != null) && imageProcessing.isRBCoefficientsAvailable())
              {
                final int[] rbCoefficients = imageProcessing.getRBCoefficients();
                image.multiplyRedCoefficient(rbCoefficients[0] / 256.0); 
                image.multiplyBlueCoefficient(rbCoefficients[1] / 256.0);
              }
          }
      }    
  }
