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
 * $Id: RAWProcessorInstaller.java,v 1.1 2006/02/17 15:31:57 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.raw.PostProcessor;
import it.tidalwave.imageio.cr2.CR2ImageReaderSpi;
import it.tidalwave.imageio.crw.CRWImageReaderSpi;
import it.tidalwave.imageio.dng.DNGImageReaderSpi;
import it.tidalwave.imageio.mrw.MRWImageReaderSpi;
import it.tidalwave.imageio.nef.NEFImageReaderSpi;
import it.tidalwave.imageio.orf.ORFImageReaderSpi;
import it.tidalwave.imageio.pef.PEFImageReaderSpi;
import it.tidalwave.imageio.srf.SRFImageReaderSpi;
import it.tidalwave.imageio.rawprocessor.cr2.CR2Processor;
import it.tidalwave.imageio.rawprocessor.crw.CRWProcessor;
import it.tidalwave.imageio.rawprocessor.dng.DNGProcessor;
import it.tidalwave.imageio.rawprocessor.mrw.MRWProcessor;
import it.tidalwave.imageio.rawprocessor.nef.NEFProcessor;
import it.tidalwave.imageio.rawprocessor.orf.ORFProcessor;
import it.tidalwave.imageio.rawprocessor.pef.PEFProcessor;
import it.tidalwave.imageio.rawprocessor.srf.SRFProcessor;

/**
 *
 * @author Fabrizio Giudici
 *
 */
public class RAWProcessorInstaller 
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.RAWProcessorInstaller";
            
    private final static Logger logger = Logger.getLogger(CLASS);
    
    static
      {
        String jrawioVersion = it.tidalwave.imageio.raw.Version.BUILD;
        
        if (jrawioVersion.startsWith(Version.REQUIRED_JRAWIO_VERSION))
          {
            logger.info("Installing RAWProcessor...");
            RAWImageReaderSpiSupport.installPostProcessor(CR2ImageReaderSpi.class, new CR2Processor());
            RAWImageReaderSpiSupport.installPostProcessor(CRWImageReaderSpi.class, new CRWProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(DNGImageReaderSpi.class, new DNGProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(MRWImageReaderSpi.class, new MRWProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(NEFImageReaderSpi.class, new NEFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(ORFImageReaderSpi.class, new ORFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(PEFImageReaderSpi.class, new PEFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(SRFImageReaderSpi.class, new SRFProcessor());
            logger.info("Installed RAWProcessor");
          }
        
        else
          {
            logger.warning("This RAWProcessor cannot be installed!");    
            logger.warning(">>>> Found jrawio version: " + jrawioVersion + ", required: " + Version.REQUIRED_JRAWIO_VERSION);    
          }
      }
  }
