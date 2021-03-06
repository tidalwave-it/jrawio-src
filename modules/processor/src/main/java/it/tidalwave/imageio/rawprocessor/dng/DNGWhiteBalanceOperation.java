/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

import it.tidalwave.imageio.raw.Source;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(DNGWhiteBalanceOperation.class);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DNGWhiteBalanceOperation()
      {
        super(Source.Type.RAW);
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (PipelineArtifact artifact)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)artifact.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        
        if (primaryIFD.isAsShotNeutralAvailable())
          {
            TagRational[] asn = primaryIFD.getAsShotNeutral();
            artifact.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            artifact.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            artifact.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
        
        if (primaryIFD.isAnalogBalanceAvailable())
          {
            TagRational[] asn = primaryIFD.getAnalogBalance();
            artifact.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            artifact.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            artifact.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
      }    
  }
