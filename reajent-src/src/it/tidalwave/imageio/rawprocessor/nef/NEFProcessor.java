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
 * $Id: NEFProcessor.java,v 1.1 2006/02/17 15:32:11 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.util.List;
import java.util.logging.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.RAWProcessor;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.raw.ColorProfileOperation;
import it.tidalwave.imageio.rawprocessor.raw.DemosaicOperation;
import it.tidalwave.imageio.rawprocessor.raw.RotateOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFProcessor.java,v 1.1 2006/02/17 15:32:11 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFProcessor extends RAWProcessor
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.nef.BindNCEMetadata";
     
    private final static Logger logger = Logger.getLogger(CLASS);
        
    class BindNCEMetadata extends OperationSupport
      {
        public void init (RAWImage image) throws Exception
          {
            NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
            NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
            
            if (makerNote.isCaptureEditorDataAvailable())
              {
                NikonCaptureEditorMetadata nceMetadata = new NikonCaptureEditorMetadata(makerNote.getCaptureEditorData()); 
                logger.finer(nceMetadata.toString());
                metadata._setCaptureEditorMetadata(nceMetadata); 
              }            
          }        

        public void process (RAWImage image) throws Exception
          {
          }        
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected void buildPipeline (List operationList)
      {
        operationList.add(new BindNCEMetadata());
        operationList.add(new NEFWhiteBalanceOperation());
        operationList.add(new NEFExposureOperation());
        operationList.add(new NEFCurveOperation());
        operationList.add(new DemosaicOperation());
        operationList.add(new NEFSizeOperation());
        operationList.add(new NEFRotateOperation());
        operationList.add(new NEFColorConversionOperation());
        operationList.add(new ColorProfileOperation());
//        operationList.add(new SharpenOperation());
      }
  }
