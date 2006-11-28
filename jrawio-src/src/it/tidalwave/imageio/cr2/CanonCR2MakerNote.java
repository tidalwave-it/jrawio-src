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
 * $Id: CanonCR2MakerNote.java,v 1.2 2006/02/25 13:27:26 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.cr2;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CanonCR2MakerNote.java,v 1.2 2006/02/25 13:27:26 fabriziogiudici Exp $
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
