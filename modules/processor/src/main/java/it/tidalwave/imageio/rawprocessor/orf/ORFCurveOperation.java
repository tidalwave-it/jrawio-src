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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFCurveOperation extends CurveOperation
  {
    @CheckForNull
    private OlympusMakerNote orfMakernote;
    
    @CheckForNull
    private String model;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void init (@Nonnull final PipelineArtifact artifact)
      throws Exception
      {       
        orfMakernote = (OlympusMakerNote)artifact.getRAWMetadata().getMakerNote();
        model = ((TIFFMetadataSupport)artifact.getRAWMetadata()).getPrimaryIFD().getModel().toUpperCase().trim();
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected int[] getBlackLevel (@Nonnull final PipelineArtifact artifact)
      {
        final int[] blackLevels = new int[]{ 0, 0, 0 };

        if (orfMakernote.isBlackLevelAvailable())
          {
            final int[] orfBlackLevel = orfMakernote.getBlackLevel();
            // FIXME: ORF has got 4 black levels; don't know if the channel matching is correct
            blackLevels[0] = orfBlackLevel[0];
            blackLevels[1] = orfBlackLevel[1];
            blackLevels[2] = orfBlackLevel[2];
          }
//        final int blackLevel = (blackLevels[0] + blackLevels[1] + blackLevels[2] + blackLevels[3]) / 4;
//        return new int[] { blackLevel, blackLevel, blackLevel };
        
        return blackLevels;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected double getWhiteLevel (@Nonnull final PipelineArtifact artifact)
      {
        // FIXME: everything is temporary
        int validBits = 12;

        if (orfMakernote.isValidBitsAvailable())
          {
            validBits = orfMakernote.getValidBits()[0];
          }

        if ("E-1".equals(model) || "E-10".equals(model)) // FIXME: don't know why
          {
            validBits = 16;
          }
        
        return (1 << validBits) - 1;
      }
  }
