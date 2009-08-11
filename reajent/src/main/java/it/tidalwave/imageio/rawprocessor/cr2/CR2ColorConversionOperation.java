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
package it.tidalwave.imageio.rawprocessor.cr2;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
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
public class CR2ColorConversionOperation extends ColorConversionOperation
  {
    private final static Logger logger = getLogger(CR2ColorConversionOperation.class);

    private final Map<String, ColorMatrix> matrixMapByModel = new HashMap<String, ColorMatrix>();

    public CR2ColorConversionOperation()
      {
        matrixMapByModel.put("CANON EOS 350D DIGITAL", get(new int[] { 6018,-617,-965,-8645,15881,2975,-1530,1719,7642 }));
        matrixMapByModel.put("CANON EOS 10D DIGITAL",  get(new int[] { 8197,-2000,-1118,-6714,14335,2592,-2536,3178,8266  }));
        matrixMapByModel.put("CANON EOS 20Da DIGITAL", get(new int[] { 14155,-5065,-1382,-6550,14633,2039,-1623,1824,6561 }));
        matrixMapByModel.put("CANON EOS 20D DIGITAL",  get(new int[] { 6599,-537,-891,-8071,15783,2424,-1983,2234,7462 }));
        matrixMapByModel.put("CANON EOS 30D DIGITAL",  get(new int[] { 6257,-303,-1000,-7880,15621,2396,-1714,1904,7046 }));
        matrixMapByModel.put("CANON EOS 40D DIGITAL",  get(new int[] { 6071,-747,-856,-7653,15365,2441,-2025,2553,7315 }));
        matrixMapByModel.put("CANON EOS 50D DIGITAL",  get(new int[] { 4920,616,-593,-6493,13964,2784,-1774,3178,7005 }));
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
        // FIXME: try to use the embedded matrix instead of hardwired coefficients.

        return matrixMapByModel.get(model);
      }    
  }
