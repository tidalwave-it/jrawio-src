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
 * $Id: ColorConversionOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raw;

import java.util.logging.Logger;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: ColorConversionOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public abstract class ColorConversionOperation extends OperationSupport
{
    private final static Logger logger = getLogger(ColorConversionOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (RAWImage image)
      {
        ColorMatrix colorMatrix = getColorMatrix(image);
        
        if (colorMatrix != null)
          {
            applyMatrix(image.getImage().getRaster(), colorMatrix);
          }
      }
    
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    protected ColorMatrix getColorMatrix (RAWImage image)
      {
        ColorMatrix colorMatrix = getColorMatrixXYZ(image);
        
        if (colorMatrix != null)
          {
            logger.info(">>>> colorMatrix in XYZ space: " + colorMatrix);
            ColorMatrix colorMatrixRGB = colorMatrix.product(ColorMatrix.XYZ_TO_RGB);
            logger.info(">>>> colorMatrix in RGB space: " + colorMatrixRGB);
            colorMatrixRGB.normalizeRows(); // FIXME: investigate if it's correct
            logger.info(">>>> colorMatrix in RGB space, normalized: " + colorMatrixRGB);
            colorMatrixRGB = colorMatrixRGB.inverse();
            logger.info(">>>> final colorMatrix:  " + colorMatrixRGB);
            return colorMatrixRGB;
          }
        
        return null;
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected ColorMatrix getColorMatrixXYZ (RAWImage image)
      {
        return null;   
      }
    
    /*******************************************************************************
     *
     * @param matrix
     * @return
     *
     *******************************************************************************/
    protected static ColorMatrix getMatrix(TagRational[] matrix)
      {
        double c[] = new double[matrix.length];
        
        for (int i = 0; i < c.length; i++)
          {
            c[i] = matrix[i].doubleValue();
          }
        
        return new ColorMatrix(c);
      }
    
    /*******************************************************************************
     *
     * @param matrix
     * @return
     *
     *******************************************************************************/
    protected static ColorMatrix getMatrix(int[] matrix, double scale)
      {
        double c[] = new double[matrix.length];
        
        for (int i = 0; i < c.length; i++)
          {
            c[i] = scale * matrix[i];
          }
        
        return new ColorMatrix(c);
      }
    
    /*******************************************************************************
     *
     * TODO: rewrite using a standard Java library!
     * @param raster
     *
     *******************************************************************************/
    private static void applyMatrix (WritableRaster raster, ColorMatrix colorMatrix1)
      {
        logger.fine("applyMatrix()");
        logger.finer(">>>> raster: " + raster);
        
        long time = System.currentTimeMillis();
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        
        while (raster.getParent() != null)
          {
            raster = (WritableRaster)raster.getParent();
          }
        
        int pixelStride = raster.getNumBands();
        int scanStride = raster.getWidth() * pixelStride;
        
        logger.finer(">>>> pixelStride: " + pixelStride + ", scanStride: " + scanStride);
        logger.finer(">>>> offset: " + dataBuffer.getOffset());
        
        // FIXME: you should be able to find the offset of the child raster within the parent
        // Until you don't do, scan over the whole parent raster
        w = raster.getWidth();
        h = raster.getHeight();
        
        for (int y = 0; y < h; y++)
          {
            int scan = y * scanStride + dataBuffer.getOffset();
            
            for (int x = 0; x < w; x++)
              {
                colorMatrix1.process(data, scan);
                scan += pixelStride;
              }
            
            Thread.yield();
          }
        
        logger.finest(">>>> applyMatrix() completed ok in " + (System.currentTimeMillis() - time) + " msec");
      }
  }
