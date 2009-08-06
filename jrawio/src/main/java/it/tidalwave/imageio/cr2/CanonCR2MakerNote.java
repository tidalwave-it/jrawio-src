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
package it.tidalwave.imageio.cr2;

import it.tidalwave.imageio.util.Logger;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CanonCR2MakerNote extends CanonCR2MakerNoteSupport
  {
    private static final String CLASS = CanonCR2MakerNote.class.getName();
    private static final Logger logger = Logger.getLogger(CLASS);
    
    private static Properties lensNameByID = new Properties();
    private final static long serialVersionUID = 6347805638960118907L;

    @CheckForNull
    private transient CR2SensorInfo sensorInfo;

    static
      {
        try
          {
            final InputStream is = CanonCR2MakerNote.class.getResourceAsStream("CanonLens.properties");
            lensNameByID.load(is);
            is.close();
          }
        catch (IOException e)
          {
            logger.warning("Can't load CanonLens.properties: %s", e);
            logger.throwing(CLASS, "", e);
          }
      }

    @Nonnull
    public synchronized  CR2SensorInfo getSensorInfo()
      {
        if (sensorInfo == null)
          {
            sensorInfo = new CR2SensorInfo(getSensorInfoAsIntegers());
          }

        return sensorInfo;
      }

    @Nonnegative
    public int getLensType()
      {
        return 0; // getCanonCameraSettings()[22]; FIXME
      }

    @Nonnull
    public String getLensName()
      {
        return lensNameByID.getProperty("" + getLensType());
      }

    @Override
    @Nonnull
    public String getOwnerName()
      {
        String artist = super.getOwnerName();
        int i = artist.indexOf(0);
        
        if (i >= 0)
          {
            artist = artist.substring(0, i);
          }
        
        return artist;
      }
    
    @Nonnull
    public short[] getWhiteBalanceCoefficients()
      {
        int[] wbi = getWhiteBalanceInfo();
        short[] coefficients = new short[4];
        
        if (wbi != null)
          {
            int offset = 0;
            
            switch (wbi.length)
              {
                case 582:
                  offset = 50 / 2;
                  break;
                  
                case 653:
                  offset = 68 / 2;
                  break;
                  
                case 796: 
                  offset = 126 / 2;
                  break;
                  
                default:
                  offset = 0;
                  break;
              }
            
            for (int i = 0; i < coefficients.length; i++)
              {
                coefficients[i] = (short)wbi[offset + i];  
                logger.finer(">>>> wb coefficient[%d]=%f", i, coefficients[i]);
              }
          }
        
        return coefficients;
      }
  }
