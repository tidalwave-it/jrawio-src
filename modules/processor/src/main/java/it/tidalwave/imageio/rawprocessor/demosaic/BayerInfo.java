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
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/

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
