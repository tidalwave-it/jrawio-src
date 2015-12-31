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
package it.tidalwave.imageio.raf;

import java.io.IOException;
import java.io.Serializable;
import it.tidalwave.imageio.io.RAWImageInputStream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class FujiTable1 implements Serializable
  {
    private final static long serialVersionUID = 234235345423523L;

    private short rawHeight;
    private short rawWidth;
    private short height;
    private short width;
    private boolean fujiLayout;
    private short[] coefficients = new short[4];

    public int getWidth()
      {
        return width;
      }

    public int getHeight()
      {
        return height;
      }

    public int getRawWidth()
      {
        return rawWidth;
      }

    public int getRawHeight()
      {
        return rawHeight;
      }

    public int getRotatedWidth()
      {
        return getWidth() / (fujiLayout ? 1 : 2) + getHeight() / (fujiLayout ? 2 : 1);
      }

    public int getRotatedHeight()
      {
        return getRotatedWidth() - 1;
      }

    public boolean isFujiLayout()
      {
        return fujiLayout;
      }

    public short[] getCoefficients()
      {
        return (coefficients == null) ? null : coefficients.clone();
      }

    public void load(final RAWImageInputStream iis,
            final int offset,
            final int length)
            throws IOException
      {
        iis.seek(offset);
        final int entries = iis.readInt();

        for (int i = 0; i < entries; i++)
          {
            if (iis.getStreamPosition() >= offset + length)
              {
                break;
              }

            final int tag = iis.readShort();
            final int len = iis.readShort();
            final long save = iis.getStreamPosition();

            switch (tag)
              {
                case 0x100:
                    rawHeight = iis.readShort();
                    rawWidth = iis.readShort();
                    break;

                case 0x121:
                    height = iis.readShort();
                    width = iis.readShort();

                    if (width == 4824)
                      {
                        width += 3;
                      }

                    break;

                case 0x130:
                    fujiLayout = (iis.readByte() & 0x80) != 0;
                    break;

                case 0x2ff0:
                    for (int c = 0; c < coefficients.length; c++)
                      {
                        coefficients[c] = iis.readShort();
                      }
                    
                    break;
              }

            iis.seek(save + len);
          }

        if (fujiLayout)
          {
            width /= 2;
            height *= 2;
          }
      }
  }
