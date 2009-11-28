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
package it.tidalwave.imageio.pef;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.io.IOException;
import java.io.DataInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class PEFDecoder
  {
    private final static String CLASS = PEFDecoder.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private int bitBuffer = 0;
    private int availableBitCount = 0;
    private boolean reset = false;
    private final int bitCountTable[] = new int[1 << 12];
    private final byte byteTable[] = new byte[1 << 12];

    public void load (final @Nonnull DataInputStream iis)
      throws IOException
      {
        final short bit0[] = new short[13];
        final short bit1[] = new short[13];

        for (int c = 0; c < 13; c++)
          {
            bit0[c] = iis.readShort();
          }

        for (int c = 0; c < 13; c++)
          {
            bit1[c] = iis.readByte();
          }

        for (int c = 0; c < 13; c++)
          {
            for (int i = bit0[c]; i <= ((bit0[c] + (bitCountTable.length >> bit1[c]) - 1) & (bitCountTable.length - 1)); )
              {
                bitCountTable[i] = bit1[c];
                byteTable[i] = (byte)c;
                i++;
              }
          }

//        logger.finest(">>>> bitCountTable: %s", Arrays.toString(bitCountTable));
//        logger.finest(">>>> byteTable:     %s", Arrays.toString(byteTable));
      }

    public int decode (final @Nonnegative int bitCount,
                       final boolean useTable,
                       final @Nonnull RAWImageInputStream iis,
                       final boolean zeroAfterFF)
      throws IOException
      {
        if ((bitCount == 0) || (availableBitCount < 0))
          {
            return 0;
          }

        int value;

        // TODO: RAWImageInputStream supports zeroAfterFF - use it and simplify this loop
        while (!reset && (availableBitCount < bitCount) && ((value = iis.read() & 0xff) != 0xff) &&
               !(reset = zeroAfterFF && (value == 0xff) && (iis.read() != 0)))
          {
            bitBuffer = (bitBuffer << 8) | value; // (uchar)c
            availableBitCount += 8;
          }

        value = (bitBuffer << (32 - availableBitCount)) >>> (32 - bitCount);

        if (useTable)
          {
            availableBitCount -= bitCountTable[value];
            value = byteTable[value] & 0xff; // (uchar)...
          }
        else
          {
            availableBitCount -= bitCount;
          }

        assert (availableBitCount >= 0);

//        logger.finest("getbithuff: returning c: %d, bitbuf: %x, vbits: %d", c, bitbuf, vbits);
        return value;
      }
  }
