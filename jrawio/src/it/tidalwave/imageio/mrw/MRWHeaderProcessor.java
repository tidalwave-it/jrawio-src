/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
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
 * $Id: MRWHeaderProcessor.java 73 2008-08-23 21:39:29Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.HeaderProcessor;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWHeaderProcessor.java 73 2008-08-23 21:39:29Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWHeaderProcessor extends HeaderProcessor
  {
    private final static String CLASS = MRWHeaderProcessor.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private int baseOffset;
    
    private int rasterOffset;
    
    private int rasterWidth;
    
    private int rasterHeight;
    
    private final double[] coefficients = new double[4];
    
    /***************************************************************************
     * 
     * @param  iis  the input stream
     * 
     **************************************************************************/
    @Override
    public void process (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {        
        iis.setBaseOffset(0);
        iis.seek(4);
        rasterOffset = iis.readInt() + 8;
        long save;
        
        while ((save = iis.getStreamPosition()) < rasterOffset)
          {
            int tag = iis.readInt();
            int len = iis.readInt();
            
            logger.fine("MRW HEADER TAG " + Integer.toHexString(tag) + " LEN: " +  len);

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
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    @Override
    public int getBaseOffset() 
      {
        return baseOffset;
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    public int getRasterOffset()
      { 
        return rasterOffset;  
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    public int getRasterWidth()
      { 
        return rasterWidth;  
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    public int getRasterHeight()
      { 
        return rasterHeight;  
      }

    /***************************************************************************
     * 
     * 
     **************************************************************************/
    public double[] getCoefficients()
      {
        return coefficients.clone();
      }
  }
