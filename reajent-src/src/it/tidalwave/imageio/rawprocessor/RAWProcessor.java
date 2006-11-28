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
 * $Id: RAWProcessor.java,v 1.2 2006/02/25 18:54:21 fabriziogiudici Exp $
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
 * @version CVS $Id: RAWProcessor.java,v 1.2 2006/02/25 18:54:21 fabriziogiudici Exp $
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
