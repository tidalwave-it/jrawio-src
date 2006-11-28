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
 * $Id: BilinearDemosaicingFilter.java,v 1.1 2006/02/17 15:32:05 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: BilinearDemosaicingFilter.java,v 1.1 2006/02/17 15:32:05 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class BilinearDemosaicingFilter extends DemosaicingFilter
  {
    public void filter (Raster raster,
                        BayerInfo bayerInfo)
      {
        int rsStride = bayerInfo.getRedSampleStride();
        int gs1Stride = bayerInfo.getGreenSample1Stride();
        int gs2Stride = bayerInfo.getGreenSample2Stride();
        int bsStride = bayerInfo.getBlueSampleStride();
        int rihStride = bayerInfo.getRedHInterpStride();
        int rivStride = bayerInfo.getRedVInterpStride();
        int ricStride = bayerInfo.getRedCInterpStride();
        int gi1Stride = bayerInfo.getGreenInterp1Stride();
        int gi2Stride = bayerInfo.getGreenInterp2Stride();
        int bihStride = bayerInfo.getBlueHInterpStride();
        int bivStride = bayerInfo.getBlueVInterpStride();
        int bicStride = bayerInfo.getBlueCInterpStride();

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int pixelStride = bayerInfo.getPixelStride();
        int scanStride = bayerInfo.getScanlineStride();
        int margin = 1;

        int tl = -scanStride - pixelStride; // top left
        int t = -scanStride; // top
        int tr = -scanStride + pixelStride; // top right
        int l = -pixelStride; // left
        int r = +pixelStride; // right
        int bl = +scanStride - pixelStride; // bottom left
        int b = +scanStride; // bottom
        int br = +scanStride + pixelStride; // bottom right

        // use w,h -1 since some sensors have an odd number of columns/rows
        for (int y = 0; y < h - 1; y += 2)
          {
            for (int x = 0; x < w - 1; x += 2)
              {
                int c = (x * pixelStride) + (y * scanStride);
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

            Thread.yield();
          }

        for (int y = margin * 2; y < (h - (margin * 2)); y += 2)
          {
            for (int x = margin * 2; x < (w - (margin * 2)); x += 2)
              {
                int c = (x * pixelStride) + (y * scanStride); // center
                int rih = c + rihStride;
                int riv = c + rivStride;
                int ric = c + ricStride;
                int gi1 = c + gi1Stride;
                int gi2 = c + gi2Stride;
                int bih = c + bihStride;
                int biv = c + bivStride;
                int bic = c + bicStride;

                //
                // Warning: this stuff is signed short, even if you treat it as unsigned. They need masking.
                // You should not apply the curve here, you're interpolating data to which curve
                // has been already applied in the previous step.
                //
                int drih = ((data[rih + l] & SHORT_MASK) + (data[rih + r] & SHORT_MASK)) / 2;
                int driv = ((data[riv + t] & SHORT_MASK) + (data[riv + b] & SHORT_MASK)) / 2;
                int dric = ((data[ric + tl] & SHORT_MASK) + (data[ric + tr] & SHORT_MASK)
                    + (data[ric + bl] & SHORT_MASK) + (data[ric + br] & SHORT_MASK)) / 4;
                int dgi1 = ((data[gi1 + t] & SHORT_MASK) + (data[gi1 + l] & SHORT_MASK) + (data[gi1 + r] & SHORT_MASK) + (data[gi1
                    + b] & SHORT_MASK)) / 4;
                int dgi2 = ((data[gi2 + t] & SHORT_MASK) + (data[gi2 + l] & SHORT_MASK) + (data[gi2 + r] & SHORT_MASK) + (data[gi2
                    + b] & SHORT_MASK)) / 4;
                int dbih = ((data[bih + l] & SHORT_MASK) + (data[bih + r] & SHORT_MASK)) / 2;
                int dbiv = ((data[biv + t] & SHORT_MASK) + (data[biv + b] & SHORT_MASK)) / 2;
                int dbic = ((data[bic + tl] & SHORT_MASK) + (data[bic + tr] & SHORT_MASK)
                    + (data[bic + bl] & SHORT_MASK) + (data[bic + br] & SHORT_MASK)) / 4;

                data[rih] = (short)((drih <= SHORT_MASK) ? drih : SHORT_MASK);
                data[riv] = (short)((driv <= SHORT_MASK) ? driv : SHORT_MASK);
                data[ric] = (short)((dric <= SHORT_MASK) ? dric : SHORT_MASK);
                data[gi1] = (short)((dgi1 <= SHORT_MASK) ? dgi1 : SHORT_MASK);
                data[gi2] = (short)((dgi2 <= SHORT_MASK) ? dgi2 : SHORT_MASK);
                data[bih] = (short)((dbih <= SHORT_MASK) ? dbih : SHORT_MASK);
                data[biv] = (short)((dbiv <= SHORT_MASK) ? dbiv : SHORT_MASK);
                data[bic] = (short)((dbic <= SHORT_MASK) ? dbic : SHORT_MASK);
              }

            Thread.yield();
          }
      }
  }
