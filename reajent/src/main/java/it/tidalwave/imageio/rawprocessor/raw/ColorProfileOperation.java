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
 * $Id: ColorProfileOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import it.tidalwave.imageio.util.Logger;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorConvertOp;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.OperationSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ColorProfileOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 ******************************************************************************/
public class ColorProfileOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(ColorProfileOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     * FIXME: temporary: always converts in sRGB to 8 bits per channel
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        logImage(logger, ">>>> image: ", image.getImage());
        ICC_Profile colorProfile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        RenderingHints hints = null; // FIXME
        ColorConvertOp ccOp = new ColorConvertOp(new ICC_Profile[] { colorProfile }, hints);
        image.setImage(ccOp.filter(image.getImage(), null));
        logImage(logger, ">>>> process returning: ", image.getImage());
      }    
  }
