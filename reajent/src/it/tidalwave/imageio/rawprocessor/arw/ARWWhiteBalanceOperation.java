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
 * $Id: MRWWhiteBalanceOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.arw;

import java.util.logging.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import it.tidalwave.imageio.arw.ARWMetadata;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWWhiteBalanceOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(ARWWhiteBalanceOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        final ARWMetadata metadata = (ARWMetadata)image.getRAWMetadata();
        final WBG wbg = metadata.getMinoltaRawData().getWBG();
        image.multiplyRedCoefficient(wbg.getRedCoefficient().doubleValue());
        image.multiplyGreenCoefficient(wbg.getGreen1Coefficient().doubleValue());
        image.multiplyBlueCoefficient(wbg.getBlueCoefficient().doubleValue());
      }    
  }
