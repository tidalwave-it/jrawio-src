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
package it.tidalwave.imageio.arw;

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
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = ARWImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public ARWImageReaderSpi()
      {
        super("ARW", "arw", "image/x-sony-arw", ARWImageReader.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard ARW Image Reader";
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new ARWImageReader(this, extension);
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

        if ((make == null) || !make.toUpperCase().startsWith("SONY") || (model == null)
            || (!model.toUpperCase().startsWith("DSLR-A100"))) // others are .SRF
          {
            logger.fine("ARWImageReaderSpi giving up on: '%s' / %s'", make, model);
            return false;
          }

        return true;
      }
  }
