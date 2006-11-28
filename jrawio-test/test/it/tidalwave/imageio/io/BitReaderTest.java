/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: BitReaderTest.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/

package it.tidalwave.imageio.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.LogManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import junit.framework.TestCase;
import it.tidalwave.imageio.io.BitReader;
import it.tidalwave.imageio.io.FastBitReader;

public class BitReaderTest extends TestCase
  {
    public void tearDown ()
      {
      }

    public BitReaderTest () throws Exception
      {
        try
          {
            configureLogging();
          }
        catch (IOException e)
          {
          }
      }

    private static void configureLogging () throws FileNotFoundException, IOException
      {
        String logConfigFile = "test-log.properties";
        System.err.println("Log configuration file: " + logConfigFile);

        InputStream is = new FileInputStream(new File(logConfigFile));
        LogManager.getLogManager().readConfiguration(is);
        is.close();
      }

    public void testBitReader () throws IOException
      {
        final int size = 1 * 1024 * 1024;
        byte[] buffer = generateTestBuffer(size);
        File file = File.createTempFile("buffer", "");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);
        fos.close();

        for (int round = 0; round < 1; round++)
          {
            System.out.println("\n**** ROUND " + round);
            int[] bitCount = generateRandomBitCountSequence(size * 8);
            long[] target = makeTargetBits(file, bitCount);
            ImageInputStream iis = new FileImageInputStream(file);
            BitReader br = new FastBitReader(iis, 64 * 1024);
            _testBitReader(br, bitCount, file, target);

            bitCount = generateFixedBitCountSequence(size * 8, 16);
            target = makeTargetBits(file, bitCount);
            iis = new FileImageInputStream(file);
            br = new SixteenBitsReader(iis, 64 * 1024);
            _testBitReader(br, bitCount, file, target);

            bitCount = generateFixedBitCountSequence(size * 8, 12);
            target = makeTargetBits(file, bitCount);
            iis = new FileImageInputStream(file);
            br = new TwelveBitsReader(iis, 64 * 1024);
            _testBitReader(br, bitCount, file, target);
          }
      }

    private int[] generateFixedBitCountSequence (int size,
                                                 int fixedBitCount)
      {
        int[] bitCount = new int[size / fixedBitCount];

        for (int i = 0; i < bitCount.length; i++)
          {
            bitCount[i] = fixedBitCount;
          }

        return bitCount;
      }

    private long[] makeTargetBits (File file,
                                   int[] bitCount) throws IOException
      {
        ImageInputStream iis = new FileImageInputStream(file);
        long[] target = new long[bitCount.length];

        long time = System.currentTimeMillis();

        for (int i = 0; i < target.length; i++)
          {
            target[i] = iis.readBits(bitCount[i]);
          }

        iis.close();
        long timeForIIS = System.currentTimeMillis() - time;
        System.out.println("ImageInputStream test done in " + timeForIIS + " msec");

        return target;
      }

    /*******************************************************************************
     * 
     * @param bitCount
     * @param file
     * @param targetBits
     * @throws FileNotFoundException
     * @throws IOException
     * 
     *******************************************************************************/

    private void _testBitReader (BitReader br,
                                 int[] bitCount,
                                 File file,
                                 long[] targetBits) throws FileNotFoundException, IOException
      {
        long time = System.currentTimeMillis();
        ImageInputStream iis = new FileImageInputStream(file);
        long[] bits = new long[bitCount.length];

        time = System.currentTimeMillis();

        for (int i = 0; i < bits.length; i++)
          {
            bits[i] = iis.readBits(bitCount[i]);
          }

        iis.close();
        long timeForFBR = System.currentTimeMillis() - time;
        System.out.println(br.getClass().getName() + " test done in " + timeForFBR + " msec");

        for (int i = 0; i < targetBits.length; i++)
          {
            assertEquals(targetBits[i], bits[i]);
          }
      }

    private byte[] generateTestBuffer (int size)
      {
        byte[] buffer = new byte[size];
        Random random = new Random(345938);
        random.nextBytes(buffer);

        return buffer;
      }

    private int[] generateRandomBitCountSequence (int size)
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

        int[] result = new int[j];
        System.arraycopy(list, 0, result, 0, result.length);
        System.out.println(">> Generated a sequence of " + result.length + " bit reads");

        return result;
      }
  }
