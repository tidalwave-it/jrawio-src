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
package it.tidalwave.imageio.rawprocessor.nef;

import javax.annotation.Nonnull;
import java.util.List;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.RAWProcessor;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.raw.ColorProfileOperation;
import it.tidalwave.imageio.rawprocessor.raw.DemosaicOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFProcessor extends RAWProcessor
  {
    private final static String CLASS = NEFProcessor.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
        
    class BindNCEMetadata extends OperationSupport
      {
        @Override
        public void init (@Nonnull final PipelineArtifact artifact)
          throws Exception
          {
            final NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
            final NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
            
            if (makerNote.isCaptureEditorDataAvailable())
              {
                final NikonCaptureEditorMetadata nceMetadata = new NikonCaptureEditorMetadata(makerNote.getCaptureEditorData());
                logger.finer(nceMetadata.toString());
                metadata._setCaptureEditorMetadata(nceMetadata); 
              }            
          }        

        public void process (@Nonnull final PipelineArtifact artifact)
          throws Exception
          {
          }        
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    protected void buildPipeline (@Nonnull final List<OperationSupport> operationList)
      {
        operationList.add(new BindNCEMetadata());
        operationList.add(new NEFWhiteBalanceOperation());
        operationList.add(new NEFExposureOperation());
        operationList.add(new NEFCurveOperation());
        operationList.add(new DemosaicOperation());
        operationList.add(new NEFRotateOperation());
        operationList.add(new NEFSizeOperation());
        operationList.add(new NEFColorConversionOperation());
        operationList.add(new ColorProfileOperation());
//        operationList.add(new SharpenOperation());
      }
  }
