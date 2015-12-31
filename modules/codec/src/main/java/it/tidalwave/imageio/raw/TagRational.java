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
package it.tidalwave.imageio.raw;

import java.io.Serializable;

/***********************************************************************************************************************
 *
 * This class holds a rational value, modeled by an integer numerator and an
 * integer denominator.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class TagRational extends Number implements Serializable
  {
    private final static long serialVersionUID = 3088068438676854749L;

    private int numerator;

    private int denominator;

    public TagRational (int i, int j)
      {
        numerator = i;
        denominator = j;
      }

    public double doubleValue ()
      {
        return (double)numerator / (double)denominator;
      }

    public float floatValue ()
      {
        return (float)numerator / (float)denominator;
      }

    public final byte byteValue ()
      {
        return (byte)(int)doubleValue();
      }

    public final int intValue ()
      {
        return (int)(doubleValue() + 0.5);
      }

    public final long longValue ()
      {
        return (long)doubleValue();
      }

    public final short shortValue ()
      {
        return (short)(int)doubleValue();
      }

    public final int getDenominator ()
      {
        return denominator;
      }

    public final int getNumerator ()
      {
        return numerator;
      }

    public TagRational reciprocal ()
      {
        return new TagRational(denominator, numerator);
      }

    /*******************************************************************************************************************
     * 
     * @param i
     * @param j
     * 
     *******************************************************************************/
    public void multiply (int i,
                          int j)
      {
        numerator *= i;
        denominator *= j;
      }

    public boolean isInteger ()
      {
        return (denominator == 1) || ((denominator != 0) && ((numerator % denominator) == 0))
            || ((denominator == 0) && (numerator == 0));
      }

    @Override
    public String toString ()
      {
        return numerator + "/" + denominator;
      }

    @Override
    public boolean equals (Object obj)
      {
        if (!(obj instanceof TagRational))
          {
            return false;
          }

        TagRational rational = (TagRational)obj;

        return (numerator == rational.numerator) && (denominator == rational.denominator);
      }

    @Override
    public int hashCode()
      {
        int hash = 5;
        hash = 89 * hash + this.numerator;
        hash = 89 * hash + this.denominator;
        return hash;
      }
  }
