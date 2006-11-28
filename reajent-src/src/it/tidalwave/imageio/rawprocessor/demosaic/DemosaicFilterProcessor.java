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
 * $Id: DemosaicFilterProcessor.java,v 1.2 2006/02/25 16:58:40 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.demosaic;

import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.rawprocessor.Curve;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: DemosaicFilterProcessor.java,v 1.2 2006/02/25 16:58:40 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DemosaicFilterProcessor
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.demosaic.DemosaicFilterProcessor";
    
    private final static Logger logger = Logger.getLogger(CLASS);

    private DemosaicingFilter filter;

    private BayerInfo bayerInfo;

    private double redCoeff = 1; 
    private double greenCoeff = 1; 
    private double blueCoeff = 1; 
    private double blackLevel = 0;
    
    private Curve curve;
    
    /*******************************************************************************
     * 
     * @param bayerPattern
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * 
     *******************************************************************************/
    public DemosaicFilterProcessor (String bayerPattern, String algorithm, double redCoeff, double greenCoeff, double blueCoeff, double blackLevel, Curve curve)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException
      {
        String packageName = getClass().getPackage().getName();

        try
          {
            Class bayerInfoClass = Class.forName(packageName + ".BayerInfo_" + bayerPattern);
            Class filterClass = Class.forName(packageName + "." + algorithm + "DemosaicingFilter");
            bayerInfo = (BayerInfo)bayerInfoClass.newInstance();
            filter = (DemosaicingFilter)filterClass.newInstance();
          }
        catch (ClassNotFoundException e)
          {
            Class filterClass = Class.forName(packageName + "." + "DemosaicingFilter_" + bayerPattern);
            filter = (DemosaicingFilter)filterClass.newInstance();
          }
        
        this.redCoeff = redCoeff;
        this.greenCoeff = greenCoeff;
        this.blueCoeff = blueCoeff;
        this.blackLevel = blackLevel;
        this.curve = curve;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public void process (BufferedImage image)
      {
        logger.info("Demosaicing: R:" + redCoeff + " G:" + greenCoeff + " B:" + blueCoeff + " black: " + blackLevel
            + " Curve: " + curve);

        if (bayerInfo != null)
          {
            bayerInfo.setSampleModel(image.getSampleModel());
          }

        filter.setRedCoefficient(redCoeff);
        filter.setGreenCoefficient(greenCoeff);
        filter.setBlueCoefficient(blueCoeff);
        filter.setBlackLevel((int)blackLevel);

        if (curve != null)
          {
            filter.setCurve(curve.resize(1 << 16).getSamples());
          }

        filter.filter(image.getRaster(), bayerInfo);
      }
  }
