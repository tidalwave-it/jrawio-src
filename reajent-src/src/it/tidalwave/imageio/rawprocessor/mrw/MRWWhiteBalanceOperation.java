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
 * $Id: MRWWhiteBalanceOperation.java,v 1.1 2006/02/25 13:24:47 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.mrw;

import it.tidalwave.imageio.mrw.MRWHeaderProcessor;
import java.util.logging.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWWhiteBalanceOperation.java,v 1.1 2006/02/25 13:24:47 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class MRWWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(MRWWhiteBalanceOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        logger.fine("process()");
        MRWHeaderProcessor mrwHeaderProcessor = (MRWHeaderProcessor)image.getRAWMetadata().getHeaderProcessor();
        double[] coefficients = mrwHeaderProcessor.getCoefficients();
        image.multiplyRedCoefficient(coefficients[0]);
        image.multiplyGreenCoefficient(coefficients[1]);
        image.multiplyBlueCoefficient(coefficients[2]);
      }    
  }
