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
 * $Id: PEFWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.pef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.pef.PentaxMakerNote;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class PEFWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(PEFWhiteBalanceOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        PentaxMakerNote pefMakernote = (PentaxMakerNote)image.getRAWMetadata().getMakerNote();
        int[] coefficients = pefMakernote.getWhiteBalanceInfo();
        double scale = 1.0 / 8192.0;
        image.multiplyRedCoefficient(scale * coefficients[0]); 
        image.multiplyGreenCoefficient(scale * coefficients[1]);
        image.multiplyBlueCoefficient(scale * coefficients[3]);
      }    
  }
