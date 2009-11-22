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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
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
 * @version $Id$
 *
 **********************************************************************************************************************/
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
    private final String registryName;

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

    /*******************************************************************************************************************
     *
     * Creates an <code>AbstractTag</code> in a registry given s numeric code.
     * 
     * @param  registry  the registry this tag belongs to
     * @param  code      the code
     * 
     ******************************************************************************************************************/
    public AbstractTag (@Nonnull final TagRegistry registry, final int code)
      {
        this.registry = registry;
        this.code = code;
        registryName = registry.getName();
      }

    /*******************************************************************************************************************
     *
     * Returns the numeric code of this tag.
     * 
     * @return  the code
     * 
     ******************************************************************************************************************/
    public int getCode()
      {
        return code;
      }

    /*******************************************************************************************************************
     *
     * Return the type of values contained in this tag.
     * 
     * @return   the type
     * 
     ******************************************************************************************************************/
    public int getType()
      {
        return type;
      }

    /*******************************************************************************************************************
     *
     * Returns the count of values in this tag.
     * 
     * @return  the count of values
     * 
     ******************************************************************************************************************/
    public int getValuesCount()
      {
        return valuesCount;
      }

    /*******************************************************************************************************************
     * 
     * Returns the value(s) contained in this tag as the most pertinent type.
     * 
     * @return  the value
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public Object getValue()
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

    /*******************************************************************************************************************
     *
     * Returns the value as bytes.
     * 
     * @return  the values as bytes
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public byte[] getByteValues()
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

    /*******************************************************************************************************************
     *
     * Returns the value as integers.
     * 
     * @return  the values as integers
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public int[] getIntValues()
      {
        return intValue;
      }

    /*******************************************************************************************************************
     *
     * Returns the value as floats.
     * 
     * @return  the values as floats
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public float[] getFloatValues()
      {
        return floatValue;
      }

    /*******************************************************************************************************************
     *
     * Returns the value as rationals.
     * 
     * @return  the values as rationals
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public TagRational[] getRationalValues ()
      {
        return rationalValue;
      }

    /*******************************************************************************************************************
     *
     * Returns the value as an ASCII string.
     * 
     * @return  the values as an ASCII string
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public String getASCIIValue ()
      {
        return asciiValue;
      }

    /*******************************************************************************************************************
     * 
     * Reads integer values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected int[] readIntValues (@Nonnull final ImageInputStream iis,
                                   final long valueOffset,
                                   @Nonnegative final int valuesCount) 
      throws IOException
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

    /*******************************************************************************************************************
     * 
     * Reads short values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected int[] readShortValues (@Nonnull final ImageInputStream iis,
                                     final long valueOffset,
                                     @Nonnegative final int valuesCount) 
      throws IOException
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

    /*******************************************************************************************************************
     * 
     * Reads byte values from the given image input stream.
     * 
     * @param  iis          the stream to read from
     * @param  valueOffset  the offset to read at
     * @param  valuesCount  the number of values to read
     * @return              the integers
     * @throws IOException  if a I/O error occurs
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected int[] readByteValues (@Nonnull final ImageInputStream iis,
                                    final long valueOffset,
                                    @Nonnegative final int valuesCount) 
      throws IOException
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

    /*******************************************************************************************************************
     * 
     * Appends the contained value(s) to the given <code>StringBuffer</code>. This
     * method is used by {@link #toString()}.
     * 
     * @param buffer  buffer
     * 
     ******************************************************************************************************************/
    protected void appendValues (@Nonnull final StringBuilder buffer)
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

                buffer.append(Integer.toHexString(undefinedValue[j] & 0xff));
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

    /*******************************************************************************************************************
     * 
     * Customized deserialization code. This method restores the link to the registry
     * this tag belongs to.
     * 
     * @param  is
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     ******************************************************************************************************************/
    private void readObject (@Nonnull final ObjectInputStream is) 
      throws IOException, ClassNotFoundException
      {
        is.defaultReadObject();
        registry = TagRegistry.getRegistry(registryName);
      }
  }
