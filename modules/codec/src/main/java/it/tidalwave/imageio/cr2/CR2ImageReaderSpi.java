/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2ImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = CR2ImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public CR2ImageReaderSpi()
      {
        super("CR2", "cr2", "image/x-canon-cr2", CR2ImageReader.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getDescription (final @Nonnull Locale locale)
      {
        return "Standard CR2 Image Reader";
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public ImageReader createReaderInstance (final @Nonnull Object extension)
      throws IOException
      {
        return new CR2ImageReader(this);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public boolean canDecodeInput (final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        iis.seek(0);
        final long ifdOffset = TIFFImageReaderSupport.processHeader(iis, null);
        final IFD primaryIFD = new IFD();
        primaryIFD.load(iis, ifdOffset);
        
        if (primaryIFD.isDNGVersionAvailable())
          { 
            return false;    
          }
        
        final String make = primaryIFD.getMake();
        final String model = primaryIFD.getModel();
        //
        // CHECK THIS: TIFF files out of Canon scanners are tagged as Canon?
        // Check the model name too.
        //
        if ((make == null) || !make.toUpperCase().startsWith("CANON") 
            || (model == null) || (!model.toUpperCase().startsWith("CANON")))
          {
            logger.fine("CR2ImageReaderSpi giving up on: '%s' / %s'", make, model);
            return false;
          }

        return true;
      }
  }
