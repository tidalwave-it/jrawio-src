/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: Curve.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;


/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: Curve.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
