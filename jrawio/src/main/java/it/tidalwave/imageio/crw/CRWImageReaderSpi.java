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
package it.tidalwave.imageio.crw;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWImageReaderSpi extends RAWImageReaderSpiSupport
  {
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public CRWImageReaderSpi()
      {
        super("CRW", "crw", "image/x-canon-crw", CRWImageReader.class);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getDescription (final @Nonnull Locale locale)
      {
        return "Standard CRW Image Reader";
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnull
    public ImageReader createReaderInstance (final @Nonnull Object extension)
      throws IOException
      {
        return new CRWImageReader(this);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public boolean canDecodeInput (final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        iis.seek(6);
        final byte[] marker = new byte[8];
        iis.readFully(marker);

        return "HEAPCCDR".equals(new String(marker));
      }
  }
