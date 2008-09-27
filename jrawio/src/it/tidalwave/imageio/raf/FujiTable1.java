/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tidalwave.imageio.raf;

import java.io.IOException;
import java.io.Serializable;
import it.tidalwave.imageio.io.RAWImageInputStream;

/**
 *
 * @author fritz
 */
public class FujiTable1 implements Serializable
  {
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
