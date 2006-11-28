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
 * $Id: RAWImage.java,v 1.2 2006/02/25 16:58:39 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.raw.RAWMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RAWImage.java,v 1.2 2006/02/25 16:58:39 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class RAWImage
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.RAWImage";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private final static char[] CHARS = { 'R', 'G', 'B', 'C', 'M', 'Y', 'W' };

    protected BufferedImage image;
    
    protected RAWMetadataSupport rawMetadata;
    
    private int[] cfaPattern;
    
    protected double[] coefficients = { 1, 1, 1, 1, 1, 1, 1, 1 };
    
    private double blackLevel = 0; // should use a different value for each channel
    
    protected Curve curve;
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public RAWImage (BufferedImage image, RAWMetadataSupport rawMetadata)
      {
        this.image = image;
        this.rawMetadata = rawMetadata;
        cfaPattern = computeCFAPattern();
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setImage (BufferedImage image)
      {
        this.image = image;  
        // TODO: should update metadata width and height!
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public BufferedImage getImage()
      {
        return image;
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private double getCoefficient (int channel)
      {
        return coefficients[channel];  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getRedCoefficient()
      {
        return getCoefficient(0);  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getGreenCoefficient()
      {
        return getCoefficient(1);  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public double getBlueCoefficient()
      {
        return getCoefficient(2);  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void multiplyRedCoefficient (double value)
      {
        multiplyRGBCoefficient(0, value);    
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void multiplyGreenCoefficient (double value)
      {
        multiplyRGBCoefficient(1, value);    
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void multiplyBlueCoefficient (double value)
      {
        multiplyRGBCoefficient(2, value);    
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void multiplyRGBCoefficient (int channel, double value)
      {
        coefficients[channel] *= value;
        logger.finer ("Coefficient[" + channel + "] = " + coefficients[channel] + " - has been multiplied by " + value);
      }
    
    /*******************************************************************************
     *
     * BEWARE THAT THE INDEX IS RELATIVE TO THE CFA PATTERN, NOT RGB.
     *
     ******************************************************************************/
    public void multiplyCFACoefficient (int cfaIndex, double value)
      {
        int channel = cfaPattern[cfaIndex];
        coefficients[channel] *= value;
        logger.finer ("Coefficient[" + channel + "] = " + coefficients[channel] + " - has been multiplied by " + value);
      }
    
    public void setBlackLevel (double blackLevel)
      {
        this.blackLevel = blackLevel;  
      }
    
    public double getBlackLevel()
      {
        return blackLevel;  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public Curve getCurve()
      {
        return curve;   
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void setCurve (Curve curve)
      {
        this.curve = curve;  
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public RAWMetadataSupport getRAWMetadata()
      {
        return rawMetadata;
      }
    
    /*******************************************************************************
     * 
     * Retrieves the CFA pattern for the current model. The default implementation
     * gets it from the EXIF data. This method should be overridden for non-TIFF
     * based RAW formats.
     * 
     * @return  the CFA pattern
     * 
     *******************************************************************************/
    public int[] getCFAPattern()
      {
        return cfaPattern;  
      }
    
    /*******************************************************************************
     * 
     * @param cfaPattern
     * @return
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
    
    /*******************************************************************************
     * 
     * @param cfaPattern
     * @return
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
