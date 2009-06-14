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
 * $Id: ORFColorConversionOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.orf;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.ColorConversionOperation;
import it.tidalwave.imageio.orf.OlympusMakerNote;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ORFColorConversionOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFColorConversionOperation extends ColorConversionOperation
  {
    private final static Logger logger = getLogger(ORFColorConversionOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
/*    protected ColorMatrix getColorMatrix (RAWImage image)
      {
        OlympusMakerNote orfMakernote = (OlympusMakerNote)image.getRAWMetadata().getMakerNote();
        
        if (orfMakernote.isColourMatrixAvailable())
          {
            int[] colorMatrix = orfMakernote.getColourMatrix();  
            return getMatrix(colorMatrix, 1.0/65535.0);
          }          
        
        return null;
      }    */
  }
