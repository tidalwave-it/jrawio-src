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
package it.tidalwave.imageio.rawprocessor;

import it.tidalwave.imageio.raw.Source.Type;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.raw.PostProcessor;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.raw.Source;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class RAWProcessor implements PostProcessor
  {
    private final static String CLASS = RAWProcessor.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private final List<OperationSupport> operationList = new ArrayList<OperationSupport>();
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public RAWProcessor()
      {
        buildPipeline(operationList);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected abstract void buildPipeline (@Nonnull List<OperationSupport> operationList);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    @Nonnull
    public final BufferedImage process (final @Nonnull BufferedImage image,
                                        final @Nonnull RAWMetadataSupport metadata,
                                        final @Nonnull RAWImageReadParam readParam)
      {
        logger.fine("process(%s, %s, %s)", image, metadata, readParam);
        PipelineArtifact rawImage = new PipelineArtifact(image, metadata, readParam);
        init(rawImage);                
        process(rawImage);                
        return rawImage.getImage();
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public final void processMetadata (final @Nonnull RAWMetadataSupport metadata,
                                       final @Nonnull RAWImageReadParam readParam)
      {
        logger.fine("processMetadata(%s, %s)", metadata, readParam);
        PipelineArtifact rawImage = new PipelineArtifact(null, metadata, readParam);
        init(rawImage);
        processMetadata(rawImage);
      }

    /*******************************************************************************************************************
     *
     * Initializes the operation pipeline.
     *
     * @param  pipelineArtifact  the image to process
     *
     ******************************************************************************************************************/
    private final void init (PipelineArtifact rawImage)
      {        
        for (Iterator i = operationList.iterator(); i.hasNext(); )
          {
            Operation operation = (Operation)i.next();
            logger.fine(">>> Initializing: %s", operation.getClass());
            
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

    /*******************************************************************************************************************
     *
     * Executes the operation pipeline.
     *
     * @param  pipelineArtifact  the image to process
     *
     ******************************************************************************************************************/
    private final void process (final @Nonnull PipelineArtifact pipelineArtifact)
      {
        for (final Operation operation : operationList)
          {
            final String operationName = operation.getClass().getName();

            try
              {
                final Type type = pipelineArtifact.lookup(Source.class).getType();

                if (!operation.supportsType(type))
                  {
                    logger.fine(">>>> skipping %s because doesn't support %s\n", operationName, type);
                  }
                else
                  {
                    long time = System.currentTimeMillis();
                    logger.fine("Executing: %s", operationName);
                    operation.process(pipelineArtifact);
                    time = System.currentTimeMillis() - time;
                    logger.finer(">>>> %s completed ok in %d msec", operationName, time);
                  }
              }

            catch (Exception e)
              {
                logger.warning("%s FAILED", operationName);
                throw new RuntimeException(e); // FIXME: temporary until we design a declared exception
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Executes the operation pipeline.
     *
     * @param  pipelineArtifact  the image to process
     *
     ******************************************************************************************************************/
    private final void processMetadata (final @Nonnull PipelineArtifact rawImage)
      {
        for (final Operation operation : operationList)
          {
            final String operationName = operation.getClass().getName();

            try
              {
                long time = System.currentTimeMillis();
                logger.fine("Executing: %s", operationName);
                operation.processMetadata(rawImage);
                time = System.currentTimeMillis() - time;
                logger.finer(">>>> %s completed ok in %d msec", operationName, time);
              }

            catch (Exception e)
              {
                logger.warning("%s FAILED", operationName);
                throw new RuntimeException(e); // FIXME: temporary until we design a declared exception
              }
          }
      }
  }
