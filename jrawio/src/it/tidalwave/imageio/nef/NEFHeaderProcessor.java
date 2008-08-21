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
 * $Id: NEFHeaderProcessor.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFHeaderProcessor.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFHeaderProcessor extends HeaderProcessor
  {
    public static final int NDF_OFFSET = 12;
    
    private boolean isNDF;

    /*******************************************************************************
     * 
     * @param iis
     * @throws IOException
     * 
     *******************************************************************************/
    public void process (RAWImageInputStream iis) throws IOException
      {
        iis.mark();
        byte[] buffer = new byte[NDF_OFFSET];
        iis.seek(0);
        iis.read(buffer, 0, buffer.length);
        isNDF = new String(buffer, 0, 3).equals("NDF");         
        iis.reset();
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public int getOffset()
      {
        return isNDF ? NDF_OFFSET : 0;
      }
  }
