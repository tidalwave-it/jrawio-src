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
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.profile.impl;

import it.tidalwave.imageio.profile.ColorProfileOp;
import it.tidalwave.imageio.profile.ProcessorOperation.AdaptivePropertiesHandler;
import it.tidalwave.imageio.profile.ProcessorOperation.ImageDescriptor;
import it.tidalwave.imageio.profile.Profile;
import it.tidalwave.imageio.profile.ProfileManager;
import it.tidalwave.imageio.profile.CropOp;
import it.tidalwave.imageio.profile.CropOp.Bounds;
import it.tidalwave.imageio.profile.WhiteBalanceOp;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.raw.RawImageReadParam;
import java.awt.color.ICC_Profile;
import javax.annotation.Nonnull;
import javax.imageio.metadata.IIOMetadata;
import org.junit.Test;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ProfileManagerImplTest
  {
    @Test
    public void testReadWithProfile()
      throws ProfileManager.NotFoundException, Profile.NotFoundException, IOException
      {
        final ICC_Profile ADOBE_RGB = null; // FIXME
        
        final ProfileManager profileManager = ProfileManager.getInstance();
        final Profile profile = profileManager.findProfileById("dcraw").createModifiableCopy();
        profile.getOperation(WhiteBalanceOp.class).setTemperature(5500);
        profile.getOperation(ColorProfileOp.class).setICCProfile(ADOBE_RGB);
        profile.getOperation(CropOp.class).setAdaptivePropertiesHandler(new AdaptivePropertiesHandler<CropOp>()
          {
            @Override
            public void adaptProperties (final @Nonnull CropOp sizeOp,
                                         final @Nonnull ImageDescriptor imageDescriptor)
              {
                final IIOMetadata metadata = imageDescriptor.getMetadata();
                final int width = 0; // FIXME get from metadata
                final int height = 0; // FIXME get from metadata
                final double margin = 10;
                sizeOp.setBounds(Bounds.create().left(margin).top(margin).right(width - margin).bottom(height - margin));
              }
          });

        final ImageReader reader = ImageIO.getImageReadersByFormatName("NEF").next();
        reader.read(0, new RawImageReadParam(profile));
      }
  }
