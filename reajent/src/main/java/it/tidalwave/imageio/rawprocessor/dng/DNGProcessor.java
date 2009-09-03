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
package it.tidalwave.imageio.rawprocessor.dng;

import java.util.List;
import it.tidalwave.imageio.rawprocessor.RAWProcessor;
import it.tidalwave.imageio.rawprocessor.craw.RotateOperation;
import it.tidalwave.imageio.rawprocessor.craw.ColorProfileOperation;
import it.tidalwave.imageio.rawprocessor.craw.DemosaicOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGProcessor extends RAWProcessor
  {
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected void buildPipeline (List operationList)
      {
        operationList.add(new DNGWhiteBalanceOperation());
//        operationList.add(new ExposureOperation());
        operationList.add(new DNGCurveOperation());
        operationList.add(new DemosaicOperation());
        operationList.add(new RotateOperation());
        operationList.add(new DNGSizeOperation());
        operationList.add(new DNGColorConversionOperation());
        operationList.add(new ColorProfileOperation());
//        operationList.add(new SharpenOperation());
      }
  }
