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
package it.tidalwave.imageio.decoder;

import javax.annotation.Nonnull;
import java.io.IOException;
import javax.annotation.Nonnegative;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
 *
 * This class is capable to decode bit strings encoded with a Huffman table.
 * PENDING: give a short description of what a Huffman table is.
 * 
 * <br>
 * This class is able to create itself from a description in an array of bytes or
 * shorts, following some common schemes used in many RAW formats.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class HuffmannDecoder
  {
    /** The offset where leaves starts. */
    private static final int LEAVES_OFFSET = 16;

    /** The counter of leaves. */
    private static int leafCounter;

    /** The two pointers (index 0 and 1) to the lower tree level. */
    public HuffmannDecoder[] branch = new HuffmannDecoder[2];

    /** The value of the current leaf. */
    private int leafValue;

    private HuffmannDecoder ()
      {
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public static HuffmannDecoder[] createDecoders (final @Nonnull short[] ... sources)
      {
        final HuffmannDecoder[] decoders = new HuffmannDecoder[sources.length];

        for (int i = 0; i < decoders.length; i++)
          {
            decoders[i] = createDecoder(sources[i]);
          }

        return decoders;
      }

    /*******************************************************************************************************************
     * 
     * Creates a decode tree according the specification in source.
     * The first 16 bytes specify how many codes should be 1-bit, 2-bit
     * 3-bit, etc.  Bytes after that are the leaf values.
     * 
     * For example, if the source is
     * 
     * <code>
     *  { 0,1,4,2,3,1,2,0,0,0,0,0,0,0,0,0,
     *    0x04,0x03,0x05,0x06,0x02,0x07,0x01,0x08,0x09,0x00,0x0a,0x0b,0xff  },
     * </code>
     * 
     * then the code is
     * 
     * <code>
     * 00			0x04
     * 010			0x03
     * 011			0x05
     * 100			0x06
     * 101			0x02
     * 1100			0x07
     * 1101			0x01
     * 11100		    0x08
     * 11101		    0x09
     * 11110		    0x00
     * 111110		0x0a
     * 1111110		0x0b
     * 1111111		0xff
     * </code>
     * 
     * @param source  the coded version as an array of shorts
     * @return        the HuffmannDecoder
     * 
     ******************************************************************************************************************/
    @Nonnull
    public static HuffmannDecoder createDecoder (final @Nonnull short[] source)
      {
        final byte[] bytes = new byte[source.length];

        for (int i = 0; i < bytes.length; i++)
          {
            bytes[i] = (byte)source[i];
          }

        return createDecoder(bytes, 0);
      }

    /*******************************************************************************************************************
     * 
     * Creates a decode tree according the specification in source. See
     * {@link #createDecoder(short[])} for more information.
     * 
     * @param source  the coded version as an array of bytes
     * @param start   the offset in the source to the first byte to use
     * @return        the HuffmannDecoder
     * 
     ******************************************************************************************************************/
    @Nonnull
    public static HuffmannDecoder createDecoder (final @Nonnull byte[] source, final int start)
      {
        leafCounter = 0;
        return createDecoder(source, start, 0, false);
      }

    /*******************************************************************************************************************
     * 
     * Creates a decode tree according the specification in source. See
     * {@link #createDecoder(short[])} for more information. This version should be
     * used for Hufmman tables used in Lossless JPEG compression - they have some
     * specific issues.
     * 
     * @param source  the coded version as an array of bytes
     * @param start   the offset in the source to the first byte to use
     * @return        the HuffmannDecoder
     * 
     ******************************************************************************************************************/
    @Nonnull
    public static HuffmannDecoder createDecoderWithJpegHack (final @Nonnull byte[] source, final int start)
      {
        leafCounter = 0;
        return createDecoder(source, start, 0, true);
      }

    /*******************************************************************************************************************
     * 
     * @param source
     * @param start
     * @param level
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private static HuffmannDecoder createDecoder (final @Nonnull byte[] source,
                                                  int start,
                                                  int level,
                                                  boolean jpgHack)
      {
        HuffmannDecoder dest = new HuffmannDecoder();
        createDecoder(dest, source, start, level, jpgHack);

        return dest;
      }

    /*******************************************************************************************************************
     * 
     * @param dest
     * @param source
     * @param start
     * @param level
     * @param jpgHack
     * 
     ******************************************************************************************************************/
    private static void createDecoder (final @Nonnull HuffmannDecoder dest,
                                       final @Nonnull byte[] source,
                                       int start,
                                       int level,
                                       boolean jpgHack)
      {
        int next;

        for (int i = next = 0; (i <= leafCounter) && (next < LEAVES_OFFSET); i += (source[start + next++] & 0xff));

        if (level < next && (!jpgHack || (next < 16)))
          {
            dest.branch[0] = createDecoder(source, start, level + 1, jpgHack);
            dest.branch[1] = createDecoder(source, start, level + 1, jpgHack);
          }

        else
          {
            int i = start + LEAVES_OFFSET + leafCounter++;

            if (i < source.length)
              {
                dest.leafValue = (source[i] & 0xff);
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * Reads an encoded value from a {@link BitReader} and returns the decoded value.
     *  
     * @param   br           the bit reader
     * @return               the decoded value
     * @throws  IOException  if an I/O exception occurs
     * 
     ******************************************************************************************************************/
    public int decode (final @Nonnull ImageInputStream br)
      throws IOException
      {
        HuffmannDecoder d = this;

        while (d.branch[0] != null)
          {
            d = d.branch[(int)br.readBits(1)]; // FIXME: replace with iis.readBit()
          }

        return d.leafValue;
      }

    /*******************************************************************************************************************
     *
     * A Huffman encoder represents a value with the strictly needed bits, thus stripping off all the leading zeroes.
     * For instance, +9 in binary is 0000 0000 0000 1001 and is encoded with just 4 bits: 1001. This means that,
     * by definition, positive numbers are encoded with the most significant bit equals to 1 - the opposite convention
     * than the usual 2-complemented representation. For coherence, negative numbers need to be represented with a
     * leading zero - in particular, they are the 1-complement of their absolute value. For instance, -9 is represented
     * as 0110, which is the 1-complement of the encoded value for +9 (1001).
     *
     * This method performs the associated decoding, thus returning integer values represented with the normal
     * convention.
     *
     * @param  bitsToGet    how many bits to read
     * @return              the bits as an integer
     * @throws IOException  if any I/O error occurs
     *
     ******************************************************************************************************************/
    public int readSignedBits (final @Nonnull ImageInputStream iis,
                               final @Nonnegative int bitCount)
      throws IOException
      {
        int value = (int)iis.readBits(bitCount);

        if ((value & (1 << (bitCount - 1))) == 0)
          {
            value -= (1 << bitCount) - 1;
          }

        return value;
      }
    
    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     ******************************************************************************************************************/
    @Nonnull
    @Override
    public String toString()
      {
        return toString("");
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String toString (final @Nonnull String prefix)
      {
        StringBuffer buffer = new StringBuffer();

        if (branch[0] == null)
          {
            buffer.append(prefix + ": 0x" + Integer.toHexString(leafValue) + "\n");
          }

        else
          {
            buffer.append(branch[0].toString(prefix + "0"));
            buffer.append(branch[1].toString(prefix + "1"));
          }

        return buffer.toString();
      }
  }
