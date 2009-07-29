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

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import it.tidalwave.imageio.profile.ColorProfileOp;
import it.tidalwave.imageio.profile.DeNoiseOp;
import it.tidalwave.imageio.profile.DemosaicOp;
import it.tidalwave.imageio.profile.RotateOp;
import it.tidalwave.imageio.profile.WhiteBalanceOp;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DcrawProfile extends ProfileImpl
  {
    public DcrawProfile()
      {
        super("dcraw", "DCraw Profile", Changeability.READ_ONLY);

        addOperation(WhiteBalanceOp.class).setTemperature(5500); // FIXME
        addOperation(DemosaicOp.class).setAlgorithm("ADR"); // FIXME
        addOperation(ColorProfileOp.class).setICCProfile(ICC_Profile.getInstance(ColorSpace.CS_sRGB)); // FIXME
        addOperation(RotateOp.class).setAngle(0);
        addOperation(DeNoiseOp.class).setAlgorithm("xxx");
      }
  }
