/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.Raster;
import javax.annotation.Nullable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class DemosaicingFilter
  {
    private final static int SCALE = 1 << 10;

    protected int redCoefficient = SCALE;

    protected int greenCoefficient = SCALE;

    protected int blueCoefficient = SCALE;

    private int blackLevel; 
    
    private short[] curve;

    protected static final int SHORT_MASK = 0xffff;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setRedCoefficient (double red)
      {
        redCoefficient = (int)Math.round(red * SCALE);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setGreenCoefficient (double green)
      {
        greenCoefficient = (int)Math.round(green * SCALE);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setBlueCoefficient (double blue)
      {
        blueCoefficient = (int)Math.round(blue * SCALE);
      }
    
    /*******************************************************************************************************************
     * 
     * @param blackLevel
     * 
     *******************************************************************************/
    public void setBlackLevel (int blackLevel)
      {
        this.blackLevel = blackLevel;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setCurve (final @Nullable short[] curve)
      {
        assert (curve == null) || (curve.length == (1 << 16)); 
        this.curve = (curve != null) ? curve.clone() : null;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected short applyCoefficientAndCurve (int coefficient, int value)
      {
        value -= blackLevel;
        
        if (value < 0)
          {
            value = 0;
          }
        
        value = (value * coefficient) / SCALE;
        value = (value < SHORT_MASK) ? value : SHORT_MASK;

        if (curve != null)
          {
            value = curve[value] & SHORT_MASK; // FIXME: you should first apply the curve then coefficients
          }

        return (short)value;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public abstract void filter (Raster raster,
                                 BayerInfo bayerInfo);
  }
