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
 * $Id: DNGWhiteBalanceOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
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
 * @version CVS $Id: DNGWhiteBalanceOperation.java 55 2008-08-21 19:43:51Z fabriziogiudici $
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
