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
package it.tidalwave.imageio.rawprocessor.nef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.raw.ExposureOperation;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFExposureOperation extends ExposureOperation
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.nef.NEFExposureOperation";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (PipelineArtifact artifact)
      {
        logger.fine("process()");
        NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
        NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();
        
        if ((nceMetadata != null) && nceMetadata.isAdvancedRawEnabled())
          {
            double exposureCompensation = nceMetadata.getEVCompensation() / 100.0;
            logger.finer (">>>> NCE exposure compensation: %s", exposureCompensation);
            double coefficient = Math.pow(2, exposureCompensation);
            artifact.multiplyRedCoefficient(coefficient);
            artifact.multiplyGreenCoefficient(coefficient);
            artifact.multiplyBlueCoefficient(coefficient);
          }
      }    
  }
