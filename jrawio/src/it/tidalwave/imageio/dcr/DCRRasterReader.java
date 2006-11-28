/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: DCRRasterReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
 * @version CVS $Id: DCRRasterReader.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
