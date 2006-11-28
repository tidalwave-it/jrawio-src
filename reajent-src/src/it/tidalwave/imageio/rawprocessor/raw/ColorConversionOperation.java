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
 * $Id: ColorConversionOperation.java,v 1.2 2006/02/25 18:54:22 fabriziogiudici Exp $
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
 * @version CVS $Id: ColorConversionOperation.java,v 1.2 2006/02/25 18:54:22 fabriziogiudici Exp $
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
