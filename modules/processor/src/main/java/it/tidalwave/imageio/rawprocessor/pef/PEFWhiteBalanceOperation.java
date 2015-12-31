/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.rawprocessor.pef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.pef.PentaxMakerNote;
import it.tidalwave.imageio.raw.Source.Type;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PEFWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(PEFWhiteBalanceOperation.class);
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public PEFWhiteBalanceOperation()
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
        PentaxMakerNote pefMakernote = (PentaxMakerNote)artifact.getRAWMetadata().getMakerNote();
        int[] coefficients = pefMakernote.getWhiteBalanceInfo();
        double scale = 1.0 / 8192.0;
        artifact.multiplyRedCoefficient(scale * coefficients[0]);
        artifact.multiplyGreenCoefficient(scale * coefficients[1]);
        artifact.multiplyBlueCoefficient(scale * coefficients[3]);
      }    
  }
