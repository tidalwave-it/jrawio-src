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
 * $Id: Curve.java,v 1.1 2006/02/17 15:31:59 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;


/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: Curve.java,v 1.1 2006/02/17 15:31:59 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class Curve
  {
    private short[] samples;

    private String name;

    /*******************************************************************************
     * 
     * @param gamma
     * @param samplesCount
     * 
     *******************************************************************************/
    public static Curve createGammaCurve (double gamma,
                                          int samplesCount)
      {
        short[] samples = new short[samplesCount];
        int whiteLevel = (1 << 16) - 1;

        for (int i = 0; i < samples.length; i++)
          {
            double r = (double)i / whiteLevel;
            double value = whiteLevel * (r <= 0.018 ? r * gamma : Math.pow(r, gamma) * 1.099 - 0.099);

            if (value > whiteLevel)
              {
                value = whiteLevel;
              }
            
            samples[i] = (short)value;
          }

        return new Curve(samples, "Gamma " + gamma);
      }

    /*******************************************************************************
     * 
     * @param samples
     * @param name
     * 
     *******************************************************************************/
    public Curve (short[] samples, String name)
      {
        this.samples = samples;
        this.name = name;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public short[] getSamples ()
      {
        return samples;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public String getName ()
      {
        return name;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getMaxSample ()
      {
        return (samples[samples.length - 1] & 0xffff);
      }

    /*******************************************************************************
     *
     * Gets an array of sampled data and copy them into a different size array,
     * applying linear interpolation to fit it.
     *
     * @param  data     the original data
     * @param  newData  the array to fill
     *
     ******************************************************************************/
    public Curve resize (int newSamplesCount)
      {
        if ((samples == null) || (samples.length == newSamplesCount))
          {
            return this;
          }
        
        short[] newSamples = new short[newSamplesCount];
        double xScale = (double)(samples.length - 1) / (double)(newSamples.length - 1);

        for (int i = 0; i < newSamples.length; i++)
          {
            double x = xScale * i;

            int x0 = (int)Math.floor(x);
            int x1 = x0 + 1;
            double delta = x - x0;
            int y0 = (samples[x0] & 0xffff);
            int y1 = (x1 < samples.length) ? (samples[x1] & 0xffff) : y0;

            newSamples[i] = (short)(y0 + (int)Math.round((y1 - y0) * delta));
          }

        return new Curve(newSamples, name + " interpolated");
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String toString ()
      {
        if (samples == null)
          {
            return "Curve[name: " + name + ", empty]";
          }

        return "Curve[name: " + name + ", sample count: " + samples.length + ", max: " + getMaxSample() + "]";
      }
  }
