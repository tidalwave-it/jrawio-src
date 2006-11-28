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
 * $Id: SRFProcessor.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.srf;

import java.util.List;
import it.tidalwave.imageio.rawprocessor.RAWProcessor;
import it.tidalwave.imageio.rawprocessor.raw.ColorProfileOperation;
import it.tidalwave.imageio.rawprocessor.raw.DemosaicOperation;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: SRFProcessor.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class SRFProcessor extends RAWProcessor
  {
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected void buildPipeline (List operationList)
      {
//        operationList.add(new NEFWhiteBalanceOperation());
//        operationList.add(new NEFExposureOperation());
//        operationList.add(new ComputeBlackLevelsOperation());
        operationList.add(new CurveOperation());
        operationList.add(new DemosaicOperation());
//        operationList.add(new NEFSizeOperation());
        operationList.add(new ColorProfileOperation());
//        operationList.add(new SharpenOperation());
      }
  }
