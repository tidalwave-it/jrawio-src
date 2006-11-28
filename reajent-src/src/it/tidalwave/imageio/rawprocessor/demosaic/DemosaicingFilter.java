/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: DemosaicingFilter.java,v 1.1 2006/02/17 15:32:03 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.Raster;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DemosaicingFilter.java,v 1.1 2006/02/17 15:32:03 fabriziogiudici Exp $
 *
 ******************************************************************************/
public abstract class DemosaicingFilter
  {
    private final static int SCALE = 1 << 10;

    protected int redCoefficient = SCALE;

    protected int greenCoefficient = SCALE;

    protected int blueCoefficient = SCALE;

    private int blackLevel; 
    
    private short[] curve;

    protected static final int SHORT_MASK = 0xffff;

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setRedCoefficient (double red)
      {
        redCoefficient = (int)Math.round(red * SCALE);
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setGreenCoefficient (double green)
      {
        greenCoefficient = (int)Math.round(green * SCALE);
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setBlueCoefficient (double blue)
      {
        blueCoefficient = (int)Math.round(blue * SCALE);
      }
    
    /*******************************************************************************
     * 
     * @param blackLevel
     * 
     *******************************************************************************/
    public void setBlackLevel (int blackLevel)
      {
        this.blackLevel = blackLevel;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setCurve (short[] curve)
      {
        assert (curve == null) || (curve.length == (1 << 16)); 
        this.curve = curve;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected short applyCoefficientAndCurve (int coefficient,
                                              int value)
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

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public abstract void filter (Raster raster,
                                 BayerInfo bayerInfo);
  }
