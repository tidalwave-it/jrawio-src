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
 * $Id: NikonLensInfo.java,v 1.2 2006/02/25 20:59:24 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: NikonLensInfo.java,v 1.2 2006/02/25 20:59:24 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NikonLensInfo // NOT Serializable, it is rebuilt on demand
  {
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
            e.printStackTrace(); // TODO
          }
      }

    /*******************************************************************************
     * 
     * @param bytes
     * 
     *******************************************************************************/
    /* package */NikonLensInfo (byte[] bytes)
      {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        version = buffer.getInt();
        int offset = 6;

        if (version == V101)
          offset = 11;

        if (version != V100 && version != V101)
          return;

        lensID = buffer.get(offset + 0);
        lensName = lensNameByID.getProperty("" + lensID);
        lensFStops = buffer.get(offset + 1);
        minFocalLength = buffer.get(offset + 2);
        maxFocalLength = buffer.get(offset + 3);
        maxApertureAtMinFocal = buffer.get(offset + 4);
        maxApertureAtMaxFocal = buffer.get(offset + 5);
        mcuVersion = buffer.get(12);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public String getLensName ()
      {
        return lensName;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String toString ()
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
