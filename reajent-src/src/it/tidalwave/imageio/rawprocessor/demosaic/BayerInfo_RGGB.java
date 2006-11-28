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
 * $Id: BayerInfo_RGGB.java,v 1.1 2006/02/17 15:32:05 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;
/*******************************************************************************
*
* @author  Fabrizio Giudici
* @version CVS $Id: BayerInfo_RGGB.java,v 1.1 2006/02/17 15:32:05 fabriziogiudici Exp $
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
