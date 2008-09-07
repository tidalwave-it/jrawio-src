/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: MRWWhiteBalanceOperation.java 136 2008-09-04 12:56:41Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.arw;

import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.arw.ARWMetadata;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWWhiteBalanceOperation.java 136 2008-09-04 12:56:41Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(ARWSizeOperation.class);

    /*******************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (final @Nonnull RAWImage image)
      {
        final ARWMetadata metadata = (ARWMetadata)image.getRAWMetadata();
        final PRD prd = metadata.getMinoltaRawData().getPRD();
        final BufferedImage bufferedImage = image.getImage();
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final Dimension newSize = prd.getImageSize();
        // FIXME: I'm not sure the crop must be centered
        return new Insets((height - newSize.height) / 2, 
                          (width - newSize.width) / 2, 
                          (height - newSize.height) / 2, 
                          (width - newSize.width) / 2);
      }
  }
