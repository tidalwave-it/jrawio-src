/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
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
 * $Id: TIFFTag.java 125 2008-08-25 01:05:22Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.AbstractTag;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.raw.TagRegistry;

/*******************************************************************************
 * 
 * This class represents a TIFF tag and is able to read from an IFD block.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: TIFFTag.java 125 2008-08-25 01:05:22Z fabriziogiudici $
 *
 ******************************************************************************/
public class TIFFTag extends AbstractTag
  {
    private final static String CLASS = TIFFTag.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 6025163135754787912L;

    public static final short TYPE_BYTE = 1;

    public static final short TYPE_ASCII = 2;

    public static final short TYPE_SHORT = 3;

    public static final short TYPE_LONG = 4;

    public static final short TYPE_RATIONAL = 5;

    public static final short TYPE_SBYTE = 6;

    public static final short TYPE_UNDEFINED = 7;

    public static final short TYPE_SSHORT = 8;

    public static final short TYPE_SLONG = 9;

    public static final short TYPE_SRATIONAL = 10;

    public static final short TYPE_FLOAT = 11;

    public static final short TYPE_DOUBLE = 12;

    public static final short TYPE_ORF_13 = 13;

    /** This array maps type codes to type descriptions. */
    private static final String[] typeToString = new String[TYPE_ORF_13 + 1];

    static
      {
        typeToString[0] = "0"; // This is used by NEF MakerNote fields. FIXME: check
        typeToString[TYPE_BYTE] = "byte";
        typeToString[TYPE_ASCII] = "ascii";
        typeToString[TYPE_SHORT] = "short";
        typeToString[TYPE_LONG] = "long";
        typeToString[TYPE_RATIONAL] = "rational";
        typeToString[TYPE_SBYTE] = "signed byte";
        typeToString[TYPE_UNDEFINED] = "undefined";
        typeToString[TYPE_SSHORT] = "signed short";
        typeToString[TYPE_SLONG] = "signed long";
        typeToString[TYPE_SRATIONAL] = "signed rational";
        typeToString[TYPE_FLOAT] = "signed float";
        typeToString[TYPE_DOUBLE] = "double"; 
        typeToString[TYPE_ORF_13] = "orf13"; // used in the Olympus ORF makernote
      }
    
    private long valueOffset;

    /*******************************************************************************
     * 
     * Creates an <code>TIFFTag</code> in a registry given s numeric code.
     * 
     * @param  registry  the registry this tag belongs to
     * @param  code      the code
     * 
     *******************************************************************************/
    public TIFFTag (TagRegistry registry, int code)
      {
        super(registry, code);
      }

    public int getValueOffset() 
      {
        return (int)valueOffset;
      }
    
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     * This method is overridden to support all TIFF specific types.
     * 
     *******************************************************************************/
    @Override
    @CheckForNull
    public Object getValue ()
      {
        switch (type)
          {
            case TYPE_SBYTE:
            case TYPE_BYTE:
              if (intValue.length == 1)
                {
                  return new Byte((byte)intValue[0]);
                }

              byte[] bytes = new byte[intValue.length];

              for (int i = 0; i < bytes.length; i++)
                {
                  bytes[i] = (byte)intValue[i];
                }

              return bytes;

            case TYPE_SSHORT:
            case TYPE_SHORT:
              if (intValue.length == 1)
                {
                  return new Short((short)intValue[0]);
                }

              short[] shorts = new short[intValue.length];

              for (int i = 0; i < shorts.length; i++)
                {
                  shorts[i] = (short)intValue[i];
                }

              return shorts;

            case 0: // This is used by NEF MakerNote fields. FIXME: check
            case TYPE_SLONG:
            case TYPE_LONG:
            case TYPE_ORF_13:
              return (intValue.length == 1) ? new Integer(intValue[0]) : (Object)intValue;

            case TYPE_ASCII:
              return asciiValue;

            case TYPE_SRATIONAL:
            case TYPE_RATIONAL:
              return (rationalValue.length == 1) ? rationalValue[0] : (Object)rationalValue;

            case TYPE_UNDEFINED:
              return undefinedValue;

            default:
              throw new RuntimeException("Unsupported type:" + type);
          }
      }

    /*******************************************************************************
     *
     * Reads this tag from a stream according to the TIFF format.
     * 
     * @param  iis         the input stream
     * @throws IOException if an I/O error ocfurs
     * 
     ******************************************************************************/
    public void read (ImageInputStream iis) throws IOException
      {
        type = iis.readUnsignedShort();
        valuesCount = iis.readInt();
        //logger.finest(">>>> tag: " + code + ", type: " + type + ", valuesCount: " + valuesCount);

        //
        // The Maker note could be coded as an "embedded TIFF" within the file.
        // So it's better to know its offset so you can read with the same classes.
        //
        if (code == 37500) // FIXME  Maker Note
          {
            type = TYPE_LONG;
            valuesCount = 1;
          }

        switch (type)
          {
            case TYPE_SBYTE:
            case TYPE_BYTE:
              intValue = readByteValues(iis, valuesCount);

              if (type == TYPE_BYTE)
                {
                  for (int i = 0; i < intValue.length; i++)
                    {
                      intValue[i] &= 0xff;
                    }
                }

              break;

            case TYPE_SSHORT:
            case TYPE_SHORT:
              intValue = readShortValues(iis, valuesCount);

              if (type == TYPE_SHORT)
                {
                  for (int i = 0; i < intValue.length; i++)
                    {
                      intValue[i] &= 0xffff;
                    }
                }

              rationalValue = new TagRational[intValue.length];

              for (int i = 0; i < intValue.length; i++)
                {
                  rationalValue[i] = new TagRational(intValue[i], 1);
                }

              break;

            case 0: // This is used by NEF MakerNote fields. FIXME: check
            case TYPE_SLONG:
            case TYPE_LONG:
            case TYPE_ORF_13:
              intValue = readIntValues(iis, valuesCount); // FIXME: these are long!
              break;

            case TYPE_ASCII:
              asciiValue = readASCIIValue(iis, valuesCount);
              break;

            case TYPE_RATIONAL:
              rationalValue = readRationalValues(iis, valuesCount);
              break;

            case TYPE_UNDEFINED:
              undefinedValue = readUndefinedValues(iis, valuesCount);
              break;

            case TYPE_SRATIONAL:
              rationalValue = readRationalValues(iis, valuesCount);
              break;

            case TYPE_FLOAT:
              logger.warning("WARNING: TIFF type not implemented [FLOAT]: " + type); // TODO
              iis.readUnsignedInt();
              break;

            case TYPE_DOUBLE:
              logger.warning("WARNING: TIFF type not implemented [DOUBLE]: " + type); // TODO
              iis.readUnsignedInt();
              break;

            default:
              logger.warning("WARNING: TIFF type unknown: " + type);
              iis.readUnsignedInt();
              break;
          }
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    @Override
    public boolean equals (Object o)
      {
        if (!(o instanceof TIFFTag))
          {
            return false;
          }

        TIFFTag t = (TIFFTag)o;

        if ((code != t.code) || (type != t.type) || (valuesCount != t.valuesCount))
          {
            return false;
          }

        if (intValue != null)
          {
            for (int i = 0; i < intValue.length; i++)
              {
                if (intValue[i] != t.intValue[i])
                  {
                    return false;
                  }
              }
          }

        else if (rationalValue != null)
          {
            for (int i = 0; i < rationalValue.length; i++)
              {
                if (!rationalValue[i].equals(t.rationalValue[i]))
                  {
                    return false;
                  }
              }
          }

        else if (asciiValue != null)
          {
            return asciiValue.equals(t.asciiValue);
          }

        else if (undefinedValue != null)
          {
            for (int i = 0; i < undefinedValue.length; i++)
              {
                if (undefinedValue[i] != t.undefinedValue[i])
                  {
                    return false;
                  }
              }
          }

        else
          {
            throw new RuntimeException();
          }

        return true;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        String name = registry.getTagName(code);

        if (name == null)
          {
            name = "#" + code;
          }

        final StringBuilder buffer = new StringBuilder(name);
        buffer.append(" type: ");
        buffer.append((type < typeToString.length) ? typeToString[type] : ("unknown type " + type));

        if (valuesCount != 1)
          {
            buffer.append("[" + valuesCount + "]");
          }

        buffer.append(" value: ");
        appendValues(buffer);

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * Reads an ASCII value.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     *******************************************************************************/
    private String readASCIIValue (ImageInputStream iis,
                                   int valueCount) throws IOException
      {
        StringBuffer buffer = new StringBuffer();
        boolean needReset = false;

        if (valueCount > 4)
          {
            needReset = true;

            long valueOffset = iis.readUnsignedInt();
            iis.mark();
            iis.seek(valueOffset);
          }

        int i = valueCount;

        while (i-- > 0)
          {
            char ch = (char)iis.readByte();

            if ((ch == 0) && (i == 0))
              {
                break;
              }

            buffer.append(ch);
          }

        if (needReset)
          {
            iis.reset();
          }

        else
          {
            iis.skipBytes(4 - valuesCount);
          }

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * Reads rational values.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     *******************************************************************************/
    private TagRational[] readRationalValues (ImageInputStream iis,
                                              int valuesCount) throws IOException
      {
        long valueOffset = iis.readUnsignedInt();
        TagRational[] buffer = new TagRational[valuesCount];
        iis.mark();
        iis.seek(valueOffset);

        for (int i = 0; i < valuesCount; i++)
          {
            int n = iis.readInt();
            int d = iis.readInt();
            buffer[i] = new TagRational(n, d);
          }

        iis.reset();

        return buffer;
      }

    /*******************************************************************************
     * 
     * Reads integer values.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     *******************************************************************************/
    private int[] readIntValues (ImageInputStream iis,
                                 int valuesCount) throws IOException
      {
        long valueOffset = iis.readUnsignedInt();

        if (valuesCount == 1)
          {
            return new int[] { (int)valueOffset };
          }

        return readIntValues(iis, valueOffset, valuesCount);
      }

    /*******************************************************************************
     * 
     * Reads short values.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     *******************************************************************************/
    private int[] readShortValues (ImageInputStream iis,
                                   int valuesCount) throws IOException
      {
        if (valuesCount <= 2)
          {
            int[] buffer = new int[valuesCount];

            for (int i = 0; i < valuesCount; i++)
              {
                buffer[i] = iis.readShort();
              }

            iis.skipBytes(4 - (valuesCount * 2));

            return buffer;
          }

        else
          {
            long valueOffset = iis.readUnsignedInt();
            return readShortValues(iis, valueOffset, valuesCount);
          }
      }

    /*******************************************************************************
     * 
     * Reads byte values.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     *******************************************************************************/
    private int[] readByteValues (ImageInputStream iis,
                                  int valuesCount) throws IOException
      {
        if (valuesCount <= 4)
          {
            int[] buffer = new int[valuesCount];

            for (int i = 0; i < valuesCount; i++)
              {
                buffer[i] = iis.readByte();
              }

            iis.skipBytes(4 - valuesCount);
            return buffer;
          }

        else
          {
            long valueOffset = iis.readUnsignedInt();
            return readByteValues(iis, valueOffset, valuesCount);
          }
      }

    /*******************************************************************************
     * 
     * Reads byte values.
     * 
     * @param iis            the input stream
     * @param valueCount     the count of values to read
     * @return               the value
     * @throws IOException   if an I/O error occurs
     * 
     * FIXME: can be merged with readByteValues (?)
     * 
     *******************************************************************************/
    private byte[] readUndefinedValues (ImageInputStream iis,
                                        int valuesCount) throws IOException
      {
        byte[] buffer = new byte[valuesCount];

        if (valuesCount <= 4)
          {
            for (int i = 0; i < valuesCount; i++)
              {
                buffer[i] = iis.readByte();
              }

            iis.skipBytes(4 - valuesCount);
          }

        else
          {
            valueOffset = iis.readUnsignedInt();
            iis.mark();
            iis.seek(valueOffset);
            iis.read(buffer);
            iis.reset();
          }

        return buffer;
      }
  }
