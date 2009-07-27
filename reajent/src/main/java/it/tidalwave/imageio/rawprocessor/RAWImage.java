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
 * $Id: RAWImage.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import it.tidalwave.imageio.util.Logger;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.raw.RAWMetadataSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: RAWImage.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class RAWImage
  {
    private final static String CLASS = RAWImage.class.getName();
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private final static char[] CHARS = { 'R', 'G', 'B', 'C', 'M', 'Y', 'W' };

    protected BufferedImage image;
    
    protected RAWMetadataSupport rawMetadata;
    
    private int[] cfaPattern;
    
    protected double[] coefficients = { 1, 1, 1, 1, 1, 1, 1, 1 };
    
    private double blackLevel = 0; // should use a different value for each channel
    
    protected Curve curve;
    
    /** The angle that the image was rotated to. */
    private int rotation;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public RAWImage (BufferedImage image, RAWMetadataSupport rawMetadata)
      {
        this.image = image;
        this.rawMetadata = rawMetadata;
        cfaPattern = computeCFAPattern();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setImage (BufferedImage image)
      {
        this.image = image;  
        // TODO: should update metadata width and height!
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public BufferedImage getImage()
      {
        return image;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public RAWMetadataSupport getRAWMetadata()
      {
        return rawMetadata;
      }
    
    /*******************************************************************************************************************
     *
     * Sets the curve.
     *
     * @param  curve  the curve
     *
     ******************************************************************************************************************/
    public void setCurve (Curve curve)
      {
        this.curve = curve;  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the curve.
     *
     * @return  the curve
     *
     ******************************************************************************************************************/
    public Curve getCurve()
      {
        return curve;   
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setRotation (int rotation)
      {
        this.rotation = rotation;  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public int getRotation()
      {
        return rotation;  
      }
    
    /*******************************************************************************************************************
     *
     * Sets the black level.
     *
     * @param  blackLevel  the black level
     *
     ******************************************************************************************************************/
    public void setBlackLevel (double blackLevel)
      {
        this.blackLevel = blackLevel;  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the black level.
     *
     * @return  the black level
     *
     ******************************************************************************************************************/
    public double getBlackLevel()
      {
        return blackLevel;  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the coefficient for the given channel.
     *
     * @param  channel  the channel
     * @return          the coefficient
     *
     ******************************************************************************************************************/
    private double getCoefficient (int channel)
      {
        return coefficients[channel];  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the red coefficient.
     *  
     * @return  the red coefficient
     *
     ******************************************************************************************************************/
    public double getRedCoefficient()
      {
        return getCoefficient(0);  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the green coefficient.
     *  
     * @return  the green coefficient
     *
     ******************************************************************************************************************/
    public double getGreenCoefficient()
      {
        return getCoefficient(1);  
      }
    
    /*******************************************************************************************************************
     *
     * Returns the blue coefficient.
     *  
     * @return  the blue coefficient
     *
     ******************************************************************************************************************/
    public double getBlueCoefficient()
      {
        return getCoefficient(2);  
      }
    
    /*******************************************************************************************************************
     *
     * Multiplies the red coefficient by the given value.
     *
     * @param  value  the multiplier
     *
     ******************************************************************************************************************/
    public void multiplyRedCoefficient (double value)
      {
        multiplyRGBCoefficient(0, value);    
      }
    
    /*******************************************************************************************************************
     *
     * Multiplies the green coefficient by the given value.
     *
     * @param  value  the multiplier
     *
     ******************************************************************************************************************/
    public void multiplyGreenCoefficient (double value)
      {
        multiplyRGBCoefficient(1, value);    
      }
    
    /*******************************************************************************************************************
     *
     * Multiplies the blue coefficient by the given value.
     *
     * @param  value  the multiplier
     *
     ******************************************************************************************************************/
    public void multiplyBlueCoefficient (double value)
      {
        multiplyRGBCoefficient(2, value);    
      }
    
    /*******************************************************************************************************************
     *
     * Multiplies a given channel coefficient by the given value. The channel
     * parameter can be 0,1,2,... to mean R,G,B,...
     *
     * @param  channel  the channel to multiply
     * @param  value    the multiplier
     *
     ******************************************************************************************************************/
    private void multiplyRGBCoefficient (int channel, double value)
      {
        coefficients[channel] *= value;
        logger.finer ("Coefficient[%d] = %f - has been multiplied by %f", channel, coefficients[channel], value);
      }
    
    /*******************************************************************************************************************
     *
     * Multiplies a given channel coefficient by the given value. The cfaIndex 
     * parameters refers to the channel at the given position in the CFA matrix.
     *
     * @param  channel  the channel to multiply
     * @param  value    the multiplier
     *
     ******************************************************************************************************************/
    public void multiplyCFACoefficient (int cfaIndex, double value)
      {
        int channel = cfaPattern[cfaIndex];
        coefficients[channel] *= value;
        logger.finer ("Coefficient[%d] = %f - has been multiplied by %f", channel, coefficients[channel], value);
      }
    
    /*******************************************************************************************************************
     * 
     * Retrieves the CFA pattern for the current model. This method should be 
     * overridden for non-TIFF based RAW formats.
     * 
     * @return  the CFA pattern
     * 
     *******************************************************************************/
    public int[] getCFAPattern()
      {
        return cfaPattern;  
      }
    
    /*******************************************************************************************************************
     * 
     * Returns the CFA pattern as a string (e.g. "GRBG").
     *
     * @return  the CFA pattern
     * 
     *******************************************************************************/
    public String getCFAPatternAsString()
      {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < cfaPattern.length; i++)
          {
            buffer.append(CHARS[cfaPattern[i]]);
          }

        return buffer.toString();
      }
    
    /*******************************************************************************************************************
     * 
     * Computes the CFA pattern as an array of indexes. This method first searches 
     * in the EXIF directory, then in the raster directory.
     *
     * @return  the CFA pattern
     * 
     *******************************************************************************/
    private int[] computeCFAPattern()
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)rawMetadata;
        IFD rasterIFD = metadata.getRasterIFD();
        IFD exifIFD = metadata.getExifIFD();
        int[] result = new int[] { 0, 1, 1, 2};
        
        if ((exifIFD != null) && exifIFD.isEXIFCFAPatternAvailable())
          {
            byte[] cfaPattern = exifIFD.getEXIFCFAPattern();    
            
            for (int i = 0; i < result.length; i++)
              {
                result[i] = cfaPattern[4 + i];  
              }
          }
        
        if ((rasterIFD != null) && rasterIFD.isCFAPatternAvailable())
          {
            byte[] cfaPattern = rasterIFD.getCFAPattern();    
            
            for (int i = 0; i < result.length; i++)
              {
                result[i] = cfaPattern[i];  
              }
          }
        
        return result;
      }  
  }
