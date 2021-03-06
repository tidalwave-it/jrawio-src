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
package it.tidalwave.imageio.rawprocessor.crw;

import java.awt.Dimension;
import it.tidalwave.imageio.util.Logger;
import java.awt.Insets;
import it.tidalwave.imageio.crw.CRWMetadata;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(CRWSizeOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected Insets getCrop (PipelineArtifact artifact)
      {
        logger.fine("getCrop()");
        CRWMetadata metadata = (CRWMetadata)artifact.getRAWMetadata();
        int l = metadata.getSensorLeftBorder();
        int t = metadata.getSensorTopBorder();
        int r = metadata.getSensorWidth() - metadata.getSensorRightBorder() - 1;
        int b = metadata.getSensorHeight() - metadata.getSensorBottomBorder() - 1;
        Insets crop = new Insets(t, l, b, r);        
        logger.fine(">>>> returning: %s", crop);
        
        return crop;
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected Dimension getSize (PipelineArtifact artifact)
      {
        logger.fine("getSize()");
        CRWMetadata crwMetadata = (CRWMetadata)artifact.getRAWMetadata();
        Dimension dimension = new Dimension(crwMetadata.getImageWidth(), crwMetadata.getImageHeight());
        logger.fine(">>>> returning: %s", dimension);
        return dimension;
      }
  }
