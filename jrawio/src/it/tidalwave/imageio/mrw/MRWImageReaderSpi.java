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
 * $Id: MRWImageReaderSpi.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import java.nio.ByteOrder;
import java.util.Locale;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWImageReaderSpi.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.mrw.MRWImageReaderSpi");
    
    private final static int MRW_MAGIC = 0x004D524D;
            
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public MRWImageReaderSpi ()
      {
        super("MRW", "mrw", "image/mrw", MRWImageReader.class);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard MRW Image Reader";
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new MRWImageReader(this, extension);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public boolean canDecodeInput (RAWImageInputStream iis) throws IOException
      {
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        iis.seek(0);
        int magic = iis.readInt();
               
        if (magic != MRW_MAGIC)
          {
            logger.fine("MRWImageReaderSpi giving up on: '" + Integer.toHexString(magic) + "'");
            return false;
          }

        return true;
      }
  }
