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
 * $Id: BayerInfo.java,v 1.1 2006/02/17 15:32:04 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: BayerInfo.java,v 1.1 2006/02/17 15:32:04 fabriziogiudici Exp $
 *
 ******************************************************************************/

public abstract class BayerInfo
  {
    protected int pixelStride;
    protected int scanlineStride;
    protected int redOffset;
    protected int greenOffset;
    protected int blueOffset;

    protected int redSampleStride;
    protected int greenSample1Stride;
    protected int greenSample2Stride;
    protected int blueSampleStride;
    protected int redHInterpStride;
    protected int redVInterpStride;
    protected int redCInterpStride;
    protected int greenInterp1Stride;
    protected int greenInterp2Stride;
    protected int blueHInterpStride;
    protected int blueVInterpStride;
    protected int blueCInterpStride;

    public abstract void computeSampleStrides();

    public final void setSampleModel (SampleModel sampleModel)
      {
        PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel)sampleModel;
        pixelStride = sm.getPixelStride();
        scanlineStride = sm.getScanlineStride();
        int[] bo = sm.getBandOffsets();
        redOffset   = bo[0] % pixelStride;
        greenOffset = bo[1] % pixelStride;
        blueOffset  = bo[2] % pixelStride;
        computeSampleStrides();
      }

    public int getPixelStride()
      {
        return pixelStride;
      }

    public int getScanlineStride()
      {
        return scanlineStride;
      }

    public int getRedOffset()
      {
        return redOffset;
      }

    public int getGreenOffset()
      {
        return greenOffset;
      }

    public int getBlueOffset()
      {
        return blueOffset;
      }

    public int getRedSampleStride()
      {
        return redSampleStride;
      }

    public int getGreenSample1Stride()
      {
        return greenSample1Stride;
      }

    public int getGreenSample2Stride()
      {
        return greenSample2Stride;
      }

    public int getBlueSampleStride()
      {
        return blueSampleStride;
      }

    public int getRedHInterpStride()
      {
        return redHInterpStride;
      }

    public int getRedVInterpStride()
      {
        return redVInterpStride;
      }

    public int getRedCInterpStride()
      {
        return redCInterpStride;
      }

    public int getGreenInterp1Stride()
      {
        return greenInterp1Stride;
      }

    public int getGreenInterp2Stride()
      {
        return greenInterp2Stride;
      }

    public int getBlueHInterpStride()
      {
        return blueHInterpStride;
      }

    public int getBlueVInterpStride()
      {
        return blueVInterpStride;
      }

    public int getBlueCInterpStride()
      {
        return blueCInterpStride;
      }
  }
