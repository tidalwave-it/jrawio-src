/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.rawprocessor.crw;

import it.tidalwave.imageio.crw.CRWMetadata;
import it.tidalwave.imageio.raw.Directory;
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
public class CRWColorConversionOperation extends ColorConversionOperation
  {
    private final static Logger logger = getLogger(CRWColorConversionOperation.class);

    private final Map<String, ColorMatrix> matrixMapByModel = new HashMap<String, ColorMatrix>();

    public CRWColorConversionOperation()
      {
        matrixMapByModel.put("CANON EOS D30 DIGITAL",  get(new int[] { 9805,-2689,-1312,-5803,13064,3068,-2438,3075,8775 }));
        matrixMapByModel.put("CANON EOS D60 DIGITAL",  get(new int[] { 6188,-1341,-890,-7168,14489,2937,-2640,3228,8483 }));
        matrixMapByModel.put("CANON EOS 300D DIGITAL", get(new int[] { 8197,-2000,-1118,-6714,14335,2592,-2536,3178,8266 }));
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
        final CRWMetadata metadata = (CRWMetadata)artifact.getRAWMetadata();
        final String model = metadata.getModel().toUpperCase().trim();
        // FIXME: try to use the embedded matrix instead of hardwired coefficients.

        return matrixMapByModel.get(model);
      }    
  }
