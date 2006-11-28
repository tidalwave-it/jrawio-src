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
 * $Id: ColorProfileOperation.java,v 1.1 2006/02/17 15:31:54 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.logging.Logger;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorConvertOp;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.OperationSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: ColorProfileOperation.java,v 1.1 2006/02/17 15:31:54 fabriziogiudici Exp $
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
