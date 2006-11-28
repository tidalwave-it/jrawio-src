/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: PEFCurveOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.pef;

import it.tidalwave.imageio.pef.PentaxMakerNote;
import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: PEFCurveOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class PEFCurveOperation extends CurveOperation  
  {
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected int[] getBlackLevel (RAWImage image)
      {
        PentaxMakerNote pefMakernote = (PentaxMakerNote)image.getRAWMetadata().getMakerNote();
        int[] blackLevels = pefMakernote.getBlackLevel();
        int blackLevel = (blackLevels[0] + blackLevels[1] + blackLevels[2] + blackLevels[3]) / 4;
        return new int[] { blackLevel, blackLevel, blackLevel };
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        return 4095; // FIXME
      }
  }
