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
package it.tidalwave.imageio.cr2;

import java.util.Locale;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/***********************************************************************************************************************
 *
 * @author  fritz
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
    public CR2ImageReaderSpi ()
      {
        super("CR2", "cr2", "image/x-canon-cr2", CR2ImageReader.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard CR2 Image Reader";
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new CR2ImageReader(this);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public boolean canDecodeInput (RAWImageInputStream iis) throws IOException
      {
        iis.seek(0);
        long ifdOffset = TIFFImageReaderSupport.processHeader(iis, null);
        IFD primaryIFD = new IFD();
        primaryIFD.load(iis, ifdOffset);
        
        if (primaryIFD.isDNGVersionAvailable())
          { 
            return false;    
          }
        
        String make = primaryIFD.getMake();
        String model = primaryIFD.getModel();
        //
        // CHECK THIS: TIFF files out of Canon scanners are tagged as Canon?
        // Check the model name too.
        //
        if ((make == null) || !make.toUpperCase().startsWith("CANON") || (model == null)
            || (!model.toUpperCase().startsWith("CANON EOS")))
          {
            logger.fine("CR2ImageReaderSpi giving up on: '%s' / %s'", make, model);
            return false;
          }

        return true;
      }
  }
