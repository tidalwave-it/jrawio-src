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
//        // TODO: put into a property file
        matrixMapByModel.put("C5050",   get(new int[] { 10508,-3124,-1273,-6079,14294,1901,-1653,2306,6237 }));
        matrixMapByModel.put("C5060",   get(new int[] { 10445,-3362,-1307,-7662,15690,2058,-1135,1176,7602 }));
        matrixMapByModel.put("C7070",   get(new int[] { 10252,-3531,-1095,-7114,14850,2436,-1451,1723,6365 }));
        matrixMapByModel.put("C70",     get(new int[] { 10793,-3791,-1146,-7498,15177,2488,-1390,1577,7321 }));
        matrixMapByModel.put("C80",     get(new int[] { 8606,-2509,-1014,-8238,15714,2703,-942,979,7760 }));
        matrixMapByModel.put("E-10",    get(new int[] { 12745,-4500,-1416,-6062,14542,1580,-1934,2256,6603 }));
        matrixMapByModel.put("E-1",     get(new int[] { 11846,-4767,-945,-7027,15878,1089,-2699,4122,8311 }));
        matrixMapByModel.put("E-20",    get(new int[] { 13173,-4732,-1499,-5807,14036,1895,-2045,2452,7142 }));
        matrixMapByModel.put("E-300",   get(new int[] { 7828,-1761,-348,-5788,14071,1830,-2853,4518,6557 }));
        matrixMapByModel.put("E-330",   get(new int[] { 8961,-2473,-1084,-7979,15990,2067,-2319,3035,8249 }));
        matrixMapByModel.put("E-30",    get(new int[] { 8144,-1861,-1111,-7763,15894,1929,-1865,2542,7607 }));
        matrixMapByModel.put("E-3",     get(new int[] { 9487,-2875,-1115,-7533,15606,2010,-1618,2100,7389 }));
        matrixMapByModel.put("E-400",   get(new int[] { 6169,-1483,-21,-7107,14761,2536,-2904,3580,8568 }));
        matrixMapByModel.put("E-410",   get(new int[] { 8856,-2582,-1026,-7761,15766,2082,-2009,2575,7469 }));
        matrixMapByModel.put("E-420",   get(new int[] { 8746,-2425,-1095,-7594,15612,2073,-1780,2309,7416 }));
        matrixMapByModel.put("E-500",   get(new int[] { 8136,-1968,-299,-5481,13742,1871,-2556,4205,6630 }));
        matrixMapByModel.put("E-510",   get(new int[] { 8785,-2529,-1033,-7639,15624,2112,-1783,2300,7817 }));
        matrixMapByModel.put("E-520",   get(new int[] { 8344,-2322,-1020,-7596,15635,2048,-1748,2269,7287 }));
        matrixMapByModel.put("SP350",   get(new int[] { 12078,-4836,-1069,-6671,14306,2578,-786,939,7418 }));
        matrixMapByModel.put("SP3",     get(new int[] { 11766,-4445,-1067,-6901,14421,2707,-1029,1217,7572 }));
        matrixMapByModel.put("SP500UZ", get(new int[] { 9493,-3415,-666,-5211,12334,3260,-1548,2262,6482 }));
        matrixMapByModel.put("SP510UZ", get(new int[] { 10593,-3607,-1010,-5881,13127,3084,-1200,1805,6721 }));
        matrixMapByModel.put("SP550UZ", get(new int[] { 11597,-4006,-1049,-5432,12799,2957,-1029,1750,6516 }));
        matrixMapByModel.put("SP560UZ", get(new int[] { 10915,-3677,-982,-5587,12986,2911,-1168,1968,6223 }));
        matrixMapByModel.put("SP570UZ", get(new int[] { 11522,-4044,-1146,-4736,12172,2904,-988,1829,6039 }));
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
