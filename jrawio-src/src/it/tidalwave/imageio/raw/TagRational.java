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
 * $Id: TagRational.java,v 1.2 2006/02/08 20:19:07 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.io.Serializable;

/*******************************************************************************
 *
 * This class holds a rational value, modeled by an integer numerator and an
 * integer denominator.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: TagRational.java,v 1.2 2006/02/08 20:19:07 fabriziogiudici Exp $
 *
 ******************************************************************************/
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

    /*******************************************************************************
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

    public String toString ()
      {
        return numerator + "/" + denominator;
      }

    public boolean equals (Object obj)
      {
        if (!(obj instanceof TagRational))
          {
            return false;
          }

        TagRational rational = (TagRational)obj;

        return (numerator == rational.numerator) && (denominator == rational.denominator);
      }
  }
