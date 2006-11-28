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
 * $Id: CIFFTag.java,v 1.2 2006/02/08 19:49:01 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.crw;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

import it.tidalwave.imageio.raw.AbstractTag;
import it.tidalwave.imageio.raw.TagRegistry;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CIFFTag.java,v 1.2 2006/02/08 19:49:01 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class CIFFTag extends AbstractTag
  {
    private final static long serialVersionUID = -7884292603248658254L;

    private int dataLocation;

    private int baseOffset;

    private int offset;

    private int size;

    private final static  int TYPE_SHIFT_COUNT = 11;

    private final static int CODE_MASK = 0x3fff;

    private final static int TYPE_MASK = 0x3800;

    private final static int DATA_LOCATION_MASK = 0xc000;

    private final static int TYPE_BYTE = 0x0000;

    private final static int TYPE_ASCII = 0x0800;

    private final static int TYPE_SHORT = 0x1000;

    private final static int TYPE_LONG = 0x1800;

    private final static int TYPE_MIX = 0x2000;

    private final static int TYPE_SUBDIR1 = 0x2800;

    private final static int TYPE_SUBDIR2 = 0x3000;

    private final static int TYPE_UNKNOWN = 0x3800;

    private final static  String[] typeToString = new String[(TYPE_UNKNOWN >>> TYPE_SHIFT_COUNT) + 1];

    static
      {
        typeToString[TYPE_BYTE >>> TYPE_SHIFT_COUNT] = "byte";
        typeToString[TYPE_ASCII >>>TYPE_SHIFT_COUNT] = "ascii";
        typeToString[TYPE_SHORT >>> TYPE_SHIFT_COUNT] = "short";
        typeToString[TYPE_LONG >>> TYPE_SHIFT_COUNT] = "long";
        typeToString[TYPE_MIX >>> TYPE_SHIFT_COUNT] = "mix";
        typeToString[TYPE_SUBDIR1 >>> TYPE_SHIFT_COUNT] = "subdirectory";
        typeToString[TYPE_SUBDIR2 >>> TYPE_SHIFT_COUNT] = "subdirectory";
        typeToString[TYPE_UNKNOWN >>> TYPE_SHIFT_COUNT] = "unknown";
      }

    /*******************************************************************************
     * 
     * @param registry
     * @param code
     * 
     *******************************************************************************/
    public CIFFTag (TagRegistry registry, int code, int baseOffset)
      {
        super(registry, code);
        this.baseOffset = baseOffset;
      }

    /*******************************************************************************
     * 
     * Returns the size of this tag
     * 
     * @return  the size
     * 
     *******************************************************************************/
    public int getSize ()
      {
        return size;
      }

    /*******************************************************************************
     * 
     * Returns the offset of this tag.
     * 
     * @return  the offset
     * 
     *******************************************************************************/
    public int getOffset ()
      {
        return offset;
      }

    /*******************************************************************************
     * 
     * Returns the base offset of this tag
     * 
     * @return  the base offset
     * 
     *******************************************************************************/
    public int getBaseOffset ()
      {
        return baseOffset;
      }

    /*******************************************************************************
     * 
     * Return true if this tag is a sub-directory.
     * 
     * @return  true if it's a subdirectory
     * 
     *******************************************************************************/
    public boolean isSubDirectory ()
      {
        return (type == TYPE_SUBDIR1) || (type == TYPE_SUBDIR2);
      }

    /*******************************************************************************
     * 
     * Reads this tag from a stream starting at the current position.
     * 
     * @param  iis          the image input stream
     * @throws IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    public void read (ImageInputStream iis) throws IOException
      {
        int savePos = (int)iis.getStreamPosition();
        size = iis.readInt();
        offset = iis.readInt();
        dataLocation = code & DATA_LOCATION_MASK;
        type = code & TYPE_MASK;
        code = code & CODE_MASK;

        if (dataLocation == 0x4000)
          {
            size = offset = 0;

            switch (type)
              {
                case TYPE_BYTE:
                  intValue = readByteValues(iis, savePos, 8);
                  valuesCount = intValue.length;
                  undefinedValue = new byte[intValue.length];

                  for (int i = 0; i < undefinedValue.length; i++)
                    {
                      undefinedValue[i] = (byte)intValue[i];
                    }

                  break;

                case TYPE_SHORT:
                  intValue = readShortValues(iis, savePos, 4);
                  valuesCount = intValue.length;
                  break;

                case TYPE_LONG:
                  intValue = readIntValues(iis, savePos, 2);
                  valuesCount = intValue.length;
                  break;
              }
          }

        else
          {
            switch (type)
              {
                case TYPE_BYTE:
                  intValue = readByteValues(iis, offset + baseOffset, size);
                  valuesCount = intValue.length;
                  undefinedValue = new byte[intValue.length];

                  for (int i = 0; i < undefinedValue.length; i++)
                    {
                      undefinedValue[i] = (byte)intValue[i];
                    }

                  break;

                case TYPE_SHORT:
                  intValue = readShortValues(iis, offset + baseOffset, size / 2);
                  valuesCount = intValue.length;
                  break;

                case TYPE_LONG:
                  intValue = readIntValues(iis, offset + baseOffset, size / 4);
                  valuesCount = intValue.length;
                  floatValue = new float[valuesCount];

                  for (int i = 0; i < valuesCount; i++)
                    {
                      floatValue[i] = Float.intBitsToFloat(intValue[i]);
                    }

                  break;

                case TYPE_ASCII:
                  asciiValue = readASCIIValue(iis, offset + baseOffset, size, (code == 0x080a) ? 2 : 1);
                  valuesCount = 1;
                  break;
              }
          }
      }

    /*******************************************************************************
     * 
     * Reads an ASCII value.
     * 
     * @param  iis          the image input stream
     * @param  valueOffset  the offset where the value is
     * @param  valueCount   the lenght of the string
     * @return              the string
     * @throws IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    private String readASCIIValue (ImageInputStream iis,
                                   int valueOffset,
                                   int valueCount,
                                   int stringCount) throws IOException
      {
        StringBuffer buffer = new StringBuffer();
        boolean needReset = false;

          //        if (valueCount > 4)
          {
            needReset = true;
            iis.mark();
            iis.seek(valueOffset);
          }

        while (valueCount-- > 0)
          {
            char ch = (char)iis.readByte();

            if ((ch == 0) && (--stringCount == 0))
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
     * @inheritDoc
     * 
     ******************************************************************************/
    public String toString ()
      {
        String name = registry.getTagName(code);

        if (name == null)
          {
            name = "#" + code;
          }

        StringBuffer buffer = new StringBuffer(name);
        buffer.append(" type: ");
        buffer.append(typeToString[type >>> 11]);

        if (valuesCount > 1)
          {
            buffer.append("[" + valuesCount + "]");
          }

        buffer.append(" ");

        if ((type == TYPE_MIX) || isSubDirectory())
          {
            buffer.append(" offset: ");
            buffer.append(offset);
            buffer.append(" size: ");
            buffer.append(size);
            buffer.append(" dataLocation: ");
            buffer.append(dataLocation);
          }

        else
          {
            appendValues(buffer);
          }

        return buffer.toString();
      }
  }
