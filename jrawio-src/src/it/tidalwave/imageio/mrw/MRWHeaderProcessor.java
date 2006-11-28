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
 * $Id: MRWHeaderProcessor.java,v 1.3 2006/02/25 00:05:54 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.HeaderProcessor;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWHeaderProcessor.java,v 1.3 2006/02/25 00:05:54 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class MRWHeaderProcessor extends HeaderProcessor
  {
    private int baseOffset;
    
    private int rasterOffset;
    
    private int rasterWidth;
    
    private int rasterHeight;
    
    private double[] coefficients = new double[4];
    
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public void process (RAWImageInputStream iis) throws IOException
      {        
        iis.setBaseOffset(0);
        iis.seek(4);
        rasterOffset = iis.readInt() + 8;
        long save;
        
        while ((save = iis.getStreamPosition()) < rasterOffset)
          {
            int tag = iis.readInt();
            int len = iis.readInt();
            
            System.err.println("TAG " + Integer.toHexString(tag) + " LEN: " +  len);

            switch (tag) 
              {
                case 0x505244: // PRD 
                  iis.skipBytes(8);
                  rasterHeight = iis.readShort() & 0xFFFF;
                  rasterWidth = iis.readShort() & 0xFFFF;
                  break;
                  
                case 0x574247: // WBG 
                  iis.skipBytes(4);
                  int j = 0; // strstr(model,"A200") ? 3:0;
                  
                  for (int i = 0; i < 4; i++)
                    {
                      coefficients[i ^ (i >> 1) ^ j] = (1.0/256.0) * iis.readShort();  
                    }

                  break;
                  
                case 0x545457: // TTW 
                  baseOffset = (int)iis.getStreamPosition();
                  break;
              }
            
            iis.seek(save + len + 8);
          }
      }
    
    /*******************************************************************************
     * 
     * @param iis
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    public int getBaseOffset() 
      {
        return baseOffset;
      }
    
    public int getRasterOffset()
      { 
        return rasterOffset;  
      }
    
    public int getRasterWidth()
      { 
        return rasterWidth;  
      }
    
    public int getRasterHeight()
      { 
        return rasterHeight;  
      }

    public double[] getCoefficients()
      {
        return coefficients;
      }
  }
