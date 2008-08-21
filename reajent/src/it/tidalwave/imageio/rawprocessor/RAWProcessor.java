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
 * $Id: RAWProcessor.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.raw.PostProcessor;
import it.tidalwave.imageio.raw.RAWMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RAWProcessor.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public abstract class RAWProcessor implements PostProcessor
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.RAWProcessor";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private List operationList = new ArrayList();
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public RAWProcessor ()
      {
        buildPipeline(operationList);
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected abstract void buildPipeline (List operationList);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public final BufferedImage process (BufferedImage image, RAWMetadataSupport metadata)
      {
        logger.fine("POSTPROCESSING...");
        RAWImage rawImage = new RAWImage(image, metadata);
        init(rawImage);                
        process(rawImage);                
        return rawImage.getImage();
      }

    /*******************************************************************************
     *
     * Initializes the operation pipeline.
     *
     * @param  rawImage  the image to process
     *
     ******************************************************************************/
    private final void init (RAWImage rawImage) 
      {        
        for (Iterator i = operationList.iterator(); i.hasNext(); )
          {
            Operation operation = (Operation)i.next();
            logger.fine(">>> Initializing: " + operation.getClass());
            
            try
              {
                operation.init(rawImage);
              } 
            
            catch (Exception e)
              {
                throw new RuntimeException(e); // FIXME: temporary until we design a declared exception
              }
          }
      }

    /*******************************************************************************
     *
     * Executes the operation pipeline.
     *
     * @param  rawImage  the image to process
     *
     ******************************************************************************/
    private final void process (RAWImage rawImage) 
      {        
        for (Iterator i = operationList.iterator(); i.hasNext(); )
          {
            Operation operation = (Operation)i.next();
            String operationName = operation.getClass().getName();
            
            try
              {
                long time = System.currentTimeMillis();
                logger.info("Executing: " + operationName);
                operation.process(rawImage);
                time = System.currentTimeMillis() - time;
                logger.fine(">>>> " + operationName + " completed ok in " + time + " msec");
              } 
            
            catch (Exception e)
              {
                logger.warning(operationName + " FAILED");
                throw new RuntimeException(e); // FIXME: temporary until we design a declared exception
              }
          }
      }
  }
