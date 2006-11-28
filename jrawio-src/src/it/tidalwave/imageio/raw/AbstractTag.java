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
 * $Id: AbstractTag.java,v 1.2 2006/02/08 20:19:08 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * This class represents an abstract tag, which is an aggregate of a numeric code,
 * a type and a value. The value can be an integer, a short, a byte, a float or
 * a rational; can be of any multiplicity. The detailed meaning of the type is
 * not specified and demanded to concrete subclasses.
 * <br>
 * This class provides some protected <code>readXXX()</code> methods to read data 
 * from an image stream.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: AbstractTag.java,v 1.2 2006/02/08 20:19:08 fabriziogiudici Exp $
 *
 ******************************************************************************/
public abstract class AbstractTag implements Serializable
  {
    private final static long serialVersionUID = 2694868438676854749L;

    private final static int MAX_TRAILING_VALUES = 20;

    private final static int MAX_LEADING_VALUES = 40;

    /** The registry this tag belongs to. This is transient so that this class can
     * be serialized without carrying along the whole registry! Upon deserialization
     * the link to the registry must be restored by using the registryName.
     */ 
    protected transient TagRegistry registry;

    /** The name of the registry this tag belongs to. */
    private String registryName;

    /** The numerical tag code. */
    protected int code;

    /** The type of this tag. AbstractTag does not define any dictionary for this
     * field - it leaves this task to concrete subclasses. */
    protected int type;

    /** The number of contained values. */
    protected int valuesCount;

    /** The values as bytes. */
    protected byte[] undefinedValue;

    /** The values as integers. */
    protected int[] intValue;

    /** The values as rationals. */
    protected TagRational[] rationalValue;

    /** The values as floats. */
    protected float[] floatValue;

    /** The values as an ASCII string. */
    protected String asciiValue;

    /*******************************************************************************
     *
     * Creates an <code>AbstractTag</code> in a registry given s numeric code.
     * 
     * @param  registry  the registry this tag belongs to
     * @param  code      the code
     * 
     ******************************************************************************/
    public AbstractTag (TagRegistry registry, int code)
      {
        this.registry = registry;
        this.code = code;
        registryName = registry.getName();
      }

    /*******************************************************************************
     *
     * Returns the numeric code of this tag.
     * 
     * @return  the code
     * 
     ******************************************************************************/
    public int getCode ()
      {
        return code;
      }

    /*******************************************************************************
     *
     * Return the type of values contained in this tag.
     * 
     * @return   the type
     * 
     ******************************************************************************/
    public int getType ()
      {
        return type;
      }

    /*******************************************************************************
     *
     * Returns the count of values in this tag.
     * 
     * @return  the count of values
     * 
     ******************************************************************************/
    public int getValuesCount ()
      {
        return valuesCount;
      }

    /*******************************************************************************
     * 
     * Returns the value(s) contained in this tag as the most pertinent type.
     * 
     * @return  the value
     * 
     *******************************************************************************/
    public Object getValue ()
      {
        if (undefinedValue != null)
          {
            return undefinedValue;
          }

        else if (intValue != null)
          {
            return (intValue.length > 1) ? intValue : (Object)new Integer(intValue[0]);
          }

        else if (rationalValue != null)
          {
            return (rationalValue.length > 1) ? rationalValue : (Object)rationalValue[0];
          }

        else if (asciiValue != null)
          {
            return asciiValue;
          }

        else
          {
            return null;
          }
      }

    /*******************************************************************************
     *
     * Returns the value as bytes.
     * 
     * @return  the values as bytes
     * 
     ******************************************************************************/
    public byte[] getByteValues ()
      {
        //
        // Arrays up to four bytes can be specified as integers and are stored into intValue.
        //
        if ((undefinedValue == null) && (intValue != null) && (intValue.length <= 4))
          {
            undefinedValue = new byte[intValue.length];

            for (int i = 0; i < undefinedValue.length; i++)
              {
                undefinedValue[i] = (byte)intValue[i];
              }
          }

        return undefinedValue;
      }

    /*******************************************************************************
     *
     * Returns the value as integers.
     * 
     * @return  the values as integers
     * 
     ******************************************************************************/
    public int[] getIntValues ()
      {
        return intValue;
      }

    /*******************************************************************************
     *
     * Returns the value as floats.
     * 
     * @return  the values as floats
     * 
     ******************************************************************************/
    public float[] getFloatValues ()
      {
        return floatValue;
      }

    /*******************************************************************************
     *
     * Returns the value as rationals.
     * 
     * @return  the values as rationals
     * 
     ******************************************************************************/
    public TagRational[] getRationalValues ()
      {
        return rationalValue;
      }

    /*******************************************************************************
     *
     * Returns the value as an ASCII string.
     * 
     * @return  the values as an ASCII string
     * 
     ******************************************************************************/
    public String getASCIIValue ()
      {
        return asciiValue;
      }

    /*******************************************************************************
     * 
     * Reads integer values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     *******************************************************************************/
    protected int[] readIntValues (ImageInputStream iis,
                                   long valueOffset,
                                   int valuesCount) throws IOException
      {
        int[] buffer = new int[valuesCount];
        iis.mark();
        iis.seek(valueOffset);

        for (int i = 0; i < valuesCount; i++)
          {
            buffer[i] = iis.readInt();
          }

        iis.reset();

        return buffer;
      }

    /*******************************************************************************
     * 
     * Reads short values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     *******************************************************************************/
    protected int[] readShortValues (ImageInputStream iis,
                                     long valueOffset,
                                     int valuesCount) throws IOException
      {
        int[] buffer = new int[valuesCount];
        iis.mark();
        iis.seek(valueOffset);

        for (int i = 0; i < valuesCount; i++)
          {
            buffer[i] = iis.readShort() & 0xffff;
          }

        iis.reset();

        return buffer;
      }

    /*******************************************************************************
     * 
     * Reads byte values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     *******************************************************************************/
    protected int[] readByteValues (ImageInputStream iis,
                                    long valueOffset,
                                    int valuesCount) throws IOException
      {
        int[] buffer = new int[valuesCount];

        iis.mark();
        iis.seek(valueOffset);

        for (int i = 0; i < valuesCount; i++)
          {
            buffer[i] = iis.readByte();
          }

        iis.reset();
        return buffer;
      }

    /*******************************************************************************
     * 
     * Appends the contained value(s) to the given <code>StringBuffer</code>. This
     * method is used by {@link #toString()}.
     * 
     * @param buffer  buffer
     * 
     *******************************************************************************/
    protected void appendValues (StringBuffer buffer)
      {
        if (undefinedValue != null)
          {
            for (int j = 0; j < undefinedValue.length; j++)
              {
                if (j > 0)
                  {
                    buffer.append(",");
                  }

                if ((j > MAX_LEADING_VALUES) && ((undefinedValue.length - MAX_TRAILING_VALUES) > j))
                  {
                    buffer.append(" ... ,");
                    j = undefinedValue.length - MAX_TRAILING_VALUES;
                  }

                buffer.append(undefinedValue[j]);
              }
          }

        else if (intValue != null)
          {
            for (int j = 0; j < intValue.length; j++)
              {
                if (j > 0)
                  {
                    buffer.append(",");
                  }

                if ((j > MAX_LEADING_VALUES) && ((intValue.length - MAX_TRAILING_VALUES) > j))
                  {
                    buffer.append(" ... ,");
                    j = intValue.length - MAX_TRAILING_VALUES;
                  }

                buffer.append(intValue[j]);
              }
          }

        else if (rationalValue != null)
          {
            for (int j = 0; j < rationalValue.length; j++)
              {
                if (j > 0)
                  {
                    buffer.append(",");
                  }

                if ((j > MAX_LEADING_VALUES) && ((rationalValue.length - MAX_TRAILING_VALUES) > j))
                  {
                    buffer.append(" ... ,");
                    j = rationalValue.length - MAX_TRAILING_VALUES;
                  }

                buffer.append(rationalValue[j]);
              }
          }

        else if (asciiValue != null)
          {
            buffer.append(asciiValue);
          }

        else
          {
            buffer.append("????");
          }
      }

    /*******************************************************************************
     * 
     * Customized deserialization code. This method restores the link to the registry
     * this tag belongs to.
     * 
     * @param  is
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     ******************************************************************************/
    private void readObject (ObjectInputStream is) throws IOException, ClassNotFoundException
      {
        is.defaultReadObject();
        registry = TagRegistry.getRegistry(registryName);
      }
  }
