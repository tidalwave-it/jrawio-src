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
 * $Id: SRFImageReaderSpi.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import java.util.Locale;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: SRFImageReaderSpi.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class SRFImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.srf.SRFImageReaderSpi");
    
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public SRFImageReaderSpi ()
      {
        super("SRF", "srf", "image/srf", SRFImageReader.class);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard SRF Image Reader";
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new SRFImageReader(this, extension);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
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
            || (!model.toUpperCase().startsWith("DSC")))
          {
            logger.fine("SRFImageReaderSpi giving up on: '" + make + "' / '" + model + "'");
            return false;
          }

        return true;
      }
  }
