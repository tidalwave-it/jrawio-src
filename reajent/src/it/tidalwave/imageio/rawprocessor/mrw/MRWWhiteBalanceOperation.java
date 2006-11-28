/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: MRWWhiteBalanceOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.mrw;

import it.tidalwave.imageio.mrw.MRWHeaderProcessor;
import java.util.logging.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWWhiteBalanceOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(MRWWhiteBalanceOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        MRWHeaderProcessor mrwHeaderProcessor = (MRWHeaderProcessor)image.getRAWMetadata().getHeaderProcessor();
        double[] coefficients = mrwHeaderProcessor.getCoefficients();
        image.multiplyRedCoefficient(coefficients[0]);
        image.multiplyGreenCoefficient(coefficients[1]);
        image.multiplyBlueCoefficient(coefficients[2]);
      }    
  }
