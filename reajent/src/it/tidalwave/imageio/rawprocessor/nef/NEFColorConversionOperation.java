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
 * $Id: NEFColorConversionOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
 * @version CVS $Id: NEFColorConversionOperation.java 9 2006-11-28 12:43:27Z fabriziogiudici $
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
