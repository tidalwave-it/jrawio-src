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
package it.tidalwave.imageio.rawprocessor.orf;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.orf.ImageProcessing;
import it.tidalwave.imageio.orf.ORFMetadata;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(ORFSizeOperation.class);

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (final @Nonnull PipelineArtifact artifact)
      {
        final BufferedImage bufferedImage = artifact.getImage();
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final Dimension newSize = getSize(artifact);

        Insets crop = NULL_CROP;

        if (newSize == null)
          {
            logger.warning("Can't find a crop");
          }
        else
          {
            // FIXME: I'm not sure the crop must be centered
            final int top = (height - newSize.height) / 2;
            final int left = (width - newSize.width) / 2;
            final int bottom = height - newSize.height - top;
            final int right = width - newSize.width - left;

            crop = new Insets(top, left, bottom, right);
          }

        logger.finer(">>>> returning %s", crop);

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
        logger.fine("getSize(%s)", artifact);

        Dimension size = null;

        final ORFMetadata metadata = (ORFMetadata)artifact.getRAWMetadata();
        final OlympusMakerNote makerNote = metadata.getOlympusMakerNote();

        if (makerNote.isImageWidthAvailable())
          {
            size = new Dimension(makerNote.getImageWidth(), makerNote.getImageHeight());
          }

        if ((size == null) && makerNote.isImageProcessingAvailable())
          {
            final ImageProcessing imageProcessing = makerNote.getOlympusImageProcessing();

            if (imageProcessing.isImageWidthAvailable())
              {
                size = new Dimension(imageProcessing.getImageWidth(), imageProcessing.getImageHeight());
              }
          }

        logger.finer(">>>> returning %s", size);

        return size;
      }
  }
