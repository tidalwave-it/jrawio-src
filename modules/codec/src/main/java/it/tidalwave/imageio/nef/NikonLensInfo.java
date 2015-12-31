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
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnull;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NikonLensInfo // NOT Serializable, it is rebuilt on demand
  {
    private final static String CLASS = NikonLensInfo.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private static Properties lensNameByID = new Properties();

    private int version;

    private final static int V100 = 0x30313030;

    private final static int V101 = 0x30313031;

    private byte lensID;

    private String lensName;

    private byte lensFStops;

    private byte minFocalLength;

    private byte maxFocalLength;

    private byte maxApertureAtMinFocal;

    private byte maxApertureAtMaxFocal;

    private byte mcuVersion;

    static
      {
        try
          {
            InputStream is = NikonLensInfo.class.getResourceAsStream("NikonLens.properties");
            
            if (is == null)
              {
                throw new RuntimeException("Cannot load NikonLens.properties");
              }
              
            lensNameByID.load(is);
            is.close();
          }
        catch (IOException e)
          {
            logger.severe(e.toString());
            logger.throwing(CLASS, "static ctor", e);
          }
      }

    /*******************************************************************************************************************
     * 
     * @param bytes
     * 
     *******************************************************************************/
    /* package */NikonLensInfo (@Nonnull final byte[] bytes)
      {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        version = buffer.getInt();
        int offset = 6;

        if (version == V101)
          {
            offset = 11;
          }

        if (version != V100 && version != V101)
          {
            return;
          }

        lensID = buffer.get(offset + 0);
        lensName = lensNameByID.getProperty("" + lensID);
        lensFStops = buffer.get(offset + 1);
        minFocalLength = buffer.get(offset + 2);
        maxFocalLength = buffer.get(offset + 3);
        maxApertureAtMinFocal = buffer.get(offset + 4);
        maxApertureAtMaxFocal = buffer.get(offset + 5);
        mcuVersion = buffer.get(12);
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public String getLensName()
      {
        return lensName;
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Version: " + Integer.toHexString(version));
        buffer.append(", lensID: " + lensID);
        buffer.append(", lensName: " + lensName);
        buffer.append(", lensFStops: " + lensFStops);
        buffer.append(", minFocalLength: " + minFocalLength);
        buffer.append(", maxFocalLength: " + maxFocalLength);
        buffer.append(", maxApertureAtMinFocal: " + maxApertureAtMinFocal);
        buffer.append(", maxApertureAtMaxFocal: " + maxApertureAtMaxFocal);
        buffer.append(", mcuVersion: " + mcuVersion);

        return buffer.toString();
      }
  }
