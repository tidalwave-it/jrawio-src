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
 * $Id: DCRRasterReader.java,v 1.4 2006/02/25 00:05:26 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.dcr;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/*******************************************************************************
 *
 * This class implements the PEF (Pentax raw Format) raster loading.
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DCRRasterReader.java,v 1.4 2006/02/25 00:05:26 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DCRRasterReader extends RasterReader
  {
    protected boolean isCompressedRaster()
      {
        return true;
      }
    
    protected void loadCompressedRaster (RAWImageInputStream iis,
                                         WritableRaster raster,
                                         RAWImageReaderSupport ir) 
      throws IOException
      {
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        short[] buf = new short[256];
        int[] pred = new int[2];
        int width = raster.getWidth();
        int height = raster.getHeight();
        
        iis.seek(561664);
        
        for (int row = 0; row < 1; row++)
          {
            for (int col = 0; col < width; col += 256)
              {
                int cfaIndex = (2 * (row & 1)) + (col & 1);
                pred[0] = pred[1] = 0;
                int len = Math.min(256, width - col);
                boolean ret = kodak_65000_decode(iis, buf, len);
                
                for (int i = 0; i < len; i++)
                  {
                    System.err.print(Integer.toHexString(buf[i] & 0xffff) + " ");  
                  }
                
                System.err.println();
                
                for (int i = 0; i < len; i++)
                  {
                    int sample = ret ? buf[i] : (pred[i & 1] += buf[i]);
                    data[(row * width + col + i) * 3 + cfaOffsets[cfaIndex]] = (short)(sample * 16);
//                    BAYER(row,col+i) = curve[];
                  }
              }
          }
      }
    
    private boolean kodak_65000_decode (ImageInputStream iis, short[] out, int bsize)
      throws IOException            
      {
        int[] blen = new int[768];
        short[] raw = new short[6];
        
        long save = iis.getStreamPosition();
    //    System.err.println("SAVE: " + save);
        bsize = (bsize + 3) & -4;
        
        for (int i = 0; i < bsize; i += 2)
          {
            int c = iis.readByte() & 0xFF;
            int a = blen[i] = c & 15;
            int b = blen[i+1] = c >>> 4;
            
            if ((a > 12) || (b > 12))
              {
                iis.seek(save);
                
                for (i = 0; i < bsize; i += 8)
                  {
                    raw[0] = iis.readShort();
                    raw[1] = iis.readShort();
                    raw[2] = iis.readShort();
                    raw[3] = iis.readShort();
                    raw[4] = iis.readShort();
                    raw[5] = iis.readShort();
                    out[i  ] = (short)(((raw[0] >>> 12) << 8) | ((raw[2] >>> 12) << 4) | (raw[4] >>> 12));
                    out[i+1] = (short)(((raw[1] >>> 12) << 8) | ((raw[3] >>> 12) << 4) | (raw[5] >>> 12));
                    
                    for (int j = 0; j < 6; j++)
                      {
                        out[i + 2 + j] = (short)(raw[j] & 0xfff);
                      }
                  }
                
                return true;
              }
          }
        
        long bitbuf = 0;
        int bits = 0;
        
        if ((bsize & 7) == 4)
          {
            bitbuf  = (iis.readByte() & 0xFF) << 8;
            bitbuf += (iis.readByte() & 0xFF);
            bits = 16;
          }
        
        for (int i = 0; i < bsize; i++)
          {
            int len = blen[i];
            
            if (bits < len)
              {
                for (int j = 0; j < 32; j += 8)
                  {
                    int g = iis.readByte() & 0xFF;
                    bitbuf += g << (bits + (j ^ 8));
                  }
                
                bits += 32;
              }
            
            int diff = (int)(bitbuf & (0xffff >>> (16 - len)));
            bitbuf >>>= len;
            bits -= len;
            
            if ((diff & (1 << (len-1))) == 0)
              {  
                diff -= (1 << len) - 1;
              }
            
            out[i] = (short)diff;
          }
        
        return false;
      }
  }
