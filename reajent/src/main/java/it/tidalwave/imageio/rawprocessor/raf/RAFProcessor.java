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
 * $Id: PEFProcessor.java 96 2008-08-24 14:51:54Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raf;

import javax.annotation.Nonnull;
import java.util.List;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWProcessor;
import it.tidalwave.imageio.rawprocessor.raw.ColorProfileOperation;
import it.tidalwave.imageio.rawprocessor.raw.DemosaicOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFProcessor.java 96 2008-08-24 14:51:54Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFProcessor extends RAWProcessor
  {
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    protected void buildPipeline (@Nonnull final List<OperationSupport> operationList)
      {
        operationList.add(new RAFWhiteBalanceOperation());
//        operationList.add(new RAFExposureOperation());
        operationList.add(new RAFCurveOperation());
        operationList.add(new RAFDemosaicOperation());
        operationList.add(new RAFRotateOperation());
////        operationList.add(new RAFSizeOperation());
        operationList.add(new ColorProfileOperation());
//        operationList.add(new SharpenOperation());
      }
  }