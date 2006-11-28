/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: NEFImageReaderSpi.java,v 1.3 2006/02/14 22:18:06 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.util.Arrays;
import java.util.List;
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
 * @version CVS $Id: NEFImageReaderSpi.java,v 1.3 2006/02/14 22:18:06 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = "it.tidalwave.imageio.nef.NEFImageReaderSpi";

    private final static Logger logger = Logger.getLogger(CLASS);

    private final static List supportedModels = Arrays.asList(new String[] { "E5700", "E8700", "E5400", "E8800" });

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public NEFImageReaderSpi ()
      {
        super("NEF", new String[] { "nef", "ndf" }, "image/nef", NEFImageReader.class);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard NEF Image Reader";
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new NEFImageReader(this, extension);
      }

    /*******************************************************************************
     * 
     * @param iis
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    protected boolean canDecodeInput (RAWImageInputStream iis) throws IOException
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
            return false;    
          }
        
        String make = primaryIFD.getMake();
        String model = primaryIFD.getModel();
        logger.finest("Make: " + make + ", Model: " + model);
        //
        // Beware that TIFF files out of Nikon scanners are tagged as Nikon.
        // Check the model name too.
        //
        if ((make == null) || !make.toUpperCase().startsWith("NIKON") || (model == null)
            || (!model.toUpperCase().startsWith("NIKON D") && !supportedModels.contains(model.toUpperCase())))
          {
            logger.finest(">>>> FAILING, supportedModels: " + supportedModels);
            return false;
          }

        return true;
      }
  }
