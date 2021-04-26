/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.arw;

import it.tidalwave.imageio.raw.Source.Type;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import it.tidalwave.imageio.arw.ARWMetadata;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(ARWWhiteBalanceOperation.class);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public ARWWhiteBalanceOperation()
      {
        super(Type.RAW);
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (PipelineArtifact artifact)
      {
        logger.fine("process()");
        final ARWMetadata metadata = (ARWMetadata)artifact.getRAWMetadata();
        final WBG wbg = metadata.getMinoltaRawData().getWBG();
        if (wbg == null)
        {
            logger.fine("no PRD values for white balance operation");
            return;
        }
        artifact.multiplyRedCoefficient(wbg.getRedCoefficient().doubleValue());
        artifact.multiplyGreenCoefficient(wbg.getGreen1Coefficient().doubleValue());
        artifact.multiplyBlueCoefficient(wbg.getBlueCoefficient().doubleValue());
      }    
  }
