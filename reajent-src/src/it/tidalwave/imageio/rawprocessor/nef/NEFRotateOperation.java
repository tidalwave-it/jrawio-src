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
 * $Id: NEFRotateOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.util.logging.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.raw.RotateOperation;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFRotateOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFRotateOperation extends RotateOperation
  {
    private final static Logger logger = getLogger(NEFRotateOperation.class);
    
    private NikonCaptureEditorMetadata nceMetadata;
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void init (RAWImage image) throws Exception
      {
        super.init(image);
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();
      }
                
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected int getCameraOrientation (RAWImage image)
      {
        return (nceMetadata != null) ? nceMetadata.getOrientation() : super.getCameraOrientation(image);      
      }
  }
