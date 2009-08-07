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
package it.tidalwave.imageio.rawprocessor.arw;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.rawprocessor.RAWImage;
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

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (final @Nonnull RAWImage image)
      {
        logger.fine("getCrop(%s)", image);
        final Dimension newSize = getSize(image);
        final BufferedImage bufferedImage = image.getImage();
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        // FIXME: I'm not sure the crop must be centered
        final Insets crop = new Insets((height - newSize.height) / 2,
                                       (width - newSize.width) / 2,
                                       (height - newSize.height) / 2,
                                       (width - newSize.width) / 2);
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
    protected Dimension getSize (final @Nonnull RAWImage image)
      {
        logger.fine("getSize(%s)", image);
        final ARWMetadata metadata = (ARWMetadata)image.getRAWMetadata();
        final PRD prd = metadata.getMinoltaRawData().getPRD();
        final Dimension size = prd.getImageSize();
        logger.finer(">>>> returning %s", size);

        return size;
      }
  }
