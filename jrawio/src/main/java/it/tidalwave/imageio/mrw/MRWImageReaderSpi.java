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
package it.tidalwave.imageio.mrw;

import javax.annotation.Nonnull;
import java.util.Locale;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.craw.RAWImageReaderSpiSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MRWImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = MRWImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private final static int MRW_MAGIC = 0x004D524D;
            
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public MRWImageReaderSpi()
      {
        super("MRW", "mrw", "image/x-minolta-mrw", MRWImageReader.class);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getDescription (final Locale locale)
      {
        return "Standard MRW Image Reader";
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    public ImageReader createReaderInstance (final Object extension) 
      throws IOException
      {
        return new MRWImageReader(this, extension);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public boolean canDecodeInput (@Nonnull final RAWImageInputStream iis)
      throws IOException
      {
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        iis.seek(0);
        final int magic = iis.readInt();
              
        // FIXME: should give up if it's a DNG, see other ImageReaderSpi's
        
        if (magic != MRW_MAGIC)
          {
            logger.fine("MRWImageReaderSpi giving up on: '%s'", Integer.toHexString(magic));
            return false;
          }

        return true;
      }
  }
