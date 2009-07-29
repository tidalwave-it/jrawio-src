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
import it.tidalwave.imageio.profile.Profile;
import it.tidalwave.imageio.profile.ProfileManager;
import it.tidalwave.imageio.profile.CropOp;
import it.tidalwave.imageio.profile.DeNoiseOp;
import it.tidalwave.imageio.profile.ImageDescriptor;
import it.tidalwave.imageio.profile.MarginSetter;
import it.tidalwave.imageio.profile.Margins;
import it.tidalwave.imageio.profile.SourceSelector;
import it.tidalwave.imageio.profile.WhiteBalanceOp;
import java.awt.Dimension;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.raw.RawImageReadParam;
import java.awt.color.ICC_Profile;
import javax.annotation.Nonnull;
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
      // NotFoundException doesnt' speak - add the thing that is not found in the name
      throws ProfileManager.NotFoundException, Profile.NotFoundException, IOException
      {
        final ICC_Profile ADOBE_RGB = null; // FIXME
        
        final ProfileManager profileManager = ProfileManager.getInstance();
        final Profile profile = profileManager.findProfileById("dcraw").createModifiableCopy();
        
        profile.getOperation(WhiteBalanceOp.class).setTemperature(5500);
        profile.getOperation(ColorProfileOp.class).setICCProfile(ADOBE_RGB);
        profile.getOperation(DeNoiseOp.class).setAdaptivePropertiesHandler(new AdaptivePropertiesHandler<DeNoiseOp>()
          {
            @Override
            public void adaptProperties (final @Nonnull DeNoiseOp operation,
                                         final @Nonnull ImageDescriptor imageDescriptor)
              {
                final Dimension dimension = imageDescriptor.getDimension();
                final int width = dimension.width;
                final int height = dimension.height;

                operation.setAlgorithm(((width * height) >= 2 * 1024 * 1024) ? "xxx" : "yyy");
              }
          });

        final Margins margins = Margins.create().withLeft(10).withRight(10).withTop(10).withBottom(10);
        profile.getOperation(CropOp.class).setAdaptivePropertiesHandler(new MarginSetter().setMargins(margins));

        final ImageReader reader = ImageIO.getImageReadersByFormatName("NEF").next();
        reader.read(0, new RawImageReadParam(profile));

        // Always read the RAW_DATA (default behaviour)
        reader.read(0, new RawImageReadParam(SourceSelector.Source.RAW_DATA()));

        // Always read the second thumbnail
        reader.read(0, new RawImageReadParam(SourceSelector.Source.THUMBNAIL(1)));

        // Read the first thumbnail if larger than 6 megapixels, the raw data instead
        reader.read(0, new RawImageReadParam(new SourceSelector()
          {
            @Override
            public Source selectSource (@Nonnull final ImageDescriptor imageDescriptor)
              {
                final Dimension dimension = imageDescriptor.getDimension();
                final int width = dimension.width;
                final int height = dimension.height;

                return ((width * height) >= 6 * 1024 * 1024) ? Source.THUMBNAIL(0) : Source.RAW_DATA();
              }
          }));
      }
  }
