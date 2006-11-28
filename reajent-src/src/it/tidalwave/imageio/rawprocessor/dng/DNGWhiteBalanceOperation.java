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
 * $Id: DNGWhiteBalanceOperation.java,v 1.1 2006/02/17 15:32:13 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

import java.util.logging.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DNGWhiteBalanceOperation.java,v 1.1 2006/02/17 15:32:13 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DNGWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(DNGWhiteBalanceOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        
        if (primaryIFD.isAsShotNeutralAvailable())
          {
            TagRational[] asn = primaryIFD.getAsShotNeutral();
            image.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            image.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            image.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
        
        if (primaryIFD.isAnalogBalanceAvailable())
          {
            TagRational[] asn = primaryIFD.getAnalogBalance();
            image.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            image.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            image.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
      }    
  }
