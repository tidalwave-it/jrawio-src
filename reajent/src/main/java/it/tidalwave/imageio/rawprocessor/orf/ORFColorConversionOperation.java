/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.orf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.rawprocessor.raw.ColorConversionOperation;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFColorConversionOperation extends ColorConversionOperation
  {
    private final static Logger logger = getLogger(ORFColorConversionOperation.class);

    private final Map<String, ColorMatrix> matrixMapByModel = new HashMap<String, ColorMatrix>();

    public ORFColorConversionOperation()
      {
        matrixMapByModel.put("E-500", get(new int[] { 8136,-1968,-299,-5481,13742,1871,-2556,4205,6630 }));
        matrixMapByModel.put("E-510", get(new int[] { 8785,-2529,-1033,-7639,15624,2112,-1783,2300,7817 }));
        matrixMapByModel.put("E-520", get(new int[] { 8344,-2322,-1020,-7596,15635,2048,-1748,2269,7287 }));
      }

    private static ColorMatrix get (final @Nonnull int[] values)
      {
        return getMatrix(values, 1.0/10000.0);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @CheckForNull
    @Override
    protected ColorMatrix getColorMatrixXYZ (final @Nonnull PipelineArtifact artifact)
      {
        final String model = ((TIFFMetadataSupport)artifact.getRAWMetadata()).getPrimaryIFD().getModel().toUpperCase().trim();
        final OlympusMakerNote orfMakernote = (OlympusMakerNote)artifact.getRAWMetadata().getMakerNote();

        // FIXME: try to use the embedded matrix instead of hardwired coefficients.
//        if (orfMakernote.isColorMatrixAvailable())
//          {
//            final int[] colorMatrix = orfMakernote.getColorMatrix();
//
////            for (int i = 0; i < colorMatrix.length; i++)
////              {
////                colorMatrix[i] = 65535 - colorMatrix[i];
////              }
//
//            return getMatrix(colorMatrix, 1.0/65535.0);
//          }

        return matrixMapByModel.get(model);
      }    
  }
