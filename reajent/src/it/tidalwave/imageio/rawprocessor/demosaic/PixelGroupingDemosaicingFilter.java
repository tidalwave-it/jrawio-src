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
 * $Id: PixelGroupingDemosaicingFilter.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: PixelGroupingDemosaicingFilter.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 * FIXME: it works only for the D100 bayer pattern, should be generalized.
 *
 ******************************************************************************/
public class PixelGroupingDemosaicingFilter extends DemosaicingFilter
  {
    private int tl;
    private int t;
    private int tr;
    private int l;
    private int r;
    private int bl;
    private int b;
    private int br;
    
    private int R_OFFSET;
    private int G_OFFSET;
    private int B_OFFSET;

    /*******************************************************************************
     *
     ******************************************************************************/

    public void filter (Raster raster,
                        BayerInfo bayerInfo)
      {
        int rsStride = bayerInfo.getRedSampleStride();
        int gs1Stride = bayerInfo.getGreenSample1Stride();
        int gs2Stride = bayerInfo.getGreenSample2Stride();
        int bsStride = bayerInfo.getBlueSampleStride();
        //        int rihStride = bayerInfo.getRedHInterpStride();
        //        int rivStride = bayerInfo.getRedVInterpStride();
        //        int ricStride = bayerInfo.getRedCInterpStride();
        //        int gi1Stride = bayerInfo.getGreenInterp1Stride();
        //        int gi2Stride = bayerInfo.getGreenInterp2Stride();
        //        int bihStride = bayerInfo.getBlueHInterpStride();
        //        int bivStride = bayerInfo.getBlueVInterpStride();
        //        int bicStride = bayerInfo.getBlueCInterpStride();

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int pixelStride = bayerInfo.getPixelStride();
        int scanStride = bayerInfo.getScanlineStride();
        int margin = 1;
        R_OFFSET = bayerInfo.getRedOffset();
        G_OFFSET = bayerInfo.getGreenOffset();
        B_OFFSET = bayerInfo.getBlueOffset();

        tl = -scanStride - pixelStride; // top left
        t = -scanStride; // top
        tr = -scanStride + pixelStride; // top right
        l = -pixelStride; // left
        r = +pixelStride; // right
        bl = +scanStride - pixelStride; // bottom left
        b = +scanStride; // bottom
        br = +scanStride + pixelStride; // bottom right

        int top = margin * 2;
        int bottom = h - margin * 2;
        int OFFSET1 = 4 * 2;
        int OFFSET2 = OFFSET1 + 4 * 2;

        for (int y = 0; y < h + OFFSET2; y += 2)
          {
            int y1 = y;
            int y2 = y - OFFSET1;
            int y3 = y - OFFSET2;

            if ((y1 >= 0) && (y1 < h))
              {
                for (int x = 0; x < w; x += 2)
                  {
                    int c = x * pixelStride + y1 * scanStride; // center
                    int rs = c + rsStride;
                    int gs1 = c + gs1Stride;
                    int gs2 = c + gs2Stride;
                    int bs = c + bsStride;
                    //
                    // Warning: this stuff is signed short, even if you treat it as unsigned. They need masking.
                    //
                    data[rs] = applyCoefficientAndCurve(redCoefficient, data[rs] & SHORT_MASK);
                    data[gs1] = applyCoefficientAndCurve(greenCoefficient, data[gs1] & SHORT_MASK);
                    data[gs2] = applyCoefficientAndCurve(greenCoefficient, data[gs2] & SHORT_MASK);
                    data[bs] = applyCoefficientAndCurve(blueCoefficient, data[bs] & SHORT_MASK);
                  }
              }

            if ((y2 >= top) && (y2 < bottom))
              {
                for (int x = margin * 2; x < w - margin * 2; x += 2)
                  {
                    int c = x * pixelStride + y2 * scanStride;
                    interpolateGreen(data, c + r, R_OFFSET);
                    interpolateGreen(data, c + b, B_OFFSET);
                  }
              }

            if ((y3 >= top) && (y3 < bottom))
              {
                for (int x = margin * 2; x < w - margin * 2; x += 2)
                  {
                    int c = x * pixelStride + y3 * scanStride;
                    interpolateRedBlueAtGreen(data, c, R_OFFSET, B_OFFSET);
                    interpolateRedBlueAtGreen(data, c + br, B_OFFSET, R_OFFSET);
                    interpolateRedXBlue(data, c + r, B_OFFSET, R_OFFSET);
                    interpolateRedXBlue(data, c + b, R_OFFSET, B_OFFSET);
                  }
              }

            Thread.yield();
          }
      }

    /*******************************************************************************
     *
     * Interpolates GREEN channel.
     *
     * @param  chOffset  which channel is a native sample at the interp. position
     *
     ******************************************************************************/

    private void interpolateGreen (short[] data,
                                   int c,
                                   int chOffset)
      {
        int rt2 = data[c + t * 2 + chOffset] & SHORT_MASK;
        int rb2 = data[c + b * 2 + chOffset] & SHORT_MASK;
        int rl2 = data[c + l * 2 + chOffset] & SHORT_MASK;
        int rr2 = data[c + r * 2 + chOffset] & SHORT_MASK;
        int rc = data[c + chOffset] & SHORT_MASK;

        int gt = data[c + t + G_OFFSET] & SHORT_MASK;
        int gb = data[c + b + G_OFFSET] & SHORT_MASK;
        int gl = data[c + l + G_OFFSET] & SHORT_MASK;
        int gr = data[c + r + G_OFFSET] & SHORT_MASK;

        int deltaN = Math.abs(rt2 - rc) * 2 + Math.abs(gt - gb);
        int deltaE = Math.abs(rr2 - rc) * 2 + Math.abs(gl - gr);
        int deltaW = Math.abs(rl2 - rc) * 2 + Math.abs(gl - gr);
        int deltaS = Math.abs(rb2 - rc) * 2 + Math.abs(gt - gb);

        int deltaMin = Math.min(deltaN, Math.min(deltaE, Math.min(deltaW, deltaS)));
        int green = 0;

        if (deltaMin == deltaN)
          {
            green = (gt * 3 + rc + gb - rt2) / 4;
          }

        else if (deltaMin == deltaE)
          {
            green = (gr * 3 + rc + gl - rr2) / 4;
          }

        else if (deltaMin == deltaW)
          {
            green = (gl * 3 + rc + gr - rl2) / 4;
          }

        else if (deltaMin == deltaS)
          {
            green = (gb * 3 + rc + gt - rb2) / 4;
          }

        if (green < 0)
          {
            green = 0;
          }

        data[c + G_OFFSET] = (short)((green < SHORT_MASK) ? green : SHORT_MASK);
      }

    /*******************************************************************************
     *
     * Interpolates RED and BLUE pixels at GREEN positions.
     *
     * @param  chOffsetH  which channel should be interpolated horizontally
     * @param  chOffsetV  which channel should be interpolated vertically
     *
     ******************************************************************************/

    private void interpolateRedBlueAtGreen (short[] data,
                                            int c,
                                            int chOffsetH,
                                            int chOffsetV)
      {
        int gsc = data[c + G_OFFSET] & SHORT_MASK;
        int gsl = data[c + l + G_OFFSET] & SHORT_MASK;
        int gsr = data[c + r + G_OFFSET] & SHORT_MASK;
        int gst = data[c + t + G_OFFSET] & SHORT_MASK;
        int gsb = data[c + b + G_OFFSET] & SHORT_MASK;

        int hsl = data[c + l + chOffsetH] & SHORT_MASK;
        int hsr = data[c + r + chOffsetH] & SHORT_MASK;

        int vst = data[c + t + chOffsetV] & SHORT_MASK;
        int vsb = data[c + b + chOffsetV] & SHORT_MASK;

        int hh = hueTransit(gsl, gsc, gsr, hsl, hsr);
        int vv = hueTransit(gst, gsc, gsb, vst, vsb);

        if (hh < 0)
          {
            hh = 0;
          }

        if (vv < 0)
          {
            vv = 0;
          }

        data[c + chOffsetH] = (short)((hh < SHORT_MASK) ? hh : SHORT_MASK);
        data[c + chOffsetV] = (short)((vv < SHORT_MASK) ? vv : SHORT_MASK);
      }

    /*******************************************************************************
     *
     ******************************************************************************/

    private void interpolateRedXBlue (short[] data,
                                      int c,
                                      int chOffset,
                                      int chOffset2)
      {
        int gsc = data[c + G_OFFSET] & SHORT_MASK;
        int gstr = data[c + tr + G_OFFSET] & SHORT_MASK;
        int gsbl = data[c + bl + G_OFFSET] & SHORT_MASK;
        int gstl = data[c + tl + G_OFFSET] & SHORT_MASK;
        int gsbr = data[c + br + G_OFFSET] & SHORT_MASK;

        int bstr = data[c + tr + chOffset] & SHORT_MASK;
        int bsbl = data[c + bl + chOffset] & SHORT_MASK;
        int bstl = data[c + tl + chOffset] & SHORT_MASK;
        int bsbr = data[c + br + chOffset] & SHORT_MASK;

        int rsc = data[c + chOffset2] & SHORT_MASK;
        int rstr2 = data[c + tr * 2 + chOffset2] & SHORT_MASK;
        int rsbl2 = data[c + bl * 2 + chOffset2] & SHORT_MASK;
        int rstl2 = data[c + tl * 2 + chOffset2] & SHORT_MASK;
        int rsbr2 = data[c + br * 2 + chOffset2] & SHORT_MASK;

        int deltaNE = Math.abs(bstr - bsbl) + Math.abs(rstr2 - rsc) + Math.abs(rsbl2 - rsc) + Math.abs(gstr - gsc)
            + Math.abs(gsbl - gsc);

        int deltaNW = Math.abs(bstl - bsbr) + Math.abs(rstl2 - rsc) + Math.abs(rsbr2 - rsc) + Math.abs(gstl - gsc)
            + Math.abs(gsbr - gsc);

        int cc = 0;

        if (deltaNE <= deltaNW)
          {
            cc = hueTransit(gstr, gsc, gsbl, bstr, bsbl);
          }

        else
          {
            cc = hueTransit(gstl, gsc, gsbr, bstl, bsbr);
          }

        if (cc < 0)
          {
            cc = 0;
          }

        data[c + chOffset] = (short)((cc < SHORT_MASK) ? cc : SHORT_MASK);
      }

    /*******************************************************************************
     *
     ******************************************************************************/

    private static int hueTransit (int l1,
                                   int l2,
                                   int l3,
                                   int v1,
                                   int v3)
      {
        if (((l1 < l2) && (l2 < l3)) || ((l1 > l2) && (l2 > l3)))
          {
            long x = (long)(v3 - v1) * (long)(l2 - l1);
            return v1 + (int)(x / (l3 - l1));
          }

        else
          {
            return (v1 + v3) / 2 + (l2 * 2 - l1 - l3) / 4;
          }
      }
  }
