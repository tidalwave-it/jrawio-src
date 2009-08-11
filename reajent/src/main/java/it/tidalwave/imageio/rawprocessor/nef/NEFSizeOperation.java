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
import it.tidalwave.imageio.util.Logger;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(NEFSizeOperation.class);

    @Nonnull
    private Insets crop;

    @Nonnull
    private Dimension size;

    /*******************************************************************************************************************
     *
     * @{@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void init (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("init(%s)", artifact);

        size = super.getSize(artifact);
        crop = super.getCrop(artifact);
        final int rotation = normalizedAngle(artifact.getRotation());
        crop = rotate(crop, rotation);
        logger.finer(">>>> size: %s, rotation: %d, crop: %s", size, rotation, crop);

        final NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
        final NikonCaptureEditorMetadata nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();

        if (nceMetadata == null)
          {
            size = rotate(size, rotation);
          }
        else
          {
            double scale = 0.5;
            Rectangle nceCrop = new Rectangle(0, 0, 0, 0);
            //
            // NCE crop settings are relative to the rotated image
            //
            nceCrop.x = (int)Math.round(nceMetadata.getCropLeft() * scale);
            nceCrop.y = (int)Math.round(nceMetadata.getCropTop() * scale);
            nceCrop.width = (int)Math.round(nceMetadata.getCropWidth() * scale);
            nceCrop.height = (int)Math.round(nceMetadata.getCropHeight() * scale);

            if (metadata.getPrimaryIFD().getModel().trim().equals("NIKON D1X"))
              {
                nceCrop.height /= 2; // ??
              }

            logger.fine(">>>> NCE crop: %s", nceCrop);

            // Some images needs to rotate the NCE crop (e.g. ccw90.nef)
            // others don't (e.g. Nikon_D70s_0001.NEF)
            // FIXME: but we don't know how to guess. See the trick below.
            boolean shouldRotateNCECrop = true;

            if (shouldRotateNCECrop)
              {
                final Rectangle save = (Rectangle)nceCrop.clone();
                nceCrop = rotate(nceCrop, size, rotation);
                //
                // FIXME: trick to recover NCE crop quirks - see above.
                //
                if ((nceCrop.x < 0) || (nceCrop.y < 0))
                  {
                    logger.warning("Bad crop, NCE crop hadn't to be rotated");
                    nceCrop = save;
                  }
              }

            size = rotate(size, rotation);
//            if ((rotation == 90) || (rotation == 270)) // FIXME: refactor into rotateDimension
//              {
//                int tmp = size.width;
//                size.width = size.height;
//                size.height = tmp;
//              }

            logger.fine(">>>> size: %s, NCE crop: %s", size, nceCrop);

            crop.left   += nceCrop.x;
            crop.top    += nceCrop.y;
            crop.right  += size.width - nceCrop.width;
            crop.bottom += size.height - nceCrop.height;

            size.width  = nceCrop.width;
            size.height = nceCrop.height;
          }

        logger.fine(">>>> computed crop: %s, size: %s", crop, size);
      }

    /*******************************************************************************************************************
     *
     * @{@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull 
    protected Insets getCrop (@Nonnull final PipelineArtifact artifact)
      { 
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
        return size;
      }
  }
