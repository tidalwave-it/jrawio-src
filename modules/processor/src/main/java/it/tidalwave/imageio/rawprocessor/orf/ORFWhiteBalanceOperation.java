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
package it.tidalwave.imageio.rawprocessor.orf;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.orf.ImageProcessing;
import it.tidalwave.imageio.raw.Source.Type;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFWhiteBalanceOperation extends OperationSupport
  {
    private final static String CLASS = ORFWhiteBalanceOperation.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public ORFWhiteBalanceOperation()
      {
        super(Type.RAW);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void process (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("process(%s)", artifact);
        final OlympusMakerNote makerNote = (OlympusMakerNote)artifact.getRAWMetadata().getMakerNote();
        
        if (makerNote != null)
          {
            if (makerNote.isRedBalanceAvailable() && makerNote.isBlueBalanceAvailable())
              {
                logger.finest(">>>> RB from makerNote.{red,blue}Balance");
                artifact.multiplyRedCoefficient(makerNote.getRedBalance()[0] / 256.0);
                artifact.multiplyBlueCoefficient(makerNote.getBlueBalance()[0] / 256.0);
              }
            else if (makerNote.isImageProcessingAvailable())
              {
                logger.finest(">>>> RB from makerNote.imageProcessing");
                final ImageProcessing imageProcessing = makerNote.getOlympusImageProcessing();

                if ((imageProcessing != null) && imageProcessing.isRBCoefficientsAvailable())
                  {
                    final int[] rbCoefficients = imageProcessing.getRBCoefficients();
                    artifact.multiplyRedCoefficient(rbCoefficients[0] / 256.0);
                    artifact.multiplyBlueCoefficient(rbCoefficients[1] / 256.0);
                  }
              }
          }
      }    
  }
