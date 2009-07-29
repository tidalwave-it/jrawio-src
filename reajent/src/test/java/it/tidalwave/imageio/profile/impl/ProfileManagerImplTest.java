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

import it.tidalwave.imageio.profile.Profile;
import it.tidalwave.imageio.profile.ProfileManager;
import it.tidalwave.imageio.profile.WhiteBalanceOp;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.raw.RawImageReadParam;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
      throws ProfileManager.NotFoundException, Profile.NotFoundException
      {
        final ProfileManager profileManager = ProfileManager.getInstance();
        final Profile profile = profileManager.findProfileById("dcraw").createModifiableCopy();
        profile.getOperation(WhiteBalanceOp.class).setTemperature(5500);
        final ImageReader reader = ImageIO.getImageReadersByFormatName("NEF").next();
        reader.read(0, new RawImageReadParam(profile));
      }
  }
