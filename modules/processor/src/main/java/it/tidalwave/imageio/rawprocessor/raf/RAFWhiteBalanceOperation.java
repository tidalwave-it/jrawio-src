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
package it.tidalwave.imageio.rawprocessor.raf;

import it.tidalwave.imageio.raf.FujiRawData;
import it.tidalwave.imageio.raf.FujiTable1;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.raf.RAFMetadata;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(RAFWhiteBalanceOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (final PipelineArtifact artifact)
      {
        logger.fine("process()");
        final RAFMetadata rafMetadata = (RAFMetadata)artifact.getRAWMetadata();
        final FujiRawData fujiRawData = rafMetadata.getFujiRawData();
        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        final short[] coefficients = fujiTable1.getCoefficients();
        artifact.multiplyRedCoefficient(coefficients[1] / 256.0);
        artifact.multiplyGreenCoefficient(coefficients[0] / 256.0);
        artifact.multiplyBlueCoefficient(coefficients[3] / 256.0);
//        for (int i = 0; i < 4; i++)
//          {
//            image.multiplyCFACoefficient(i, coefficients[i]);
//          }
      }    
  }
