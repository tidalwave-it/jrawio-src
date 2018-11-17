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
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.dng;

import java.util.Locale;
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
public class DNGImageReaderSpi extends RAWImageReaderSpiSupport
  {
    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public DNGImageReaderSpi ()
      {
        super("DNG", "dng", "image/x-adobe-dng", DNGImageReader.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard DNG Image Reader";
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new DNGImageReader(this, extension);
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
        return primaryIFD.isDNGVersionAvailable();
      }
  }
