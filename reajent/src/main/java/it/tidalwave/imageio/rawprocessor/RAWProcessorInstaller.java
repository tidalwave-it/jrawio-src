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
 * $Id: RAWProcessorInstaller.java 264 2009-06-15 09:31:03Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.arw.ARWImageReaderSpi;
import it.tidalwave.imageio.cr2.CR2ImageReaderSpi;
import it.tidalwave.imageio.crw.CRWImageReaderSpi;
import it.tidalwave.imageio.dng.DNGImageReaderSpi;
import it.tidalwave.imageio.mrw.MRWImageReaderSpi;
import it.tidalwave.imageio.nef.NEFImageReaderSpi;
import it.tidalwave.imageio.orf.ORFImageReaderSpi;
import it.tidalwave.imageio.pef.PEFImageReaderSpi;
import it.tidalwave.imageio.raf.RAFImageReaderSpi;
import it.tidalwave.imageio.srf.SRFImageReaderSpi;
import it.tidalwave.imageio.rawprocessor.arw.ARWProcessor;
import it.tidalwave.imageio.rawprocessor.cr2.CR2Processor;
import it.tidalwave.imageio.rawprocessor.crw.CRWProcessor;
import it.tidalwave.imageio.rawprocessor.dng.DNGProcessor;
import it.tidalwave.imageio.rawprocessor.mrw.MRWProcessor;
import it.tidalwave.imageio.rawprocessor.nef.NEFProcessor;
import it.tidalwave.imageio.rawprocessor.orf.ORFProcessor;
import it.tidalwave.imageio.rawprocessor.pef.PEFProcessor;
import it.tidalwave.imageio.rawprocessor.raf.RAFProcessor;
import it.tidalwave.imageio.rawprocessor.srf.SRFProcessor;

/**
 *
 * @author Fabrizio Giudici
 *
 */
public class RAWProcessorInstaller 
  {
    private final static String CLASS = RAWProcessorInstaller.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    static
      {
        String jrawioVersion = it.tidalwave.imageio.raw.Version.TAG;
        
        if (jrawioVersion.startsWith(Version.REQUIRED_JRAWIO_TAG))
          {
            logger.info("Installing RAWProcessor...");
            RAWImageReaderSpiSupport.installPostProcessor(ARWImageReaderSpi.class, new ARWProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(CR2ImageReaderSpi.class, new CR2Processor());
            RAWImageReaderSpiSupport.installPostProcessor(CRWImageReaderSpi.class, new CRWProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(DNGImageReaderSpi.class, new DNGProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(MRWImageReaderSpi.class, new MRWProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(NEFImageReaderSpi.class, new NEFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(ORFImageReaderSpi.class, new ORFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(PEFImageReaderSpi.class, new PEFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(RAFImageReaderSpi.class, new RAFProcessor());
            RAWImageReaderSpiSupport.installPostProcessor(SRFImageReaderSpi.class, new SRFProcessor());
            logger.info("Installed RAWProcessor");
          }
        
        else
          {
            logger.warning("This RAWProcessor cannot be installed!");    
            logger.warning(">>>> Found jrawio version: " + jrawioVersion + ", required: " + Version.REQUIRED_JRAWIO_TAG);
          }
      }
  }
