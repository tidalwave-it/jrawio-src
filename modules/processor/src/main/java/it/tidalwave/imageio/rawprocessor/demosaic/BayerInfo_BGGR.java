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
package it.tidalwave.imageio.rawprocessor.demosaic;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *

       Indexes

       tl t tr
       l  c  r
       bl b br

         Bayer BGGR pattern:
          x  x+1 x+2 x+3
        . . . . . . . . .
     y  . B . G . B . G .
        . . . . . . . . .
    y+1 . G . R . G . R .
        . . . . . . . . .
    y+2 . B . G . B . G .
        . . . . . . . . .
    y+3 . G . R . G . R .
        . . . . . . . . .

         Red                   Green                 Blue
         x-1  x  x+1 x+2       x-1  x  x+1 x+2       x-1  x  x+1 x+2
        . . . . . . . . .     . . . . . . . . .     . . . . . . . . .
    y-1 . R .   . R .   .     .   . G .   . G .     .   .   .   .   .
        . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
     y  .   |r3 |r2 |   .     . G |g1 | Ga|   .     .   | B |b1 | B .
        . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
    y+1 . R |r1 | R |   .     .   | Gb|g2 | G .     .   |b2 |b3 |   .
        . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
    y+2 .   .   .   .   .     . G .   . G .   .     .   . B .   . B .
        . . . . . . . . .     . . . . . . . . .     . . . . . . . . .

 **********************************************************************************************************************/

public class BayerInfo_BGGR extends BayerInfo
  {
    public void computeSampleStrides()
      {
        int r  = getPixelStride();    // right
        int b  = getScanlineStride(); // bottom
        int br = b + r;               // bottom right

        redSampleStride    = br + redOffset;
        greenSample1Stride = r  + greenOffset;
        greenSample2Stride = b  + greenOffset;
        blueSampleStride   =      blueOffset;

        redHInterpStride   = b  + redOffset;
        redVInterpStride   = r  + redOffset;
        redCInterpStride   =    + redOffset;

        greenInterp1Stride =    + greenOffset;
        greenInterp2Stride = br + greenOffset;

        blueHInterpStride  = r  + blueOffset;
        blueVInterpStride  = b  + blueOffset;
        blueCInterpStride  = br + blueOffset;
      }
  }
