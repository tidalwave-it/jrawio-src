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
 * $Id: BayerInfo_GRBG.java,v 1.1 2006/02/17 15:32:04 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: BayerInfo_GRBG.java,v 1.1 2006/02/17 15:32:04 fabriziogiudici Exp $
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
 ******************************************************************************/
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
