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
package it.tidalwave.imageio.raf;

import javax.annotation.Nonnull;
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
public class RAFImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = RAFImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     * 
     ******************************************************************************************************************/
    public RAFImageReaderSpi()
      {
        super("RAF", "raf", "image/x-fuji-raf", RAFImageReader.class);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getDescription (final Locale locale)
      {
        return "Standard RAF Image Reader";
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
        return new RAFImageReader(this, extension);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public boolean canDecodeInput (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {
        iis.seek(0);
        final byte[] buffer = new byte[48];
        iis.readFully(buffer);
        final String s = new String(buffer);

        return s.startsWith("FUJIFILMCCD-RAW");
//        long ifdOffset = TIFFImageReaderSupport.processHeader(iis, null);
//        final IFD primaryIFD = new IFD();
//        primaryIFD.load(iis, ifdOffset);
//
//        if (primaryIFD.isDNGVersionAvailable())
//          {
//            return false;
//          }
//
//        final String make = primaryIFD.getMake();
//        final String model = primaryIFD.getModel();
//
//        if ((make == null) || !make.toUpperCase().startsWith("FUJI"))
//          {
//            logger.fine("RAFImageReaderSpi giving up on: '%s' / '%s'", make, model);
//            return false;
//          }
      }
  }
