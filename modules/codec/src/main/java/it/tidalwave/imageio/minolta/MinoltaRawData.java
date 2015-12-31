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
package it.tidalwave.imageio.minolta;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import it.tidalwave.imageio.util.Logger;
import java.awt.Dimension;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteOrder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.TagRational;

/***********************************************************************************************************************
 *
 * This structure is used by both MRW and ARW (Sony-Minolta).
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MinoltaRawData 
  {
    private final static String CLASS = MinoltaRawData.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 23423490823498939L;
    
    private int rasterOffset;
    
    private int baseOffset;

    // TODO: replace with a Map and the various getters with a single T get(Class<T>)
    @CheckForNull
    private PRD prd;

    @CheckForNull
    private RIF rif;

    @CheckForNull
    private WBG wbg;
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public static final class PRD implements Serializable
      {
        public static final int TAG = 0x505244;
        private final static long serialVersionUID = 54354350983459348L;
        
        private String version;
        @CheckForNull
        private Dimension ccdSize;
        @CheckForNull
        private Dimension imageSize;
        private int dataSize;
        private int pixelSize;
        private int storageMethod;
        private int unknown1;
        private int unknown2;
        private int unknown3;

        public Dimension getCcdSize()
          {
            return (Dimension)ccdSize.clone();
          }

        public int getDataSize()
          {
            return dataSize;
          }

        public Dimension getImageSize()
          {
            return (Dimension)imageSize.clone();
          }

        public int getPixelSize()
          {
            return pixelSize;
          }

        public int getStorageMethod()
          {
            return storageMethod;
          }

        public int getUnknown1()
          {
            return unknown1;
          }

        public int getUnknown2()
          {
            return unknown2;
          }

        public int getUnknown3()
          {
            return unknown3;
          }

        @CheckForNull
        public String getVersion()
          {
            return version;
          }

        private void load (@Nonnull final RAWImageInputStream iis) 
          throws IOException
          {
            final byte[] bytes = new byte[8];
            iis.readFully(bytes);
            version = new String(bytes);
            final int h1 = iis.readShort() & 0xFFFF;
            final int w1 = iis.readShort() & 0xFFFF;
            ccdSize = new Dimension(w1, h1);
            final int h2 = iis.readShort() & 0xFFFF;
            final int w2 = iis.readShort() & 0xFFFF;
            imageSize = new Dimension(w2, h2);
            dataSize = iis.readByte();
            pixelSize = iis.readByte();
            storageMethod = iis.readByte();
            unknown1 = iis.readByte();
            unknown2 = iis.readByte();
            unknown3 = iis.readByte();
          }
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public static final class WBG implements Serializable
      {
        public static final int TAG = 0x574247;
        private final static long serialVersionUID = 3242342349238982L;

        private TagRational[] coefficients = new TagRational[4];
       
        @Nonnull
        public TagRational getRedCoefficient() 
          {
            return coefficients[0];
          }
        
        @Nonnull
        public TagRational getGreen1Coefficient() 
          {
            return coefficients[1];
          }

        @Nonnull
        public TagRational getGreen2Coefficient() 
          {
            return coefficients[3];
          }

        @Nonnull
        public TagRational getBlueCoefficient() 
          {
            return coefficients[2];
          }

        private void load (@Nonnull final RAWImageInputStream iis) 
          throws IOException
          {
            final byte[] denominators = new byte[4];
            iis.readFully(denominators);
            int j = 0; // strstr(model,"A200") ? 3:0;

            for (int i = 0; i < 4; i++)
              {
                coefficients[i ^ (i >> 1) ^ j] = new TagRational(iis.readShort(), 64 << denominators[i]);
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public static final class RIF implements Serializable
      {
        public static final int TAG = 0x524946;
        private final static long serialVersionUID = 32423490823409238L;
        
        private int unknown;
        private int saturation;
        private int contrast;
        private int sharpness;
        private int whiteBalance;
        private int subjectProgram;
        private int ccdSensitivity;
        private int colorMode;
        private int colorFilter;
        private int bwFilter;

        public int getBwFilter()
          {
            return bwFilter;
          }

        public int getCcdSensitivity()
          {
            return ccdSensitivity;
          }

        public int getColorFilter()
          {
            return colorFilter;
          }

        public int getColorMode()
          {
            return colorMode;
          }

        public int getContrast()
          {
            return contrast;
          }

        public int getSaturation()
          {
            return saturation;
          }

        public int getSharpness()
          {
            return sharpness;
          }

        public int getSubjectProgram()
          {
            return subjectProgram;
          }

        public int getUnknown()
          {
            return unknown;
          }

        public int getWhiteBalance()
          {
            return whiteBalance;
          }
        
        private void load (@Nonnull final RAWImageInputStream iis) 
          throws IOException
          {
          }
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public void load (@Nonnull final RAWImageInputStream iis, 
                      final int base, 
                      @Nonnull final ByteOrder byteOrder) 
      throws IOException
      {
        rasterOffset = iis.readInt() + base;
        long save;
        final ByteOrder byteOrderSave = iis.getByteOrder();
        iis.setByteOrder(byteOrder);    

        logger.fine(String.format("MRW header at %d (0x%x)", (int)iis.getStreamPosition(), (int)iis.getStreamPosition()));
        
        while ((save = iis.getStreamPosition()) < rasterOffset)
          {
            final ByteOrder byteOrderSave2 = iis.getByteOrder();
            iis.setByteOrder(ByteOrder.BIG_ENDIAN);    
            final int tag = iis.readInt(); // the tag is always in big endian
            iis.setByteOrder(byteOrderSave2); 
            final int len = iis.readInt();
            
            logger.fine("MRW header tag 0x%s length: %d", Integer.toHexString(tag), len);

            switch (tag) 
              {
                case PRD.TAG:
                  prd = new PRD();
                  prd.load(iis);
                  break;
                  
                case WBG.TAG:
                  wbg = new WBG();
                  wbg.load(iis);
                  break;

                case RIF.TAG:
                  rif = new RIF();
                  rif.load(iis);
                  break;
                  
                case 0x545457: // TTW 
                  baseOffset = (int)iis.getStreamPosition();
                  break;
                  
                default:
                  logger.warning(String.format("Unknown Minolta Raw tag 0x%x", tag));
              }
            
            iis.seek(save + len + 8);
          }

        iis.setByteOrder(byteOrderSave);
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public PRD getPRD()
      {
        return prd;
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public RIF getRIF()
      {
        return rif;
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public WBG getWBG() 
      {
        return wbg;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public int getRasterOffset()
      { 
        return rasterOffset;  
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    public int getBaseOffset() 
      {
        return baseOffset;
      }
  }
