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
package it.tidalwave.imageio.rawprocessor.cr2;

import it.tidalwave.imageio.raw.Source.Type;
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
        // FIXME: put in property file
        matrixMapByModel.put("CANON EOS-1D",           get(new int[] { 6806,-179,-1020,-8097,16415,1687,-3267,4236,7690 }));
        matrixMapByModel.put("CANON EOS-1D MARK II",   get(new int[] { 6264,-582,-724,-8312,15948,2504,-1744,1919,8664 }));
        matrixMapByModel.put("CANON EOS-1D MARK II N", get(new int[] { 6240,-466,-822,-8180,15825,2500,-1801,1938,8042 }));
        matrixMapByModel.put("CANON EOS-1D MARK III",  get(new int[] { 6291,-540,-976,-8350,16145,2311,-1714,1858,7326 }));
        matrixMapByModel.put("CANON EOS-1DS",          get(new int[] { 4374,3631,-1743,-7520,15212,2472,-2892,3632,8161 }));
        matrixMapByModel.put("CANON EOS-1DS MARK II",  get(new int[] { 6517,-602,-867,-8180,15926,2378,-1618,1771,7633 }));
        matrixMapByModel.put("CANON EOS-1DS MARK III", get(new int[] { 5859,-211,-930,-8255,16017,2353,-1732,1887,7448 }));
        matrixMapByModel.put("CANON EOS 5D",           get(new int[] { 6347,-479,-972,-8297,15954,2480,-1968,2131,7649 }));
        matrixMapByModel.put("CANON EOS 5D MARK II",   get(new int[] { 4716,603,-830,-7798,15474,2480,-1496,1937,6651 }));
        matrixMapByModel.put("CANON EOS 10D DIGITAL",  get(new int[] { 8197,-2000,-1118,-6714,14335,2592,-2536,3178,8266  }));
        matrixMapByModel.put("CANON EOS 20Da",         get(new int[] { 14155,-5065,-1382,-6550,14633,2039,-1623,1824,6561 }));
        matrixMapByModel.put("CANON EOS 20D",          get(new int[] { 6599,-537,-891,-8071,15783,2424,-1983,2234,7462 }));
        matrixMapByModel.put("CANON EOS 30D",          get(new int[] { 6257,-303,-1000,-7880,15621,2396,-1714,1904,7046 }));
        matrixMapByModel.put("CANON EOS 40D",          get(new int[] { 6071,-747,-856,-7653,15365,2441,-2025,2553,7315 }));
        matrixMapByModel.put("CANON EOS 50D",          get(new int[] { 4920,616,-593,-6493,13964,2784,-1774,3178,7005 }));
        matrixMapByModel.put("CANON EOS 350D DIGITAL", get(new int[] { 6018,-617,-965,-8645,15881,2975,-1530,1719,7642 }));
        matrixMapByModel.put("CANON EOS 400D DIGITAL", get(new int[] { 7054,-1501,-990,-8156,15544,2812,-1278,1414,7796 }));
        matrixMapByModel.put("CANON EOS 450D",         get(new int[] { 5784,-262,-821,-7539,15064,2672,-1982,2681,7427 }));
        matrixMapByModel.put("CANON EOS 1000D",        get(new int[] { 6771,-1139,-977,-7818,15123,2928,-1244,1437,7533 }));
      }

    @Nonnull
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
        final ColorMatrix matrix = matrixMapByModel.get(model);
        // FIXME: try to use the embedded matrix instead of hardwired coefficients.

        logger.fine("getColorMatrixXYZ(%s) - model: %s, returning %s", artifact, model, matrix);

        return matrix;
      }    
  }
