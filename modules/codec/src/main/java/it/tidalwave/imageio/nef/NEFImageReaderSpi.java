/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.nef;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
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
public class NEFImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = NEFImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private final static List supportedModels = Arrays.asList(new String[] { "E5700", "E8700", "E5400", "E8800" });

    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public NEFImageReaderSpi()
      {
        super("NEF", new String[] { "nef", "ndf" }, "image/x-nikon-nef", NEFImageReader.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getDescription (final Locale locale)
      {
        return "Standard NEF Image Reader";
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    public ImageReader createReaderInstance (@CheckForNull final Object extension) 
      throws IOException
      {
        return new NEFImageReader(this, extension);
      }

    /*******************************************************************************************************************
     * 
     * @param iis
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    protected boolean canDecodeInput (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {
        iis.seek(0);
        NEFHeaderProcessor headerProcessor = new NEFHeaderProcessor();
        headerProcessor.process(iis);
        long ifdOffset = TIFFImageReaderSupport.processHeader(iis, headerProcessor);

        if (ifdOffset == 20) // TODO: bad fix for NDF files, must test for NDF instead - headerProcessor.isNDF()
          {
            ifdOffset -= NEFHeaderProcessor.NDF_OFFSET;
            iis.setBaseOffset(NEFHeaderProcessor.NDF_OFFSET); // TODO: move this behaviour as generic, with hproc.getBaseOffset()
          }

        IFD primaryIFD = new IFD();
        primaryIFD.load(iis,  ifdOffset);
        
        if (primaryIFD.isDNGVersionAvailable())
          { 
            logger.finest(">>>> FAILING, isDNGVersionAvailable returning true");
            return false;    
          }
        
        String make = primaryIFD.getMake();
        String model = primaryIFD.getModel();
        logger.finest("Make: %s, Model: %s", make, model);
        //
        // Beware that TIFF files out of Nikon scanners are tagged as Nikon.
        // Check the model name too.
        //
        if ((make == null) || !make.toUpperCase().startsWith("NIKON") || (model == null)
            || (!model.toUpperCase().startsWith("NIKON D") && !supportedModels.contains(model.toUpperCase())))
          {
            logger.finest(">>>> FAILING, supportedModels: %s", supportedModels);
            return false;
          }

        return true;
      }
  }
