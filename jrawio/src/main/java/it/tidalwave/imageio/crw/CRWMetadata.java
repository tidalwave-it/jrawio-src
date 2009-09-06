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
package it.tidalwave.imageio.crw;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Iterator;
import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWMetadata extends TIFFMetadataSupport
  {
    private final static String CLASS = CRWMetadata.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

//    private final static String NATIVE_FORMAT_NAME = "it_tidalwave_imageio_plugins_crw_image_1.0";

    private CanonCRWMakerNote canonMakerNote;

    private final static int[] WHITE_BALANCE_MAP = { 1, 2, 3, 4, 5, 1 };

    private final static int[] WHITE_BALANCE_MAP_10D = { 0, 1, 3, 4, 5, 6, 0, 0, 2, 8 };

    private final static int[] WHITE_BALANCE_MAP_S70 = { 0, 1, 2, 9, 4, 3, 6, 7, 8, 9, 10, 0, 0, 0, 7, 0, 0, 8 };

    private final static int[] RB_OBFUSCATOR = { 0x410, 0x45f3 };

    private final static int[] NO_OBFUSCATOR = { 0, 0 };

    private final static String[] WHITE_BALANCE_NAMES = {
      "auto", "daylight", "cloudy", "tungsten", "fluorescent", "flash", "custom", "b&w", "shade" };

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public CRWMetadata (final @Nonnull CanonCRWMakerNote canonMakerNote,
                        final @Nonnull Directory imageIFD,
                        final @Nonnull RAWImageInputStream iis,
                        final @Nonnull HeaderProcessor headerProcessor)
      {
        super((IFD)imageIFD, iis, headerProcessor);
        this.canonMakerNote = canonMakerNote;

        // Potrebbero essere temperature e coefficienti r-b        
        //    	#4269 type: short[31] 62,20127,8480,7000,6000,5600,5200,4724,4200,3266,1446,-716,-410,-347,-287,-254,-215,-164,-90,111,667,-120,-326,-354,-382,-398,-412,-425,-436,-450,-194
        /*
         int[] c = findTag(CanonMakerNote.WHITE_BALANCE_COEFF_CHECK).getIntValues();
         
         for (int i = 0; i < c.length; i++)
         System.err.println(" " + c[i] + " " + Integer.toHexString(c[i]));
         */
      }

    public CanonCRWMakerNote getCanonMakerNote()
      {
        return canonMakerNote;
      }

    @Nonnegative
    public int getFileNumber()
      {
        return findTag(CanonCRWMakerNote.FILE_NUMBER).getIntValues()[0];
      }

    @Nonnegative
    public int getImageWidth()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[0];
      }

    @Nonnegative
    public int getImageHeight()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[1];
      }

//    @Nonnegative
//    @Override
//    public int getThumbnailWidth (final @Nonnegative int index)
//      {
//        final CIFFTag thumbnailInfo = findTag(CanonCRWMakerNote.THUMBNAIL_INFO);
//        return (thumbnailInfo == null)? 0 : thumbnailInfo.getIntValues()[3];
//      }
//
//    @Nonnegative
//    @Override
//    public int getThumbnailHeight(final @Nonnegative int index)
//      {
//        final CIFFTag thumbnailInfo = findTag(CanonCRWMakerNote.THUMBNAIL_INFO);
//        return (thumbnailInfo == null)? 0 : thumbnailInfo.getIntValues()[4];
//      }

    @Nonnegative
    public float getPixelAspectRatio()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getFloatValues()[2];
      }

    public int getRotation()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[3];
      }

    @Nonnegative
    public int getComponentBitDepth()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[4];
      }

    @Nonnegative
    public int getColorBitDepth()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[5];
      }

    public int getColorBW()
      {
        return findTag(CanonCRWMakerNote.IMAGE_INFO).getIntValues()[6];
      }

    @Nonnull
    public Date getTimeStampAsDate()
      {
        return new Date(findTag(CanonCRWMakerNote.TIME_STAMP).getIntValues()[0] * 1000L);
      }

    @Nonnegative
    public int getBaseISO()
      {
        return findTag(CanonCRWMakerNote.BASE_ISO).getIntValues()[0];
      }

    @Nonnull
    public String getFirmwareVersion()
      {
        return findTag(CanonCRWMakerNote.CANON_FIRMWARE_VERSION).getASCIIValue();
      }

    @Nonnull
    public String getModel()
      {
        String s = findTag(CanonCRWMakerNote.CANON_RAW_MAKE_MODEL).getASCIIValue();
        final int i = s.indexOf(0);

        if (i >= 0)
          {
            s = s.substring(i + 1);
          }

        return s;
      }

    @Nonnegative
    public int getSensorWidth()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[1];
      }

    @Nonnegative
    public int getSensorHeight()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[2];
      }

    @Nonnegative
    public int getSensorLeftBorder()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[5];
      }

    @Nonnegative
    public int getSensorTopBorder()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[6];
      }

    @Nonnegative
    public int getSensorRightBorder()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[7];
      }

    @Nonnegative
    public int getSensorBottomBorder()
      {
        return findTag(CanonCRWMakerNote.SENSOR_INFO).getIntValues()[8];
      }

    @Nonnegative
    public float getFocalLength()
      {
        return findTag(CanonCRWMakerNote.FOCAL_LENGTH).getIntValues()[1];
      }

    public int getSerialNumber()
      {
        return findTag(CanonCRWMakerNote.SERIAL_NUMBER).getIntValues()[0];
      }

    @Nonnull
    public int[] getDecoderTable()
      {
        return findTag(CanonCRWMakerNote.DECODER_TABLE).getIntValues();
      }
            
    @Nonnull
    public CanonCRWMakerNote findCameraSettings()
      {
        return findDirectory(CanonCRWMakerNote.CANON_CAMERA_SETTINGS);
      }

    /*******************************************************************************************************************
     *
     * @return
     *
     ******************************************************************************************************************/
    public int getWhiteBalance()
      {
        int wb = findTag(CanonCRWMakerNote.CANON_SHOT_INFO).getIntValues()[14];
        final String model = getModel();

        if ((wb == 6) && ("Canon EOS DIGITAL REBEL".equals(model) || "Canon EOS 300D DIGITAL".equals(model)))
          {
            wb++;
          }

        if ("Canon EOS 10D".equals(model))
          {
            wb = WHITE_BALANCE_MAP_10D[wb];
          }

        return wb;
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getWhiteBalanceAsString()
      {
        int whiteBalance = getWhiteBalance();

        return (whiteBalance < WHITE_BALANCE_NAMES.length) ? WHITE_BALANCE_NAMES[whiteBalance] : ("" + whiteBalance);
      }

    /*******************************************************************************************************************
     *
     * @return
     *
     ******************************************************************************************************************/
    @Nonnull
    public double[] getRBCoefficients()
      {
        final double[] rbCoefficients = { 1, 1 };
        final int wb = getWhiteBalance();
        final CIFFTag d60tag = findTag(CanonCRWMakerNote.D60_RED_BLUE_COEFFICIENTS);
        final CIFFTag d30tag = findTag(CanonCRWMakerNote.D30_WHITE_BALANCE);
        final CIFFTag g2tag = findTag(CanonCRWMakerNote.G2_WHITE_BALANCE);
        final String model = getModel();

        if (d30tag != null)
          {
            final ShortBuffer sb = ByteBuffer.wrap(d30tag.getByteValues()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            final short[] xx = new short[sb.capacity()];
            sb.get(xx);
            int scan = 36;
            logger.fine("GETTING RB COEFF FROM D30 TAG AT %d", scan);

            int[] obfuscator = NO_OBFUSCATOR;

            if ("Canon PowerShot G6".equals(model) || "Canon PowerShot S60".equals(model)
                || "Canon PowerShot S70".equals(model) || "Canon PowerShot Pro1".equals(model))
              {
                obfuscator = RB_OBFUSCATOR;
              }

            rbCoefficients[0] = (double)((xx[scan + 1] & 0xffff) ^ obfuscator[1]) / ((xx[scan + 0] & 0xffff) ^ obfuscator[0]);
            rbCoefficients[1] = (double)((xx[scan + 2] & 0xffff) ^ obfuscator[0]) / ((xx[scan + 3] & 0xffff) ^ obfuscator[1]);
          }

        else if (g2tag != null)
          {
            final int[] xx = g2tag.getIntValues();
            final int scan = 50;

            /*            if ("Canon PowerShot G1".equals(model) || "Canon PowerShot Pro90 IS".equals(model))
             {
             scan = 60;
             scan += 4; // FORC4 cam_mul[c ^ 2] = get2();
             }
             */
            logger.fine("GETTING RB COEFF FROM G2 TAG AT %d", scan);
            rbCoefficients[0] = (double)(xx[scan + 1] ^ RB_OBFUSCATOR[1]) / (xx[scan + 0] ^ RB_OBFUSCATOR[0]);
            rbCoefficients[1] = (double)(xx[scan + 2] ^ RB_OBFUSCATOR[0]) / (xx[scan + 3] ^ RB_OBFUSCATOR[1]);
          }

        if (d60tag != null)
          {
            final int[] xx = d60tag.getIntValues();
            final int scan = 1 + wb * 4;
            logger.fine("GETTING RB COEFF FROM D60 TAG AT %d", scan);
            rbCoefficients[0] = (double)xx[scan + 0] / xx[scan + 1];
            rbCoefficients[1] = (double)xx[scan + 3] / xx[scan + 2];
          }

        else
          {
            logger.warning("WARNING getRBCoefficients() NO COEFF");
          }

        return rbCoefficients;
      }

    /*******************************************************************************************************************
     * 
     * Returns true if the color temperature is available.
     * 
     * @return true if the color temperature is available
     * 
     ******************************************************************************************************************/
    public boolean isColorTemperatureAvailable()
      {
        return findTag(CanonCRWMakerNote.COLOR_TEMPERATURE) != null;
      }

    /*******************************************************************************************************************
     * 
     * Return the color temperature.
     * 
     * @return  the color temperature
     * 
     ******************************************************************************************************************/
    @Nonnegative
    public int getColorTemperature()
      {
        return findTag(CanonCRWMakerNote.COLOR_TEMPERATURE).getIntValues()[0];
      }

    /*
     UserComment type: ascii 
     MeasuredEV type: long[2] 0,-334815623
     UserComment type: ascii High definition CCD image
     CanonRawMakeModel type: ascii Canon - Powershot

     SerialNumber type: long[2] 1135765596,-1420655045
     */

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    @Override
    public String toString()
      {
        final StringBuilder buffer = new StringBuilder(super.toString());

        buffer.append("\n>>>>FileNumber: " + getFileNumber());
        buffer.append("\n>>>>FocalLengh: " + getFocalLength());
        buffer.append("\n>>>>ImageWidth: " + getImageWidth());
        buffer.append("\n>>>>ImageHeight: " + getImageHeight());
        //        buffer.append("\n>>>>ThumbnailWidth: " + getThumbnailWidth());
        //        buffer.append("\n>>>>ThumbnailHeight: " + getThumbnailHeight());
        buffer.append("\n>>>>PixelAspectRatio: " + getPixelAspectRatio());
        buffer.append("\n>>>>Rotation: " + getRotation());
        buffer.append("\n>>>>ComponentBitDepth: " + getComponentBitDepth());
        buffer.append("\n>>>>ColorBitDepth: " + getColorBitDepth());
        buffer.append("\n>>>>ColorBW: " + getColorBW());
        buffer.append("\n>>>>TimeStampAsDate: " + getTimeStampAsDate());
        buffer.append("\n>>>>BaseISO: " + getBaseISO());
        buffer.append("\n>>>>FirmwareVersion: " + getFirmwareVersion());
        buffer.append("\n>>>>Model: " + getModel());
        buffer.append("\n>>>>SensorWidth: " + getSensorWidth());
        buffer.append("\n>>>>SensorHeight: " + getSensorHeight());
        buffer.append("\n>>>>SensorLeftBorder: " + getSensorLeftBorder());
        buffer.append("\n>>>>SensorTopBorder: " + getSensorTopBorder());
        buffer.append("\n>>>>SensorRightBorder: " + getSensorRightBorder());
        buffer.append("\n>>>>SensorBottomBorder: " + getSensorBottomBorder());
        buffer.append("\n>>>>WhiteBalance: " + getWhiteBalance() + " - " + getWhiteBalanceAsString());

        if (isColorTemperatureAvailable())
          {
            buffer.append("\n>>>>ColorTemperature: " + getColorTemperature());
          }

        final double[] rbCoefficients = getRBCoefficients();

        if (rbCoefficients != null)
          {
            buffer.append("\n>>>>Red-Blue Coefficients: " + rbCoefficients[0] + ", " + rbCoefficients[1]);
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getImageSize()
      {
        return new Dimension(getSensorWidth(), getSensorHeight());
      }

    /*******************************************************************************************************************
     * 
     * @param dir
     * @param code
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private CIFFTag findTag (final @Nonnull CanonCRWMakerNote dir,
                             final @Nonnull Object code)
      {
        CIFFTag tag = (CIFFTag)dir.getTag(code);

        if (tag == null)
          {
            for (final Directory subDir : dir.getSubDirectories())
              {
                tag = findTag((CanonCRWMakerNote)subDir, code);

                if (tag != null)
                  {
                    break;
                  }
              }
          }

        return tag;
      }

    /*******************************************************************************************************************
     * 
     * @param code
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private CIFFTag findTag (final @Nonnull Object code)
      {
        return findTag(canonMakerNote, code);
      }

    /*******************************************************************************************************************
     * 
     * @param dir
     * @param code
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private CanonCRWMakerNote findDirectory (final @Nonnull CanonCRWMakerNote dir,
                                             final @Nonnull Object code)
      {
        if (dir.containsTag(code))
          {
            return dir;
          }

        for (Iterator i = dir.getSubDirectories().iterator(); i.hasNext();)
          {
            CanonCRWMakerNote subDir = (CanonCRWMakerNote)i.next();
            CanonCRWMakerNote d2 = findDirectory(subDir, code);

            if (d2 != null)
              {
                return d2;
              }
          }

        return null;
      }

    /*******************************************************************************************************************
     * 
     * @param code
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private CanonCRWMakerNote findDirectory (final @Nonnull Object code)
      {
        return findDirectory(canonMakerNote, code);
      }
  }
