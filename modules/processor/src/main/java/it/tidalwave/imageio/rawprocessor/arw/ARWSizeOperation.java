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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.awt.Insets;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.arw.ARWMetadata;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(ARWSizeOperation.class);

    @CheckForNull
    private Insets crop;

    @CheckForNull
    private Dimension size;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void init (final @Nonnull PipelineArtifact artifact)
      throws Exception
      {
        logger.fine("init(%s)", artifact);

        final ARWMetadata metadata = (ARWMetadata)artifact.getRAWMetadata();
        final PRD prd = metadata.getMinoltaRawData().getPRD();
	if (prd == null)
	{
            logger.fine("no PRD values for size operation");
            return;
	}
        size = prd.getImageSize();
        final Dimension sensorSize = prd.getCcdSize();
        // FIXME: I'm not sure the crop must be centered
        crop = new Insets((sensorSize.height - size.height) / 2,
                          (sensorSize.width - size.width) / 2,
                          (sensorSize.height - size.height) / 2,
                          (sensorSize.width - size.width) / 2);

        logger.finer(">>>> sensor size: %s, size: %s, crop: %s", sensorSize, size, crop);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (final @Nonnull PipelineArtifact artifact)
      {
        return crop;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize (final @Nonnull PipelineArtifact artifact)
      {
        return size;
      }
  }
