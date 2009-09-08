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
/***********************************************************************************************************************
*
* @author  Fabrizio Giudici
* @version $Id$
*

      Indexes

      tl t tr
      l  c  r
      bl b br

        Bayer RGGB pattern:
         x  x+1 x+2 x+3
       . . . . . . . . .
    y  . R . G . R . G .
       . . . . . . . . .
   y+1 . G . B . G . B .
       . . . . . . . . .
   y+2 . R . G . R . G .
       . . . . . . . . .
   y+3 . G . B . G . B .
       . . . . . . . . .

        Blue                  Green                 Red 
        x-1  x  x+1 x+2       x-1  x  x+1 x+2       x-1  x  x+1 x+2
       . . . . . . . . .     . . . . . . . . .     . . . . . . . . .
   y-1 . B .   . B .   .     .   . G .   . G .     .   .   .   .   .
       . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
    y  .   |b3 |b2 |   .     . G |g1 | Ga|   .     .   | R |r1 | R .
       . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
   y+1 . B |b1 | B |   .     .   | Gb|g2 | G .     .   |r2 |r3 |   .
       . . +---+---+ . .     . . +---+---+ . .     . . +---+---+ . .
   y+2 .   .   .   .   .     . G .   . G .   .     .   . R .   . R .
       . . . . . . . . .     . . . . . . . . .     . . . . . . . . .

******************************************************************************/

public class BayerInfo_RGGB extends BayerInfo
 {
   public void computeSampleStrides()
     {
       int r  = getPixelStride();    // right
       int b  = getScanlineStride(); // bottom
       int br = b + r;               // bottom right

       redSampleStride    =      redOffset;
       greenSample1Stride = r  + greenOffset;
       greenSample2Stride = b  + greenOffset;
       blueSampleStride   = br + blueOffset;

       redHInterpStride   = r  + redOffset;
       redVInterpStride   = b  + redOffset;
       redCInterpStride   = br + redOffset;

       greenInterp1Stride =    + greenOffset;
       greenInterp2Stride = br + greenOffset;

       blueHInterpStride  = b  + blueOffset;
       blueVInterpStride  = r  + blueOffset;
       blueCInterpStride  =    + blueOffset;
     }
 }
