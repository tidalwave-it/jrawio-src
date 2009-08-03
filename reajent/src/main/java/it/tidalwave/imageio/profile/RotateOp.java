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
package it.tidalwave.imageio.profile;

import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class RotateOp extends ProcessorOperation<RotateOp>
  {
    public static final String PROP_ANGLE = "angle";

    protected double angle;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public RotateOp (final @Nonnull Profile profile)
      {
        super(profile);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public double getAngle()
      {
        return angle;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public RotateOp setAngle (final @Nonnull double angle)
      {
        final double oldAngle = this.angle;
        this.angle = angle;
        propertyChangeSupport.firePropertyChange(PROP_ANGLE, oldAngle, angle);

        return this;
      }
  }
