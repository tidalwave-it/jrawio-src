/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.rawprocessor.demosaic;

import it.tidalwave.imageio.util.Logger;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.rawprocessor.Curve;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
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
    
    /*******************************************************************************************************************
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

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (BufferedImage image)
      {
        logger.fine("Demosaicing: R: %f, G: %f, B: %f, black: %f, Curve: %s", redCoeff, greenCoeff, blueCoeff, blackLevel, curve);

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
