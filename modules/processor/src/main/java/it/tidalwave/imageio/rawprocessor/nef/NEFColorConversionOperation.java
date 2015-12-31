/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
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
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonMakerNote3;
import it.tidalwave.imageio.rawprocessor.ColorMatrix;
import it.tidalwave.imageio.rawprocessor.raw.ColorConversionOperation;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.nef.Nikon3MakerNoteSupport.ColorMode;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFColorConversionOperation extends ColorConversionOperation  
  {
    private final static String CLASS = NEFColorConversionOperation.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private static Map colorMatrixProfileMap;

    private static Map colorModesMap;
        
    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    @Override
    @CheckForNull
    protected ColorMatrix getColorMatrixXYZ (final @Nonnull PipelineArtifact artifact)
      {
        final NEFMetadata metadata = (NEFMetadata)artifact.getRAWMetadata();
        final IFD primaryIFD = metadata.getPrimaryIFD();
        final NikonMakerNote3 makerNote = metadata.getNikonMakerNote();
        
        final ColorMode colorMode = makerNote.isColorModeAvailable() ? makerNote.getColorMode() : ColorMode.getInstance("default");
        final ColorMatrix colorMatrix = getColorModeColorMatrix(primaryIFD.getModel(), colorMode);
        logger.finer(">>>> model: %s colorMode: %s matrixXYZ: %s", primaryIFD.getModel(), colorMode, colorMatrix);
        
        return colorMatrix;
      }
    
    /*******************************************************************************************************************
     * 
     * @param colorMatrix
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    private static ColorMatrix parseColorMatrix (@Nonnull String string)
      {
        int j = string.indexOf('#');

        if (j >= 0)
          {
            string = string.substring(0, j);
          }

        String[] parts = string.trim().split(" ");
        assert parts.length == 9 : "Not a 3x3 matrix: " + Arrays.asList(parts);
        double c[] = new double[9];

        for (int i = 0; i < c.length; i++)
          {
            c[i] = Double.parseDouble(parts[i].trim());
          }

        return new ColorMatrix(c);
      }

    /*******************************************************************************************************************
     *
     * Returns the color matrix for a given model and color mode.
     *
     ******************************************************************************************************************/
    @CheckForNull
    public static ColorMatrix getColorModeColorMatrix (final @Nonnull String model,
                                                       final @Nonnull NikonMakerNote3.ColorMode cameraColorMode)
      {
        loadICCProfileMap();

        return (ColorMatrix)colorMatrixProfileMap.get(getCompositeKey(model, cameraColorMode));
      }

    /*******************************************************************************************************************
     *
     * Returns the color matrix for a given model and color mode.
     *
     ******************************************************************************************************************/
    @CheckForNull
    public static ColorMatrix getColorModeColorMatrix (final @Nonnull String model,
                                                       final int nceColorMode)
      {
        loadICCProfileMap();

        return (ColorMatrix)colorMatrixProfileMap.get(getCompositeKey(model, nceColorMode));
      }

    /*******************************************************************************************************************
     *
     * Returns the color modes for the given model.
     *
     ******************************************************************************************************************/
    @CheckForNull
    public static int[] getColorModes (final @Nonnull String model)
      {
        loadICCProfileMap();

        return (int[])colorModesMap.get(model);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Object getCompositeKey (final @Nonnull String model,
                                           final @Nonnull Object colorMode)
      {
        return model.toString().trim() + "." + colorMode.toString().trim();
      }
    
    /*******************************************************************************************************************
     *
     * This should be called once after metadata has been set (it needs the model
     * name).
     *
     ******************************************************************************************************************/
    private static synchronized void loadICCProfileMap ()
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

                        logger.fine("Model: %s, colorMode: %s, nce code: %s", model, colorMode, nceCode);
                        logger.fine(">>>> Color matrix:   %s", colorMatrix);
                      }

                    colorModesMap.put(model, colorModes);
                  }
              }
          }
      }
  }
