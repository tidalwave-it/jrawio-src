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
 * $Id: DemosaicOperation.java,v 1.2 2006/02/25 16:58:36 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.Curve;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.demosaic.DemosaicFilterProcessor;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DemosaicOperation.java,v 1.2 2006/02/25 16:58:36 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DemosaicOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(DemosaicOperation.class);

    protected String algorithm;

    protected DemosaicFilterProcessor processor;

    private int[] cfaPattern;

    private String cfaPatternAsString;

    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image) throws Exception
      {
        logger.fine("process()");
        cfaPattern = image.getCFAPattern();
        String cfaPatternAsString = image.getCFAPatternAsString();
        algorithm = chooseDemosaicAlgorithm(cfaPatternAsString);
        double redCoeff = image.getRedCoefficient();
        double greenCoeff = image.getGreenCoefficient();
        double blueCoeff = image.getBlueCoefficient();
        double blackLevel = image.getBlackLevel();
        Curve curve = image.getCurve();
        processor = new DemosaicFilterProcessor(cfaPatternAsString, algorithm, redCoeff, greenCoeff, blueCoeff, blackLevel, curve);
        logger.finer("Bayer pattern: " + cfaPatternAsString);
        processor.process(image.getImage());
      }    
    
    /*******************************************************************************
     * 
     * Chooses the demosaic algorithm. Can be overridden if a particular RAW format
     * processor wants to make it own selection.
     * 
     * @return
     * 
     *******************************************************************************/
    protected String chooseDemosaicAlgorithm (String cfaPatternAsString)
      {
        if (!cfaPatternAsString.equals("GRBG")) // FIXME: PixelGrouping does not work yet with other patterns
          {
            return "Bilinear";
          }

        return "PixelGrouping";
      }
  }
