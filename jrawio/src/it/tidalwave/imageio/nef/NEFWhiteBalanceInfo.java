/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
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
 * $Id: NEFWhiteBalanceInfo.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/*******************************************************************************
 *
 * @author  fritz
 * @version $Id: NEFWhiteBalanceInfo.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/

public class NEFWhiteBalanceInfo // NOT Serializable
  {
    private ByteBuffer buffer; 

    private boolean valid = true;
    
    private int version;
    
    private int redIndex;
    
    private int green1Index;
    
    private int green2Index;
    
    private int blueIndex;
    
    private int[] coefficients = new int[4];
    
    private final static double SCALE = 1.0 / 256.0;

    private final static short[] KEY1 =
      { 
        0xc1,0xbf,0x6d,0x0d,0x59,0xc5,0x13,0x9d,0x83,0x61,0x6b,0x4f,0xc7,0x7f,0x3d,0x3d,
        0x53,0x59,0xe3,0xc7,0xe9,0x2f,0x95,0xa7,0x95,0x1f,0xdf,0x7f,0x2b,0x29,0xc7,0x0d,
        0xdf,0x07,0xef,0x71,0x89,0x3d,0x13,0x3d,0x3b,0x13,0xfb,0x0d,0x89,0xc1,0x65,0x1f,
        0xb3,0x0d,0x6b,0x29,0xe3,0xfb,0xef,0xa3,0x6b,0x47,0x7f,0x95,0x35,0xa7,0x47,0x4f,
        0xc7,0xf1,0x59,0x95,0x35,0x11,0x29,0x61,0xf1,0x3d,0xb3,0x2b,0x0d,0x43,0x89,0xc1,
        0x9d,0x9d,0x89,0x65,0xf1,0xe9,0xdf,0xbf,0x3d,0x7f,0x53,0x97,0xe5,0xe9,0x95,0x17,
        0x1d,0x3d,0x8b,0xfb,0xc7,0xe3,0x67,0xa7,0x07,0xf1,0x71,0xa7,0x53,0xb5,0x29,0x89,
        0xe5,0x2b,0xa7,0x17,0x29,0xe9,0x4f,0xc5,0x65,0x6d,0x6b,0xef,0x0d,0x89,0x49,0x2f,
        0xb3,0x43,0x53,0x65,0x1d,0x49,0xa3,0x13,0x89,0x59,0xef,0x6b,0xef,0x65,0x1d,0x0b,
        0x59,0x13,0xe3,0x4f,0x9d,0xb3,0x29,0x43,0x2b,0x07,0x1d,0x95,0x59,0x59,0x47,0xfb,
        0xe5,0xe9,0x61,0x47,0x2f,0x35,0x7f,0x17,0x7f,0xef,0x7f,0x95,0x95,0x71,0xd3,0xa3,
        0x0b,0x71,0xa3,0xad,0x0b,0x3b,0xb5,0xfb,0xa3,0xbf,0x4f,0x83,0x1d,0xad,0xe9,0x2f,
        0x71,0x65,0xa3,0xe5,0x07,0x35,0x3d,0x0d,0xb5,0xe9,0xe5,0x47,0x3b,0x9d,0xef,0x35,
        0xa3,0xbf,0xb3,0xdf,0x53,0xd3,0x97,0x53,0x49,0x71,0x07,0x35,0x61,0x71,0x2f,0x43,
        0x2f,0x11,0xdf,0x17,0x97,0xfb,0x95,0x3b,0x7f,0x6b,0xd3,0x25,0xbf,0xad,0xc7,0xc5,
        0xc5,0xb5,0x8b,0xef,0x2f,0xd3,0x07,0x6b,0x25,0x49,0x95,0x25,0x49,0x6d,0x71,0xc7 
      };
    
    private final static short[] KEY2 =
      { 
        0xa7,0xbc,0xc9,0xad,0x91,0xdf,0x85,0xe5,0xd4,0x78,0xd5,0x17,0x46,0x7c,0x29,0x4c,
        0x4d,0x03,0xe9,0x25,0x68,0x11,0x86,0xb3,0xbd,0xf7,0x6f,0x61,0x22,0xa2,0x26,0x34,
        0x2a,0xbe,0x1e,0x46,0x14,0x68,0x9d,0x44,0x18,0xc2,0x40,0xf4,0x7e,0x5f,0x1b,0xad,
        0x0b,0x94,0xb6,0x67,0xb4,0x0b,0xe1,0xea,0x95,0x9c,0x66,0xdc,0xe7,0x5d,0x6c,0x05,
        0xda,0xd5,0xdf,0x7a,0xef,0xf6,0xdb,0x1f,0x82,0x4c,0xc0,0x68,0x47,0xa1,0xbd,0xee,
        0x39,0x50,0x56,0x4a,0xdd,0xdf,0xa5,0xf8,0xc6,0xda,0xca,0x90,0xca,0x01,0x42,0x9d,
        0x8b,0x0c,0x73,0x43,0x75,0x05,0x94,0xde,0x24,0xb3,0x80,0x34,0xe5,0x2c,0xdc,0x9b,
        0x3f,0xca,0x33,0x45,0xd0,0xdb,0x5f,0xf5,0x52,0xc3,0x21,0xda,0xe2,0x22,0x72,0x6b,
        0x3e,0xd0,0x5b,0xa8,0x87,0x8c,0x06,0x5d,0x0f,0xdd,0x09,0x19,0x93,0xd0,0xb9,0xfc,
        0x8b,0x0f,0x84,0x60,0x33,0x1c,0x9b,0x45,0xf1,0xf0,0xa3,0x94,0x3a,0x12,0x77,0x33,
        0x4d,0x44,0x78,0x28,0x3c,0x9e,0xfd,0x65,0x57,0x16,0x94,0x6b,0xfb,0x59,0xd0,0xc8,
        0x22,0x36,0xdb,0xd2,0x63,0x98,0x43,0xa1,0x04,0x87,0x86,0xf7,0xa6,0x26,0xbb,0xd6,
        0x59,0x4d,0xbf,0x6a,0x2e,0xaa,0x2b,0xef,0xe6,0x78,0xb6,0x4e,0xe0,0x2f,0xdc,0x7c,
        0xbe,0x57,0x19,0x32,0x7e,0x2a,0xd0,0xb8,0xba,0x29,0x00,0x3c,0x52,0x7d,0xa8,0x49,
        0x3b,0x2d,0xeb,0x25,0x49,0xfa,0xa3,0xaa,0x39,0xa7,0xc5,0xa7,0x50,0x11,0x36,0xfb,
        0xc6,0x67,0x4a,0xf5,0xa5,0x12,0x65,0x7e,0xb0,0xdf,0xaf,0x4e,0xb3,0x61,0x7f,0x2f 
      };
  
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected NEFWhiteBalanceInfo (NikonMakerNote3 makerNote, boolean bigEndian)
      {
        buffer = ByteBuffer.wrap(makerNote.getBlock151());
        buffer.order(bigEndian? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        computeVersion();
        
        if (version >= 0x200)
          { 
            demangle(makerNote);
          }
        
        computeCoefficients();
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public int getVersion()
      {
        return version;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public boolean isValid()
      {
        return valid;  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public int[] getCoefficients()
      {
        return coefficients;  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getRedCoefficient()
      {
        return SCALE * coefficients[redIndex];  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getGreen1Coefficient()
      {
        return SCALE * coefficients[green1Index];  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getGreen2Coefficient()
      {
        return SCALE * coefficients[green2Index];  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getBlueCoefficient()
      {
        return SCALE * coefficients[blueIndex];  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void demangle ( NikonMakerNote3 makerNote)
      {
        String serialNumber = makerNote.getD2XSerialNumber();
        int shutterCount = makerNote.getShutterCount();
        int key = 0;
        
        for (int i = 0; i < serialNumber.length(); i++)
          {
            char c = serialNumber.charAt(i);
            key = key * 10 + (Character.isDigit(c) ? c - '0' : c % 10);
          }
        
        int k1 = key & 0xff;
        int k2 = ((shutterCount >>> 24) & 0xff) ^ ((shutterCount >>> 16) & 0xff) ^ ((shutterCount >>> 8) & 0xff) ^ ((shutterCount >>> 0) & 0xff);
        
        byte c1 = (byte)KEY1[k1];
        byte c2 = (byte)KEY2[k2];
        byte c3 = 0x60;
        int offset = (version != 0x205) ? 284 : 4;
        
        for (int i = offset; i < buffer.limit(); i++)
          {
            buffer.put(i, (byte)(buffer.get(i) ^ (c2 += c1 * c3++)));                
          }
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void computeVersion()
      {
        version = 0;

        for (int i = 0; i < 4; i++)
          {
            version <<= 4;
            version |= (buffer.get(i) - '0');
          }
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void computeCoefficients()
      {
        int offset = 0;
                
        switch (version)
          {
            case 0x100: // coefficients are R B G G
              offset = 68 + 4;
              redIndex = 0;
              green1Index = 2;
              green2Index = 3;
              blueIndex = 1;
              break;

            case 0x102: // coefficients are R G G B
              offset = 6 + 4;
              redIndex = 0;
              green1Index = 1;
              green2Index = 2;
              blueIndex = 3;
              break;
              
            case 0x103: // coefficients are R G B G
              offset = 16 + 4;
              redIndex = 0;
              green1Index = 1;
              green2Index = 3;
              blueIndex = 2;
              break;
              
            case 0x200: // coefficients are R G G B
            case 0x201:
            case 0x202:
            case 0x203:
            case 0x204:
            case 0x206:
            case 0x207:
              offset = 286 + 4;
              redIndex = 0;
              green1Index = 1;
              green2Index = 2;
              blueIndex = 3;
              break;
              
            case 0x205: // coefficients are R G G B
              offset = 14 + 4;
              redIndex = 0;
              green1Index = 1;
              green2Index = 2;
              blueIndex = 3;
              break;
              
            default:
              valid = false;
              break;
          }
        
        if (valid)
          {
            for (int i = 0; i < 4; i++)
              {
                coefficients[i] = buffer.getShort(offset + i * 2);
              }
          }
      }
  }
