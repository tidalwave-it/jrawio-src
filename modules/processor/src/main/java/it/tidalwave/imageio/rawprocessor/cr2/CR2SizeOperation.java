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
package it.tidalwave.imageio.rawprocessor.cr2;

import java.awt.Dimension;
import javax.annotation.Nonnull;
import java.awt.Insets;
import it.tidalwave.imageio.cr2.CR2Metadata;
import it.tidalwave.imageio.cr2.CR2SensorInfo;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2SizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(CR2SizeOperation.class);
    
    /*******************************************************************************************************************
     *
     * @{@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull 
    protected Insets getCrop (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("getCrop()");
        Insets crop = super.getCrop(artifact);
        final int rotation = normalizedAngle(artifact.getRotation());
//        crop = rotate(crop, rotation);
        logger.finer(">>>> rotation: %d, crop: %s", rotation, crop);

        final CR2Metadata metadata = (CR2Metadata)artifact.getRAWMetadata();
        final CR2SensorInfo sensorInfo = metadata.getCanonMakerNote().getSensorInfo();
        crop.left = sensorInfo.getCropLeft();
        crop.top = sensorInfo.getCropTop();
        crop.right = sensorInfo.getWidth() - 1 - sensorInfo.getCropRight();
        crop.bottom = sensorInfo.getHeight() - 1 - sensorInfo.getCropBottom();
        
        logger.fine(">>>> returning crop: %s", crop);
        
        return crop;
      }

    /*******************************************************************************************************************
     *
     * @{@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("getSize()");
        final CR2Metadata metadata = (CR2Metadata)artifact.getRAWMetadata();
        final CR2SensorInfo sensorInfo = metadata.getCanonMakerNote().getSensorInfo();
        return new Dimension(sensorInfo.getCropRight() - sensorInfo.getCropLeft() + 1,
                             sensorInfo.getCropBottom() - sensorInfo.getCropTop() + 1);
      }
  }
