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
 * $Id: BayerInfo_GRBG.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: BayerInfo_GRBG.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 *
 *    Indexes
 *
 *    tl t tr
 *    l  c  r
 *    bl b br
 *
 *      Bayer GRGB pattern:
 *       x  x+1 x+2 x+3
 *     . . . . . . . . .
 *  y  . G . R . G . R .
 *     . . . . . . . . .
 * y+1 . B . G . B . G .
 *     . . . . . . . . .
 * y+2 . G . R . G . R .
 *     . . . . . . . . .
 * y+3 . B . G . B . G .
 *     . . . . . . . . .
 *
 *      Red                   Green                 Blue
 *      x-1  x  x+1 x+2       x-1  x  x+1 x+2       x-1  x  x+1 x+2
 *     . . . . . . . . .     . . . . . . . . .     . . . . . . . . .
 * y-1 .   .   .   .   .     . g .   . G .   .     .   . B .   . B .
 *     . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
 *  y  . R |rih| R |   .     .   |GS1|gi1| G .     .   |biv|bic|   .
 *     . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
 * y+1 .   |ric|riv|   .     . G |gi2|GS2|   .     .   | B |bih| B .
 *     . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
 * y+2 . R .   . R .   .     .   . G .   . g .     .   .   .   .   .
 *     . . . . . . . . .     . . . . . . . . .     . . . . . . . . .
 *
 **********************************************************************************************************************/
public class BayerInfo_GRBG extends BayerInfo
  {
    public void computeSampleStrides()
      {
        int r  = getPixelStride();    // right
        int b  = getScanlineStride(); // bottom
        int br = b + r;               // bottom right

        redSampleStride    = r  + redOffset;
        greenSample1Stride =    + greenOffset;
        greenSample2Stride = br + greenOffset;
        blueSampleStride   = b  + blueOffset;

        redHInterpStride   =    + redOffset;
        redVInterpStride   = br + redOffset;
        redCInterpStride   = b  + redOffset;

        greenInterp1Stride = r  + greenOffset;
        greenInterp2Stride = b  + greenOffset;

        blueHInterpStride  = br + blueOffset;
        blueVInterpStride  =    + blueOffset;
        blueCInterpStride  = r  + blueOffset;
      }
  }
