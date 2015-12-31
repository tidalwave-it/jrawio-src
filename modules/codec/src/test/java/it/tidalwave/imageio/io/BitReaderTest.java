/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.io;

import it.tidalwave.imageio.util.Logger;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class BitReaderTest 
  {
    private static final String CLASS = BitReaderTest.class.getName();
    private static final Logger logger = Logger.getLogger(CLASS);
    
    @Test
    public void testBitReader()
      throws IOException
      {
        final int size = 1 * 1024 * 1024;
        final byte[] buffer = generateTestBuffer(size);
        final File file = File.createTempFile("buffer", "");
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);
        fos.close();

        for (int round = 0; round < 10; round++)
          {
            logger.info("\n************ ROUND %d", round);
            final int[] bitCount1 = generateBitCountSequence(size * 8);
            final long[] target1 = makeTargetBits(file, bitCount1);
            final ImageInputStream iis1 = new FileImageInputStream(file);
            final BitReader br1 = new FastBitReader(iis1, 64 * 1024);
            testBitReader(br1, bitCount1, file, target1);

            final int[] bitCount2 = generateSameSizeBitCountSequence(size * 8, 16);
            final long[] target2 = makeTargetBits(file, bitCount2);
            final ImageInputStream iis2 = new FileImageInputStream(file);
            final BitReader br2 = new SixteenBitsReader(iis2, 64 * 1024);
            testBitReader(br2, bitCount2, file, target2);

            final int[] bitCount3 = generateSameSizeBitCountSequence(size * 8, 12);
            final long[] target3 = makeTargetBits(file, bitCount3);
            final ImageInputStream iis3 = new FileImageInputStream(file);
            final BitReader br3 = new TwelveBitsReader(iis3, 64 * 1024);
            testBitReader(br3, bitCount3, file, target3);
          }
      }

    private int[] generateSameSizeBitCountSequence (final int size,
                                                    final int fixedBitCount)
      {
        int[] bitCount = new int[size / fixedBitCount];

        for (int i = 0; i < bitCount.length; i++)
          {
            bitCount[i] = fixedBitCount;
          }

        return bitCount;
      }

    private long[] makeTargetBits (final File file,
                                   final int[] bitCount)
      throws IOException
      {
        final ImageInputStream iis = new FileImageInputStream(file);
        final long[] target = new long[bitCount.length];

        final long time = System.currentTimeMillis();

        for (int i = 0; i < target.length; i++)
          {
            target[i] = iis.readBits(bitCount[i]);
          }

        iis.close();
        final long timeForIIS = System.currentTimeMillis() - time;
        logger.info("ImageInputStream test done in %s msec", timeForIIS);

        return target;
      }

    /*******************************************************************************************************************
     * 
     * @param bitCount
     * @param file
     * @param targetBits
     * @throws FileNotFoundException
     * @throws IOException
     * 
     *******************************************************************************/
    private void testBitReader (BitReader br,
                                int[] bitCount,
                                File file,
                                long[] targetBits)
      throws FileNotFoundException, IOException
      {
        long time = System.currentTimeMillis();
        final ImageInputStream iis = new FileImageInputStream(file);
        final long[] bits = new long[bitCount.length];

        time = System.currentTimeMillis();

        for (int i = 0; i < bits.length; i++)
          {
            bits[i] = iis.readBits(bitCount[i]);
          }

        iis.close();
        final long timeForFBR = System.currentTimeMillis() - time;
        logger.info("%s test done in %d msec", br.getClass().getName(), timeForFBR);

        for (int i = 0; i < targetBits.length; i++)
          {
            assertEquals(targetBits[i], bits[i]);
          }
      }

    private byte[] generateTestBuffer (final int size)
      {
        final byte[] buffer = new byte[size];
        final Random random = new Random(345938);
        random.nextBytes(buffer);

        return buffer;
      }

    private int[] generateBitCountSequence (int size)
      {
        System.out.println("Generating bit count sequence for " + size + " bits");
        int[] list = new int[size]; // worst case
        Random random = new Random(125543);

        int j = 0;
        while (size > 1)
          {
            int i = 1 + random.nextInt(Math.min(size, 32) - 1);
            list[j++] = i;
            size -= i;
          }

        final int[] result = new int[j];
        System.arraycopy(list, 0, result, 0, result.length);
        logger.info(">> Generated a sequence of %d bit reads", result.length);

        return result;
      }
  }
