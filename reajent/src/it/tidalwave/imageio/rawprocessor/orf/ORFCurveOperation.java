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
 * $Id: ORFCurveOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.orf;

import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ORFCurveOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFCurveOperation extends CurveOperation
  {
    private int[] blackLevels;
    
    private int validBits;
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void init (RAWImage image) throws Exception
      {        
        OlympusMakerNote orfMakernote = (OlympusMakerNote)image.getRAWMetadata().getMakerNote();
        blackLevels = orfMakernote.getBlackLevel();
        validBits = orfMakernote.getValidBits();
        
        String model = ((TIFFMetadataSupport)image.getRAWMetadata()).getPrimaryIFD().getModel();
        model = model.toUpperCase().trim();
        
        if ("E-1".equals(model) || "E-10".equals(model)) // FIXME: don't know why
          {
            validBits = 16;   
          }
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected int[] getBlackLevel(RAWImage image)
      {
        int blackLevel = (blackLevels[0] + blackLevels[1] + blackLevels[2] + blackLevels[3]) / 4;
        return new int[] { blackLevel, blackLevel, blackLevel };
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        double whiteLevel = (1 << validBits) - 1;
        
        return whiteLevel;
      }
  }
