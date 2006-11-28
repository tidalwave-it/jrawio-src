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
 * $Id: CanonCR2MakerNote.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.cr2;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CanonCR2MakerNote.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class CanonCR2MakerNote extends CanonCR2MakerNoteSupport
  {
    private static Properties lensNameByID = new Properties();
    private final static long serialVersionUID = 6347805638960118907L;

    static
      {
        try
          {
            InputStream is = CanonCR2MakerNote.class.getResourceAsStream("CanonLens.properties");
            lensNameByID.load(is);
            is.close();
          }
        catch (IOException e)
          {
            e.printStackTrace(); // TODO
          }
      }

    public int getLensType ()
      {
        return 0; // getCanonCameraSettings()[22]; FIXME
      }

    public String getLensName ()
      {
        return lensNameByID.getProperty("" + getLensType());
      }

    public int getSensorWidth ()
      {
        return getSensorInfo()[1];
      }

    public int getSensorHeight()
      {
        return getSensorInfo()[2];
      }
    
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
                System.err.println("COEEFICIENT[" + i + "]=" + coefficients[i]);
              }
          }
        
        return coefficients;
      }
  }
