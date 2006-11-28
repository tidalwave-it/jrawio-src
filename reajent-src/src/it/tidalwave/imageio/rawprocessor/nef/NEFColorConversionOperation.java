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
 * $Id: NEFColorConversionOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.raw.ColorConversionOperation;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFColorConversionOperation.java,v 1.1 2006/02/17 15:32:10 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFColorConversionOperation extends ColorConversionOperation  
  {
    private final static Logger logger = getLogger(NEFColorConversionOperation.class);

    private static Map colorMatrixProfileMap;

    private static Map colorModesMap;
        
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    protected ColorMatrix getColorMatrixXYZ (RAWImage image)
      {
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        
        if (makerNote.isColorModeAvailable())
          {
            ColorMatrix colorMatrix = getColorModeColorMatrix(primaryIFD.getModel(), makerNote.getColorMode());
            logger.finer(">>>> model: " + primaryIFD.getModel() + " colorMode: " + makerNote.getColorMode() + " matrixXYZ: " + colorMatrix);
        
             return colorMatrix;
          }
       
        return null;
      }
    
    /*******************************************************************************
     * 
     * @param colorMatrix
     * @return
     * 
     *******************************************************************************/
    private static ColorMatrix parseColorMatrix (String string)
      {
        int j = string.indexOf('#');

        if (j >= 0)
          {
            string = string.substring(0, j);
          }

        String[] parts = string.trim().split(" ");
        assert parts.length == 9;
        double c[] = new double[9];

        for (int i = 0; i < c.length; i++)
          {
            c[i] = Double.parseDouble(parts[i].trim());
          }

        return new ColorMatrix(c);
      }

    /*******************************************************************************
     *
     * Returns the color matrix for a given model and color mode.
     *
     ******************************************************************************/
    public static ColorMatrix getColorModeColorMatrix (String model, NikonMakerNote3.ColorMode cameraColorMode)
      {
        loadICCProfileMap();

        return (ColorMatrix)colorMatrixProfileMap.get(getCompositeKey(model, cameraColorMode));
      }

    /*******************************************************************************
     *
     * Returns the color matrix for a given model and color mode.
     *
     ******************************************************************************/
    public static ColorMatrix getColorModeColorMatrix (String model,
                                                int nceColorMode)
      {
        loadICCProfileMap();

        return (ColorMatrix)colorMatrixProfileMap.get(getCompositeKey(model, new Integer(nceColorMode)));
      }

    /*******************************************************************************
     *
     * Returns the color modes for the given model.
     *
     ******************************************************************************/
    public static int[] getColorModes (String model)
      {
        loadICCProfileMap();

        return (int[])colorModesMap.get(model);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    private static Object getCompositeKey (String model,
                                           Object colorMode)
      {
        return model.toString().trim() + "." + colorMode.toString().trim();
      }
    
    /*******************************************************************************
     *
     * This should be called once after metadata has been set (it needs the model
     * name).
     *
     ******************************************************************************/
    private static void loadICCProfileMap ()
      {
        if (colorMatrixProfileMap == null)
          {
            colorMatrixProfileMap = new HashMap();
            colorModesMap = new HashMap();

            Properties properties = getProperties(NEFColorConversionOperation.class);
            String property = properties.getProperty("models");

            if (property != null)
              {
                String[] models = property.split(",");

                for (int j = 0; j < models.length; j++)
                  {
                    String model = models[j].trim();
                    String prefix = model;
                    String alias = properties.getProperty(model + ".alias");

                    if (alias != null)
                      {
                        prefix = alias;
                      }

                    String[] colorModeNames = properties.getProperty(prefix + ".colorModes").split(",");
                    int[] colorModes = new int[colorModeNames.length];

                    for (int i = 0; i < colorModes.length; i++)
                      {
                        String colorMode = colorModeNames[i].trim();
                        Integer nceCode = new Integer(properties.getProperty(prefix + "." + colorMode + ".nceCode"));
                        colorModes[i] = nceCode.intValue();

                        ColorMatrix colorMatrix = null;
                        String colorMatrixString = properties.getProperty(prefix + "." + colorMode + ".colorMatrix");

                        if (colorMatrixString != null)
                          {
                            colorMatrix = parseColorMatrix(colorMatrixString);
                            colorMatrixProfileMap.put(getCompositeKey(model, nceCode), colorMatrix);
                            colorMatrixProfileMap.put(getCompositeKey(model, colorMode), colorMatrix);
                          }

                        logger.info("Model: " + model + ", colorMode: " + colorMode + ", nce code: " + nceCode);
                        logger.info(">>>> Color matrix:   " + colorMatrix);
                      }

                    colorModesMap.put(model, colorModes);
                  }
              }
          }
      }
  }
