/*******************************************************************************
 * 
 * blueMarine - open source photo workflow
 * ---------------------------------------
 * 
 * Copyright (C) 2003-2005 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://www.tidalwave.it/bluemarine
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
 * $Id: BitReaderTest.java,v 1.1 2006/01/31 21:18:53 fabriziogiudici Exp $
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
