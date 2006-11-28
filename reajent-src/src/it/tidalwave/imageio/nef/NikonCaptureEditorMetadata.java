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
 * $Id: NikonCaptureEditorMetadata.java,v 1.1 2006/02/17 15:32:15 fabriziogiudici Exp $
 *
 ******************************************************************************/

package it.tidalwave.imageio.nef;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NikonCaptureEditorMetadata.java,v 1.1 2006/02/17 15:32:15 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NikonCaptureEditorMetadata
  {
    public static class UnsharpMaskData
      {
        public final static int RGB = 0;

        public final static int RED = 1;

        public final static int GREEN = 2;

        public final static int BLUE = 3;

        public final static int YELLOW = 4;

        public final static int MAGENTA = 5;

        public final static int CYAN = 6;

        private int type;

        private int intensity;

        private int haloWidth;

        private int threshold;

        public UnsharpMaskData (int type, int intensity, int haloWidth, int threshold)
          {
            super();
            this.type = type;
            this.intensity = intensity;
            this.haloWidth = haloWidth;
            this.threshold = threshold;
          }

        public int getHaloWidth ()
          {
            return haloWidth;
          }

        public int getIntensity ()
          {
            return intensity;
          }

        public int getThreshold ()
          {
            return threshold;
          }

        public int getType ()
          {
            return type;
          }
      }

    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.nef.NikonCaptureEditorMetadata";
    
    private final static Logger logger = Logger.getLogger(CLASS);

    private static final int NOISE_REDUCTION_MARKER = 0x753dcbc0;

    private static final int NOISE_REDUCTION_DATA_MARKER = 0x926f13e0;

    private static final int WHITE_BALANCE_MARKER = 0x76a43204;

    private static final int WHITE_BALANCE_DATA_MARKER = 0xbf3c6c20;

    private static final int ADVANCED_RAW_MARKER = 0x76a43203;

    private static final int ADVANCED_RAW_DATA_MARKER = 0x56a54260;

    private static final int COLOR_BOOSTER_MARKER = 0x5f0e7d23;

    private static final int COLOR_BOOSTER_DATA_MARKER = 0xb999a36f;

    private static final int UNSHARP_MASK_MARKER = 0x76a43200;

    private static final int UNSHARP_MASK_DATA_MARKER = 0xe42b5161;

    private static final int CROP_MARKER = 0x374233e0;

    private static final int ORIENTATION_MARKER = 0x76a43207;

    private static final int VIGNETTE_MARKER = 0x76a43205;

    private static final int COLOR_BALANCE_MARKER = 0x76a43202;

    private static final int CURVES_MARKER = 0x76a43201;

    private static final int DUST_REFERENCE_MARKER = 0xf852757d;

    private static final int PHOTO_EFFECTS_MARKER = 0xab5eca5e;

    private static final int PHOTO_EFFECTS_DATA_MARKER = 0xb0384e1e;

    private static Map colorModeMap = new HashMap();

    private static Map hueMap = new HashMap();

    private static Map sharpeningMap = new HashMap();

    private static Map white1Map = new HashMap();

    private static Map white2Map = new HashMap();

    private static Map toneCompMap = new HashMap();

    private static Map colorBoosterMap = new HashMap();

    private static Map moireReductionMap = new HashMap();

    private static Map photoEffectMap = new HashMap();

    private static Map unsharpMaskMap = new HashMap();

    private static Map markerMap = new HashMap();

    //// ORIENTATION ///////////////////////////////////////////////////////////////
    public final static int ORIENTATION_0 = 0;

    public final static int ORIENTATION_CW90 = 90;

    public final static int ORIENTATION_CCW90 = 270;

    public final static int ORIENTATION_180 = 180;

    private final static int ORIENTATION_ORIENTATION_OFFSET = 0;

    //// CROP //////////////////////////////////////////////////////////////////////
    private final static int CROP_LEFT_OFFSET = 30;

    private final static int CROP_TOP_OFFSET = 38;

    private final static int CROP_RIGHT_OFFSET = 46;

    private final static int CROP_BOTTOM_OFFSET = 54;

    //// ADVANCED RAW //////////////////////////////////////////////////////////////
    private final static int ADVANCED_RAW_ENABLED_OFFSET = 0;

    //// ADVANCED RAW DATA /////////////////////////////////////////////////////////
    private final static int ADVANCED_RAW_EV_OVERRIDE_OFFSET = 0;

    private final static int ADVANCED_RAW_CONTRAST_OFFSET = 2;

    private final static int ADVANCED_RAW_COLOR_MODE_OFFSET = 4;

    private final static int ADVANCED_RAW_HUE_CORRECTION_ENABLED_OFFSET = 6;

    private final static int ADVANCED_RAW_HUE_CORRECTION_OFFSET = 7;

    private final static int ADVANCED_RAW_SATURATION_ENABLED_OFFSET = 13;

    private final static int ADVANCED_RAW_SATURATION_COMPENSATION_OFFSET = 14;

    private final static int ADVANCED_RAW_SHARPENING_OFFSET = 15;

    public final static int ADVANCED_RAW_TONECOMP_UNCHANGED_CONTRAST = 0x00;

    public final static int ADVANCED_RAW_TONECOMP_NORMAL_CONTRAST = 0x01;

    public final static int ADVANCED_RAW_TONECOMP_LOW_CONTRAST = 0x02;

    public final static int ADVANCED_RAW_TONECOMP_HIGH_CONTRAST = 0x03;

    public final static int ADVANCED_RAW_TONECOMP_USER_DEFINED_CURVE = 0x04;

    public final static int ADVANCED_RAW_TONECOMP_MEDIUM_LOW_CONTRAST = 0x05;

    public final static int ADVANCED_RAW_TONECOMP_MEDIUM_HIGH_CONTRAST = 0x06;

    public final static int ADVANCED_RAW_COLOR_MODE_UNCHANGED = 0x00;

    public final static int ADVANCED_RAW_COLOR_MODE_I = 0x01;

    public final static int ADVANCED_RAW_COLOR_MODE_II = 0x02;

    public final static int ADVANCED_RAW_COLOR_MODE_III = 0x04;

    public final static int ADVANCED_RAW_HUE_MINUS_9 = 0x00;

    public final static int ADVANCED_RAW_HUE_MINUS_6 = 0x01;

    public final static int ADVANCED_RAW_HUE_MINUS_3 = 0x02;

    public final static int ADVANCED_RAW_HUE_ZERO = 0x03;

    public final static int ADVANCED_RAW_HUE_PLUS_3 = 0x04;

    public final static int ADVANCED_RAW_HUE_PLUS_6 = 0x05;

    public final static int ADVANCED_RAW_HUE_PLUS_9 = 0x06;

    public final static int ADVANCED_RAW_SHARPENING_UNCHANGED = 0x00;

    public final static int ADVANCED_RAW_SHARPENING_NONE = 0x01;

    public final static int ADVANCED_RAW_SHARPENING_LOW = 0x02;

    public final static int ADVANCED_RAW_SHARPENING_NORMAL = 0x03;

    public final static int ADVANCED_RAW_SHARPENING_HIGH = 0x04;

    //// WHITE BALANCE /////////////////////////////////////////////////////////////
    public final static int WHITE_BALANCE_USE_GRAY_POINT = 0x0001;

    public final static int WHITE_BALANCE_RECORDED_VALUE = 0x0002;

    public final static int WHITE_BALANCE_SET_TEMPERATURE = 0x0003;

    public final static int WHITE_BALANCE_CALCULATE_AUTOMATICALLY = 0x0004;

    public final static int WHITE_BALANCE_DAYLIGHT_DIRECT_SUNLIGHT = 0x0200;

    public final static int WHITE_BALANCE_DAYLIGHT_CLOUDY = 0x0202;

    public final static int WHITE_BALANCE_DAYLIGHT_SHADE = 0x0201;

    public final static int WHITE_BALANCE_STANDARD_FLUO_WARM_WHITE = 0x0300;

    public final static int WHITE_BALANCE_STANDARD_FLUO_3700K = 0x0301;

    public final static int WHITE_BALANCE_STANDARD_FLUO_COOL_WHITE = 0x0302;

    public final static int WHITE_BALANCE_STANDARD_FLUO_5000K = 0x0303;

    public final static int WHITE_BALANCE_STANDARD_FLUO_DAYLIGHT = 0x0304;

    public final static int WHITE_BALANCE_HICOLOR_FLUO_WARM_WHITE = 0x0400;

    public final static int WHITE_BALANCE_HICOLOR_FLUO_3700K = 0x0401;

    public final static int WHITE_BALANCE_HICOLOR_FLUO_COOL_WHITE = 0x0402;

    public final static int WHITE_BALANCE_HICOLOR_FLUO_5000K = 0x0403;

    public final static int WHITE_BALANCE_HICOLOR_FLUO_DAYLIGHT = 0x0404;

    public final static int WHITE_BALANCE_FLASH = 0x0500;

    private static final int WHITE_BALANCE_INCANDESCENT = 256;

    private final static int WHITE_BALANCE_ENABLED_OFFSET = 0;

    //// WHITE BALANCE DATA ////////////////////////////////////////////////////////
    private final static int WHITE_BALANCE_RED_COEFF_OFFSET = 0;

    private final static int WHITE_BALANCE_BLUE_COEFF_OFFSET = 8;

    private final static int WHITE_BALANCE_WHITEPOINT_OFFSET = 16;

    private final static int WHITE_BALANCE_WHITEPOINT_FINE_OFFSET = 20;

    private final static int WHITE_BALANCE_TEMPERATURE_OFFSET = 24;

    //// COLOR BOOSTER /////////////////////////////////////////////////////////////
    private final static int COLOR_BOOSTER_ENABLED_OFFSET = 0;

    private final static int COLOR_BOOSTER_TYPE_OFFSET = 0;

    private final static int COLOR_BOOSTER_LEVEL_OFFSET = 1;

    public final static int COLOR_BOOSTER_NATURE = 0;

    public final static int COLOR_BOOSTER_PEOPLE = 1;

    //// NOISE REDUCTION ///////////////////////////////////////////////////////////
    private final static int NOISE_REDUCTION_ENABLED_OFFSET = 0;

    private final static int NOISE_REDUCTION_OFFSET = 0;

    private final static int NOISE_REDUCTION_EDGE_OFFSET = 4;

    private final static int NOISE_REDUCTION_MOIRE_OFFSET = 5;

    public final static int NOISE_REDUCTION_MOIRE_OFF = 0;

    public final static int NOISE_REDUCTION_MOIRE_LOW = 1;

    public final static int NOISE_REDUCTION_MOIRE_MEDIUM = 2;

    public final static int NOISE_REDUCTION_MOIRE_HIGH = 3;

    /// PHOTO EFFECTS DATA /////////////////////////////////////////////////////////
    private final static int PHOTO_EFFECTS_EFFECT_OFFSET = 0;

    private final static int PHOTO_EFFECTS_CYAN_RED_BALANCE_OFFSET = 4;

    private final static int PHOTO_EFFECTS_MAGENTA_GREEN_BALANCE_OFFSET = 6;

    private final static int PHOTO_EFFECTS_YELLOW_BLUE_BALANCE_OFFSET = 8;

    public final static int PHOTO_EFFECT_NONE = 0;

    public final static int PHOTO_EFFECT_BW = 1;

    public final static int PHOTO_EFFECT_SEPIA = 2;

    public final static int PHOTO_EFFECT_TINTED = 3;

    /// UNSHARP MASK DATA //////////////////////////////////////////////////////////
    private static final int UNSHARP_MASK_TYPE_OFFSET = 19;

    private static final int UNSHARP_MASK_INTENSITY_OFFSET = 23;

    private static final int UNSHARP_MASK_HALO_WIDTH_OFFSET = 25;

    private static final int UNSHARP_MASK_THRESHOLD_OFFSET = 27;

    private static final int UNSHARP_MASK_DELTA = (46 - 19);

    static
      {
        registerConstant(colorModeMap, ADVANCED_RAW_COLOR_MODE_UNCHANGED, "unchanged");
        registerConstant(colorModeMap, ADVANCED_RAW_COLOR_MODE_I, "mode I (sRGB)");
        registerConstant(colorModeMap, ADVANCED_RAW_COLOR_MODE_II, "mode II (Adobe RGB)");
        registerConstant(colorModeMap, ADVANCED_RAW_COLOR_MODE_III, "mode III (sRGB)");

        registerConstant(hueMap, ADVANCED_RAW_HUE_MINUS_9, "-9");
        registerConstant(hueMap, ADVANCED_RAW_HUE_MINUS_6, "-6");
        registerConstant(hueMap, ADVANCED_RAW_HUE_MINUS_3, "-3");
        registerConstant(hueMap, ADVANCED_RAW_HUE_ZERO, "none");
        registerConstant(hueMap, ADVANCED_RAW_HUE_PLUS_3, "+3");
        registerConstant(hueMap, ADVANCED_RAW_HUE_PLUS_6, "+6");
        registerConstant(hueMap, ADVANCED_RAW_HUE_PLUS_9, "+9");

        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_UNCHANGED_CONTRAST, "unchanged");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_LOW_CONTRAST, "low");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_NORMAL_CONTRAST, "normal");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_HIGH_CONTRAST, "high");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_USER_DEFINED_CURVE, "user defined curve");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_MEDIUM_LOW_CONTRAST, "medium low");
        registerConstant(toneCompMap, ADVANCED_RAW_TONECOMP_MEDIUM_HIGH_CONTRAST, "medium high");

        registerConstant(sharpeningMap, ADVANCED_RAW_SHARPENING_UNCHANGED, "unchanged");
        registerConstant(sharpeningMap, ADVANCED_RAW_SHARPENING_NONE, "none");
        registerConstant(sharpeningMap, ADVANCED_RAW_SHARPENING_LOW, "low");
        registerConstant(sharpeningMap, ADVANCED_RAW_SHARPENING_NORMAL, "normal");
        registerConstant(sharpeningMap, ADVANCED_RAW_SHARPENING_HIGH, "high");

        registerConstant(white1Map, WHITE_BALANCE_USE_GRAY_POINT, "use gray point");
        registerConstant(white1Map, WHITE_BALANCE_RECORDED_VALUE, "recorded value");
        registerConstant(white1Map, WHITE_BALANCE_SET_TEMPERATURE, "set");
        registerConstant(white1Map, WHITE_BALANCE_CALCULATE_AUTOMATICALLY, "auto");

        registerConstant(white2Map, WHITE_BALANCE_INCANDESCENT, "incandescent");
        registerConstant(white2Map, WHITE_BALANCE_DAYLIGHT_DIRECT_SUNLIGHT, "daylight/direct sunlight");
        registerConstant(white2Map, WHITE_BALANCE_DAYLIGHT_CLOUDY, "daylight/cloudy");
        registerConstant(white2Map, WHITE_BALANCE_DAYLIGHT_SHADE, "daylight/shade");
        registerConstant(white2Map, WHITE_BALANCE_STANDARD_FLUO_WARM_WHITE, "standard fluorescent/warm white");
        registerConstant(white2Map, WHITE_BALANCE_STANDARD_FLUO_3700K, "standard fluorescent/3700K");
        registerConstant(white2Map, WHITE_BALANCE_STANDARD_FLUO_COOL_WHITE, "standard fluorescent/cool white");
        registerConstant(white2Map, WHITE_BALANCE_STANDARD_FLUO_5000K, "standard fluorescent/5000K");
        registerConstant(white2Map, WHITE_BALANCE_STANDARD_FLUO_DAYLIGHT, "standard fluorescent/daylight");
        registerConstant(white2Map, WHITE_BALANCE_HICOLOR_FLUO_WARM_WHITE, "hicolor fluorescent/warm white");
        registerConstant(white2Map, WHITE_BALANCE_HICOLOR_FLUO_3700K, "hicolor fluorescent/3700K");
        registerConstant(white2Map, WHITE_BALANCE_HICOLOR_FLUO_COOL_WHITE, "hicolor fluorescent/cool white");
        registerConstant(white2Map, WHITE_BALANCE_HICOLOR_FLUO_5000K, "hicolor fluorescent/5000K");
        registerConstant(white2Map, WHITE_BALANCE_HICOLOR_FLUO_DAYLIGHT, "hicolor fluorescent/daylight");
        registerConstant(white2Map, WHITE_BALANCE_FLASH, "flash");

        registerConstant(colorBoosterMap, COLOR_BOOSTER_NATURE, "nature");
        registerConstant(colorBoosterMap, COLOR_BOOSTER_PEOPLE, "people");

        registerConstant(moireReductionMap, NOISE_REDUCTION_MOIRE_OFF, "off");
        registerConstant(moireReductionMap, NOISE_REDUCTION_MOIRE_LOW, "low");
        registerConstant(moireReductionMap, NOISE_REDUCTION_MOIRE_MEDIUM, "medium");
        registerConstant(moireReductionMap, NOISE_REDUCTION_MOIRE_HIGH, "high");

        registerConstant(photoEffectMap, PHOTO_EFFECT_NONE, "none");
        registerConstant(photoEffectMap, PHOTO_EFFECT_BW, "black & white");
        registerConstant(photoEffectMap, PHOTO_EFFECT_SEPIA, "sepia");
        registerConstant(photoEffectMap, PHOTO_EFFECT_TINTED, "tinted");

        registerConstant(unsharpMaskMap, UnsharpMaskData.RGB, "RGB");
        registerConstant(unsharpMaskMap, UnsharpMaskData.RED, "RED");
        registerConstant(unsharpMaskMap, UnsharpMaskData.GREEN, "GREEN");
        registerConstant(unsharpMaskMap, UnsharpMaskData.BLUE, "BLUE");
        registerConstant(unsharpMaskMap, UnsharpMaskData.YELLOW, "YELLOW");
        registerConstant(unsharpMaskMap, UnsharpMaskData.MAGENTA, "MAGENTA");
        registerConstant(unsharpMaskMap, UnsharpMaskData.CYAN, "CYAN");

        registerConstant(markerMap, NOISE_REDUCTION_MARKER, "NoiseReduction");
        registerConstant(markerMap, NOISE_REDUCTION_DATA_MARKER, "NoiseReductionData");
        registerConstant(markerMap, WHITE_BALANCE_MARKER, "WhiteBalance");
        registerConstant(markerMap, WHITE_BALANCE_DATA_MARKER, "WhiteBalanceData");
        registerConstant(markerMap, ADVANCED_RAW_MARKER, "AdvancedRaw");
        registerConstant(markerMap, ADVANCED_RAW_DATA_MARKER, "AdvancedRawData");
        registerConstant(markerMap, COLOR_BOOSTER_MARKER, "ColorBooster");
        registerConstant(markerMap, COLOR_BOOSTER_DATA_MARKER, "ColorBoosterData");
        registerConstant(markerMap, UNSHARP_MASK_MARKER, "UnsharpMask");
        registerConstant(markerMap, UNSHARP_MASK_DATA_MARKER, "UnsharpMaskData");
        registerConstant(markerMap, CROP_MARKER, "Crop");
        registerConstant(markerMap, ORIENTATION_MARKER, "Orientation");
        registerConstant(markerMap, VIGNETTE_MARKER, "Vignette");
        registerConstant(markerMap, COLOR_BALANCE_MARKER, "ColorBalance");
        registerConstant(markerMap, CURVES_MARKER, "Curves");
        registerConstant(markerMap, DUST_REFERENCE_MARKER, "DustReference");
        registerConstant(markerMap, PHOTO_EFFECTS_MARKER, "PhotoEffects");
        registerConstant(markerMap, PHOTO_EFFECTS_DATA_MARKER, "PhotoEffectsData");
      }

    private ByteBuffer byteBuffer;

    private ByteBuffer noiseReductionBuffer;

    private ByteBuffer noiseReductionDataBuffer;

    private ByteBuffer advancedRawBuffer;

    private ByteBuffer advancedRawDataBuffer;

    private ByteBuffer whiteBalanceBuffer;

    private ByteBuffer whiteBalanceDataBuffer;

    private ByteBuffer colorBoosterBuffer;

    private ByteBuffer colorBoosterDataBuffer;

    private ByteBuffer unsharpMaskBuffer;

    private ByteBuffer unsharpMaskDataBuffer;

    private ByteBuffer cropBuffer;

    private ByteBuffer orientationBuffer;

    private ByteBuffer curvesBuffer;

    private ByteBuffer colorBalanceBuffer;

    private ByteBuffer vignetteBuffer;

    private ByteBuffer photoEffectBuffer;

    private ByteBuffer photoEffectDataBuffer;

    private Map bufferMapById = new HashMap();

    /*******************************************************************************
     * 
     * @param buffer
     * 
     *******************************************************************************/
    public NikonCaptureEditorMetadata (byte[] buffer)
      {
        logger.finer("Creating a CaptureEditorMetadata from " + buffer.length + " bytes");
        byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // FIXME check this
        byteBuffer.put(buffer);

        int offset = 0x16; // FIXME: parse the header in a better way!
        List temp = new ArrayList();

        while ((offset + 24) /* FIXME: ? */< byteBuffer.limit())
          {
            int id = byteBuffer.getInt(offset);
            int size = (int)byteBuffer.getLong(offset + 18) - 4;
            offset += 22;
            byteBuffer.position(offset);

            ByteBuffer subBuffer = byteBuffer.slice();
            subBuffer.order(byteBuffer.order());
            subBuffer.limit(size);
            bufferMapById.put(new Integer(id), subBuffer);
            offset += size;

            int index = temp.size() - 1;

            String s = "";

            if (logger.isLoggable(Level.FINER))
              {
                s = getDebugString(offset, id, size, subBuffer, index);
              }

            switch (id)
              {
                case UNSHARP_MASK_MARKER:
                  unsharpMaskBuffer = subBuffer;
                  logger.finer(">>>> UNSHARP MASK: " + s);
                  break;

                case UNSHARP_MASK_DATA_MARKER:
                  unsharpMaskDataBuffer = subBuffer;
                  logger.finer(">>>> UNSHARP MASK DATA: " + s);
                  break;

                case CURVES_MARKER:
                  curvesBuffer = subBuffer;
                  logger.finer(">>>> CURVES: " + s);
                  break;

                case COLOR_BALANCE_MARKER:
                  colorBalanceBuffer = subBuffer;
                  logger.finer(">>>> COLOR BALANCE: " + s);
                  break;

                case ADVANCED_RAW_MARKER:
                  advancedRawBuffer = subBuffer;
                  logger.finer(">>>> ADVANCED RAW: " + s);
                  break;

                case ADVANCED_RAW_DATA_MARKER:
                  advancedRawDataBuffer = subBuffer;
                  logger.finer(">>>> ADVANCED RAW DATA: " + s);
                  break;

                case WHITE_BALANCE_MARKER:
                  whiteBalanceBuffer = subBuffer;
                  logger.finer(">>>> WHITE BALANCE: " + s);
                  break;

                case WHITE_BALANCE_DATA_MARKER:
                  whiteBalanceDataBuffer = subBuffer;
                  logger.finer(">>>> WHITE BALANCE DATA: " + s);
                  break;

                case VIGNETTE_MARKER:
                  vignetteBuffer = subBuffer;
                  logger.finer(">>>> VIGNETTE: " + s);
                  break;

                case ORIENTATION_MARKER:
                  orientationBuffer = subBuffer;
                  logger.finer(">>>> ORIENTATION: " + s);
                  break;

                case CROP_MARKER:
                  cropBuffer = subBuffer;
                  logger.finer(">>>> CROP: " + s);
                  break;

                case NOISE_REDUCTION_MARKER:
                  noiseReductionBuffer = subBuffer;
                  logger.finer(">>>> NOISE REDUCTION: " + s);
                  break;

                case NOISE_REDUCTION_DATA_MARKER:
                  noiseReductionDataBuffer = subBuffer;
                  logger.finer(">>>> NOISE REDUCTION DATA: " + s);
                  break;

                case COLOR_BOOSTER_MARKER:
                  colorBoosterBuffer = subBuffer;
                  logger.finer(">>>> COLOR BOOSTER: " + s);
                  break;

                case COLOR_BOOSTER_DATA_MARKER:
                  colorBoosterDataBuffer = subBuffer;
                  logger.finer(">>>> COLOR BOOSTER DATA: " + s);
                  break;

                case DUST_REFERENCE_MARKER:
                  logger.finer(">>>> DUST REFERENCE: " + s);
                  break;

                case PHOTO_EFFECTS_MARKER:
                  photoEffectBuffer = subBuffer;
                  logger.finer(">>>> PHOTO EFFECTS: " + s);
                  break;

                case PHOTO_EFFECTS_DATA_MARKER:
                  photoEffectDataBuffer = subBuffer;
                  logger.finer(">>>> PHOTO EFFECTS DATA: " + s);
                  break;

                default:
                  logger.finer(">>>> UNKNOWN: " + s);
                  break;
              }
          }

        logger.finer(">>>> " + toString());
      }

    /*******************************************************************************
     * 
     * @param id
     * @return
     * 
     *******************************************************************************/
    public ByteBuffer getBuffer (Integer id)
      {
        return (ByteBuffer)bufferMapById.get(id);
      }

    /*******************************************************************************
     * 
     * @param id
     * @return
     * 
     *******************************************************************************/
    public static String getBufferName (Integer id)
      {
        return getConstant(markerMap, id.intValue());
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public Set bufferIdSet ()
      {
        return Collections.unmodifiableSet(bufferMapById.keySet());
      }

    public int getOrientation ()
      {
        return orientationBuffer.getShort(ORIENTATION_ORIENTATION_OFFSET);
      }

    //    private final static int CROP_LEFT_PREV_OFFSET    = 62;
    //    private final static int CROP_TOP_PREV_OFFSET     = 70;
    //    private final static int CROP_RIGHT_PREV_OFFSET   = 78;
    //    private final static int CROP_BOTTOM_PREV_OFFSET  = 86;
    //    private final static int CROP_XDPI_OFFSET         = 158;
    //    private final static int CROP_XDPI_FACTOR_OFFSET  = 166;
    //    private final static int CROP_YDPI_OFFSET         = 182;
    //    private final static int CROP_YDPI_FACTOR_OFFSET  = 190;
    //    private final static int CROP_PIXEL_WIDTH_OFFSET  = 198;
    //    private final static int CROP_PIXEL_HEIGHT_OFFSET = 206;
    //    private final static int CROP_PIXEL_AREA_OFFSET   = 214;
    //    private final static int CROP_RATIO_OFFSET        = 222;
    public double getCropLeft ()
      {
        return cropBuffer.getDouble(CROP_LEFT_OFFSET);
      }

    public double getCropTop ()
      {
        return cropBuffer.getDouble(CROP_TOP_OFFSET);
      }

    public double getCropWidth ()
      {
        return cropBuffer.getDouble(CROP_RIGHT_OFFSET);
      }

    public double getCropHeight ()
      {
        return cropBuffer.getDouble(CROP_BOTTOM_OFFSET);
      }

    public boolean isAdvancedRawEnabled ()
      {
        return (advancedRawBuffer != null) && (advancedRawBuffer.get(ADVANCED_RAW_ENABLED_OFFSET) != 0);
      }

    public int getEVCompensation ()
      {
        return advancedRawDataBuffer.getShort(ADVANCED_RAW_EV_OVERRIDE_OFFSET);
      }

    public int getToneCompensation ()
      {
        return advancedRawDataBuffer.getShort(ADVANCED_RAW_CONTRAST_OFFSET);
      }

    public int getColorMode ()
      {
        return advancedRawDataBuffer.getShort(ADVANCED_RAW_COLOR_MODE_OFFSET);
      }

    public boolean isHueCorrectionEnabled ()
      {
        return advancedRawDataBuffer.get(ADVANCED_RAW_HUE_CORRECTION_ENABLED_OFFSET) != 0;
      }

    public int getHueCorrection ()
      {
        return advancedRawDataBuffer.get(ADVANCED_RAW_HUE_CORRECTION_OFFSET);
      }

    public boolean isSaturationCompensationEnabled ()
      {
        return advancedRawDataBuffer.get(ADVANCED_RAW_SATURATION_ENABLED_OFFSET) != 0;
      }

    public int getSaturationCompensation ()
      {
        return advancedRawDataBuffer.get(ADVANCED_RAW_SATURATION_COMPENSATION_OFFSET);
      }

    public int getSharpening ()
      {
        return advancedRawDataBuffer.get(ADVANCED_RAW_SHARPENING_OFFSET);
      }

    public boolean isWhiteBalanceEnabled ()
      {
        return (whiteBalanceBuffer != null) && (whiteBalanceBuffer.get(WHITE_BALANCE_ENABLED_OFFSET) == 0x01);
      }

    public double getWhiteBalanceRedCoeff ()
      {
        return whiteBalanceDataBuffer.getDouble(WHITE_BALANCE_RED_COEFF_OFFSET);
      }

    public double getWhiteBalanceBlueCoeff ()
      {
        return whiteBalanceDataBuffer.getDouble(WHITE_BALANCE_BLUE_COEFF_OFFSET);
      }

    public int getWhiteBalanceWhitePoint ()
      {
        return whiteBalanceDataBuffer.getInt(WHITE_BALANCE_WHITEPOINT_OFFSET);
      }

    public int getWhiteBalanceWhitePointFine ()
      {
        return whiteBalanceDataBuffer.getInt(WHITE_BALANCE_WHITEPOINT_FINE_OFFSET);
      }

    public int getWhiteBalanceTemperature ()
      {
        return whiteBalanceDataBuffer.getInt(WHITE_BALANCE_TEMPERATURE_OFFSET);
      }

    public boolean isColorBoosterEnabled ()
      {
        return (colorBoosterBuffer != null) && (colorBoosterBuffer.get(COLOR_BOOSTER_ENABLED_OFFSET) == 0x01);
      }

    public int getColorBoosterType ()
      {
        return colorBoosterDataBuffer.get(COLOR_BOOSTER_TYPE_OFFSET);
      }

    public int getColorBoosterLevel ()
      {
        return colorBoosterDataBuffer.getInt(COLOR_BOOSTER_LEVEL_OFFSET);
      }

    public boolean isNoiseReductionEnabled ()
      {
        return (noiseReductionBuffer != null) && (noiseReductionBuffer.get(NOISE_REDUCTION_ENABLED_OFFSET) == 0x01);
      }

    public int getNoiseReduction ()
      {
        return noiseReductionDataBuffer.getInt(NOISE_REDUCTION_OFFSET);
      }

    public boolean isEdgeNoiseReductionEnabled ()
      {
        return (noiseReductionDataBuffer.limit() > NOISE_REDUCTION_EDGE_OFFSET)
            && (noiseReductionDataBuffer.get(NOISE_REDUCTION_EDGE_OFFSET) == 0x01);
      }

    public int getMoireReduction ()
      {
        return (noiseReductionDataBuffer.limit() > NOISE_REDUCTION_MOIRE_OFFSET) ? noiseReductionDataBuffer
            .getInt(NOISE_REDUCTION_MOIRE_OFFSET) : NOISE_REDUCTION_MOIRE_OFF;
      }

    public boolean isPhotoEffectEnabled ()
      {
        return (photoEffectBuffer != null) && (photoEffectDataBuffer != null) && (photoEffectBuffer.get(0x00) == 1);
      }

    public int getPhotoEffect ()
      {
        return photoEffectDataBuffer.getShort(PHOTO_EFFECTS_EFFECT_OFFSET);
      }

    public int getCyanRedBalance ()
      {
        return photoEffectDataBuffer.getShort(PHOTO_EFFECTS_CYAN_RED_BALANCE_OFFSET);
      }

    public int getMagentaGreenBalance ()
      {
        return photoEffectDataBuffer.getShort(PHOTO_EFFECTS_MAGENTA_GREEN_BALANCE_OFFSET);
      }

    public int getYellowBlueBalance ()
      {
        return photoEffectDataBuffer.getShort(PHOTO_EFFECTS_YELLOW_BLUE_BALANCE_OFFSET);
      }

    public boolean isUnsharpMaskEnabled ()
      {
        return getUnsharpMaskData() != null; // unsharpMaskBuffer.get(0x00) == 1; FIXME
      }

    public UnsharpMaskData[] getUnsharpMaskData ()
      {
        if (unsharpMaskDataBuffer.limit() < 19)
          {
            return null;
          }

        int count = 1 + (unsharpMaskDataBuffer.limit() - 19) / UNSHARP_MASK_DELTA;
        UnsharpMaskData[] unsharpMaskData = new UnsharpMaskData[count];

        for (int i = 0; i < count; i++)
          {
            int type = unsharpMaskDataBuffer.get(UNSHARP_MASK_TYPE_OFFSET + UNSHARP_MASK_DELTA * i);
            int intensity = unsharpMaskDataBuffer.getShort(UNSHARP_MASK_INTENSITY_OFFSET + UNSHARP_MASK_DELTA * i);
            int haloWidth = unsharpMaskDataBuffer.getShort(UNSHARP_MASK_HALO_WIDTH_OFFSET + UNSHARP_MASK_DELTA * i);
            int threshold = unsharpMaskDataBuffer.get(UNSHARP_MASK_THRESHOLD_OFFSET + UNSHARP_MASK_DELTA * i) & 0xff;
            unsharpMaskData[i] = new UnsharpMaskData(type, intensity, haloWidth, threshold);
          }

        return unsharpMaskData;
      }

    ////////////////////////////////////////////////////////////////////////////////
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer("CaptureEditorMetadata[");
        buffer.append("\n\tOrientation: " + getOrientation() + " degrees, ");        
        buffer.append("\n\tcrop: " + getCropLeft() + ", " + getCropTop() + ", " + getCropWidth() + ", " + getCropHeight());

        if (isAdvancedRawEnabled())
          {
            buffer.append("\n\tEV compensation: " + (getEVCompensation() / 100.0) + ", ");
            buffer.append("Sharpening: " + getConstant(sharpeningMap, getSharpening()) + ", ");
            buffer.append("Tone comp: " + getConstant(toneCompMap, getToneCompensation()) + ", ");
            buffer.append("Color mode: " + getConstant(colorModeMap, getColorMode()) + ", ");

            if (isHueCorrectionEnabled())
              {
                buffer.append("Hue correction: " + getConstant(hueMap, getHueCorrection()) + ", ");
              }

            if (isSaturationCompensationEnabled())
              {
                buffer.append("Saturation compensation: " + getSaturationCompensation() + ", ");
              }
          }

        if (isWhiteBalanceEnabled())
          {
            buffer.append("\n\tRed: " + getWhiteBalanceRedCoeff() + ", ");
            buffer.append("Blue: " + getWhiteBalanceBlueCoeff() + ", ");
            buffer.append("White point: " + getWhitePointAsString(getWhiteBalanceWhitePoint()) + ", ");
            buffer.append("White point fine: " + getWhiteBalanceWhitePointFine() + ", ");
            buffer.append("Temperature: " + getWhiteBalanceTemperature());
          }

        if (isColorBoosterEnabled())
          {
            buffer.append("\n\tColorBooster type: " + getConstant(colorBoosterMap, getColorBoosterType()) + ", ");
            buffer.append("ColorBooster value: " + getColorBoosterLevel());
          }

        if (isNoiseReductionEnabled())
          {
            buffer.append("\n\tNoise reduction: " + getNoiseReduction() + ", ");
            buffer.append("Edge reduction: " + isEdgeNoiseReductionEnabled() + ", ");
            buffer.append("Moire reduction: " + getConstant(moireReductionMap, getMoireReduction()));
          }

        if (isPhotoEffectEnabled())
          {
            buffer.append("\n\tEffect: " + getConstant(photoEffectMap, getPhotoEffect()) + ", ");
            buffer.append("Cyan/Red balance: " + getCyanRedBalance() + ", ");
            buffer.append("Magenta/Green balance: " + getMagentaGreenBalance() + ", ");
            buffer.append("Yellow/Bluebalance: " + getYellowBlueBalance());
          }

        if (isUnsharpMaskEnabled())
          {
            UnsharpMaskData[] unsharpMaskData = getUnsharpMaskData();

            for (int i = 0; i < unsharpMaskData.length; i++)
              {
                buffer.append("\n\tUnsharp mask[" + i + "]: ");
                buffer.append("type: " + getConstant(unsharpMaskMap, unsharpMaskData[i].getType()) + ", ");
                buffer.append("intensity: " + unsharpMaskData[i].getIntensity() + "%, ");
                buffer.append("halo width: " + unsharpMaskData[i].getHaloWidth() + "%, ");
                buffer.append("threshold: " + unsharpMaskData[i].getThreshold());
              }
          }

        buffer.append("\n\t]");

        return buffer.toString();
      }

    /**
     * @return
     */
    public String getWhitePointAsString (int whitePoint)
      {
        return getConstant(white1Map, whitePoint);
      }

    public String getWhitePointFineAsString (int whitePoint)
      {
        return getConstant(white2Map, whitePoint);
      }

    private static void registerConstant (Map map,
                                          int key,
                                          String text)
      {
        map.put(new Integer(key), text);
      }

    private static String getConstant (Map map,
                                       int key)
      {
        String string = (String)map.get(new Integer(key));

        return (string != null) ? string : ("#" + key + " - 0x" + Integer.toHexString(key));
      }

    public static String getColorModeAsString (int colorMode)
      {
        return getConstant(colorModeMap, colorMode);
      }

    public static String getSharpeningAsString (int sharpening)
      {
        return getConstant(sharpeningMap, sharpening);
      }

    public static String getToneCompensationAsString (int toneComp)
      {
        return getConstant(toneCompMap, toneComp);
      }

    public static String getUnsharpMaskTypeAsString (int type)
      {
        return getConstant(unsharpMaskMap, type);
      }

    /**
     * @param offset
     * @param id
     * @param size
     * @param subBuffer
     * @param i
     * @return
     */
    private String getDebugString (int offset,
                                   int id,
                                   int size,
                                   ByteBuffer subBuffer,
                                   int i)
      {
        String s;
        StringBuffer bbb = new StringBuffer();
        bbb.append("subBuffer #");
        bbb.append(i);
        bbb.append(" ");
        bbb.append(Integer.toHexString(id));
        bbb.append(" at ");
        bbb.append(Integer.toHexString(offset));
        bbb.append(", size: ");
        bbb.append(size);
        bbb.append(", data: ");

        for (int j = 0; j < size; j++)
          {
            int b = subBuffer.get(j) & 0xFF;

            if (b < 0x10)
              {
                bbb.append("0");
              }

            bbb.append(Integer.toHexString(b));

            if (j > 64)
              {
                bbb.append("...");

                break;
              }

            bbb.append(" ");
          }

        s = bbb.toString();

        return s;
      }
  }
