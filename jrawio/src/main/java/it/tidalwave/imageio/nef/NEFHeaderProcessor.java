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
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.craw.HeaderProcessor;
import it.tidalwave.imageio.io.RAWImageInputStream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFHeaderProcessor extends HeaderProcessor
  {
    public static final int NDF_OFFSET = 12;
    
    private boolean isNDF;

    /*******************************************************************************************************************
     * 
     * @param iis
     * @throws IOException
     * 
     *******************************************************************************/
    @Override
    public void process (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {
        iis.mark();
        byte[] buffer = new byte[NDF_OFFSET];
        iis.seek(0);
        iis.readFully(buffer);
        isNDF = new String(buffer, 0, 3).equals("NDF");         
        iis.reset();
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    @Override
    public int getOffset()
      {
        return isNDF ? NDF_OFFSET : 0;
      }
  }
