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
package it.tidalwave.imageio.srf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
 *
 * Thanks to Dave Coffin: http://www.cybercom.net/~dcoffin/dcraw/sony_clear.c
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SonyDecipher
  {
    private int key;

    private byte[] data = new byte[0];

    private int[] pad = new int[128];

    private int padScan;

    private boolean initialized = false;

    private ByteBuffer byteBuffer;

    /*******************************************************************************************************************
     * 
     * @param key
     * 
     *******************************************************************************/
    public SonyDecipher (int key)
      {
        this.key = key;
      }

    /*******************************************************************************************************************
     * 
     * @param iis
     * @param size
     * @throws IOException 
     * 
     *******************************************************************************/
    public void decrypt (ImageInputStream iis,
                         int size) throws IOException
      {
        if (size % 4 != 0)
          {
            throw new IllegalArgumentException("size must be a multiple of 4");
          }

        if (data.length < size)
          {
            data = new byte[size];
            byteBuffer = ByteBuffer.allocate(size);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
          }

        iis.readFully(data);

        if (!initialized)
          {
            initialize();
          }

        ByteBuffer tempByteBuffer = ByteBuffer.wrap(data);
        tempByteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.rewind();

        for (int i = 0; i < size / 4; i++)
          {
            int mask = (pad[(padScan + 1) & 127] ^ pad[(padScan + 65) & 127]);
            pad[padScan++ & 127] = mask;
            int v = tempByteBuffer.getInt() ^ mask;
            byteBuffer.putInt(v);
          }

        byteBuffer.rewind();
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public ByteBuffer getByteBuffer ()
      {
        return byteBuffer;
      }

    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    private void initialize ()
      {
        for (int p = 0; p < 4; p++)
          {
            pad[p] = key = (int)((((long)key) & 0xffffffff) * 48828125 + 1);
          }

        pad[3] = (pad[3] << 1) | ((pad[0] ^ pad[2]) >>> 31);

        for (int p = 4; p < 127; p++)
          {
            pad[p] = ((pad[p - 4] ^ pad[p - 2]) << 1) | ((pad[p - 3] ^ pad[p - 1]) >>> 31);
          }

        padScan = 127;
        initialized = true;
      }
  }
