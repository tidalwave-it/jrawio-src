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
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2Metadata extends TIFFMetadataSupport
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public CR2Metadata (final @Nonnull Directory primaryIFD,
                        final @Nonnull RAWImageInputStream iis,
                        final @Nonnull HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    public CanonCR2MakerNote getCanonMakerNote()
      {
        return (CanonCR2MakerNote)getMakerNote();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize()
      {
        final CR2SensorInfo sensorInfo = getCanonMakerNote().getSensorInfo();
        return new Dimension(sensorInfo.getWidth(), sensorInfo.getHeight());
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isRasterIFD (final @Nonnull IFD ifd)
      {
        return ifd.isCanon50648Available();
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isThumbnailIFD (final @Nonnull IFD ifd)
      {
        return !ifd.isCanon50648Available();
      }
  }
