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
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import javax.annotation.Nonnull;
import javax.annotation.Nonnegative;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class OperationSupport implements Operation
  {
    private static final Map<String, Properties> PROPERTY_MAP = new HashMap<String, Properties>();
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull 
    protected static Logger getLogger (@Nonnull final Class clazz)
      {
        return Logger.getLogger(clazz.getName());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void init (@Nonnull final RAWImage image) 
      throws Exception
      {        
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void processMetadata (RAWImage image) throws Exception
      {
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull 
    protected static Properties getProperties (@Nonnull final Class clazz)
      {
        String name = clazz.getName();
        int i = name.lastIndexOf('.');

        if (i >= 0)
          {
            name = name.substring(i + 1);
          }

        return getProperties(clazz, name + ".properties");
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull 
    private static Properties getProperties (@Nonnull final Class clazz, 
                                             @Nonnull final String resourceName)
      {
        String key = clazz+ ":" + resourceName;
        Properties properties = (Properties)PROPERTY_MAP.get(key);

        if (properties == null)
          {
            InputStream[] is = getResourceInputStream(clazz, resourceName);
            properties = new Properties();

            if (is != null)
              {
                for (int i = 0; i < is.length; i++)
                  {

                    try
                      {
                        properties.load(is[i]);
                        is[i].close();
                      }

                    catch (IOException e)
                      {
                        throw new RuntimeException(e);
                      }
                  }

                PROPERTY_MAP.put(key, properties);
              }
          }

        return properties;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static InputStream[] getResourceInputStream (@Nonnull Class clazz, 
                                                        @Nonnull final String resourceName)
      {
        final List<InputStream> temp = new ArrayList<InputStream>();

        for (; clazz != null; clazz = clazz.getSuperclass())
          {
            String string = "/" + clazz.getPackage().getName().replace('.', '/') + "/" + resourceName;
            InputStream is = clazz.getResourceAsStream(string);

            if (is != null)
              {
                System.err.println("OperationSpiSupport.getProperties() from " + string);
                temp.add(is);
              }
          }

        return temp.toArray(new InputStream[0]);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString() 
      {
        return "[" + getClass().getSimpleName().replaceAll(".*\\.", "") + "]";
      }    

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected static String toString (
        int[] array,
        int   radix)
      {
        StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < array.length; i++)
          {
            buffer.append(Integer.toString(array[i], radix));

            if (i < (array.length - 1))
              {
                buffer.append(",");
              }
          }

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected static void logImage (final @Nonnull Logger logger,
                                    final @Nonnull String prefix,
                                    final @Nonnull RenderedImage image)
      {
        if (image == null)
          {
            logger.fine("%s.image: null", prefix);
          }
        else
          {
            final ColorModel colorModel = image.getColorModel();
            logger.fine("%s.class:       %s", prefix, image.getClass().getName());
            logger.fine("%s.sampleModel: %s", prefix, toString(image.getSampleModel()));
            logger.fine("%s.colorModel:  %s : %s", prefix, colorModel.getClass().getName(), colorModel);
            logger.fine("%s.colorSpace:  %s", prefix, toString(colorModel.getColorSpace()));

            //      log.debug(">>>> iccProfile is now: " + getICCProfileName(bufferedImage));
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static String toString (SampleModel sampleModel)
      {
        if (sampleModel instanceof SinglePixelPackedSampleModel)
          {
            return toString((SinglePixelPackedSampleModel)sampleModel);
          }

        else if (sampleModel instanceof PixelInterleavedSampleModel)
          {
            return toString((PixelInterleavedSampleModel)sampleModel);
          }

        else
          {
            return sampleModel.toString();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static String toString (ColorSpace colorSpace)
      {
       if (colorSpace instanceof ICC_ColorSpace)
          {
            return toString((ICC_ColorSpace)colorSpace);
          }

        else
          {
            return colorSpace.toString();
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static String toString (ICC_ColorSpace colorSpace)
      {
        StringBuilder buffer = new StringBuilder("");
        buffer.append(colorSpace.getClass().getName());
        buffer.append("[type: ");
        buffer.append(colorSpace.getType());
        buffer.append(", profile name: ");
        buffer.append(getICCProfileName(colorSpace.getProfile()));
        buffer.append("]");

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static String toString (SinglePixelPackedSampleModel sampleModel)
      {
        StringBuilder buffer = new StringBuilder("");
        buffer.append(sampleModel.getClass().getName());
        buffer.append("[width: ");
        buffer.append(sampleModel.getWidth());
        buffer.append(", height: ");
        buffer.append(sampleModel.getHeight());
        buffer.append(", numBands: ");
        buffer.append(sampleModel.getNumBands());
        buffer.append(", dataType: ");
        buffer.append(sampleModel.getDataType());
        buffer.append(", scanlineStride: ");
        buffer.append(sampleModel.getScanlineStride());
        buffer.append(", transferType: ");
        buffer.append(sampleModel.getTransferType());
        buffer.append(", numDataElements: ");
        buffer.append(sampleModel.getNumDataElements());
        buffer.append(", bitMasks: ");
        buffer.append(toString(sampleModel.getBitMasks(), 16));
        buffer.append(", bitOffsets: ");
        buffer.append(toString(sampleModel.getBitOffsets(), 10));
        buffer.append("]");

        return buffer.toString();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private static String toString (PixelInterleavedSampleModel sampleModel)
      {
        StringBuilder buffer = new StringBuilder("");
        buffer.append(sampleModel.getClass().getName());
        buffer.append("[width: ");
        buffer.append(sampleModel.getWidth());
        buffer.append(", height: ");
        buffer.append(sampleModel.getHeight());
        buffer.append(", numBands: ");
        buffer.append(sampleModel.getNumBands());
        buffer.append(", dataType: ");
        buffer.append(sampleModel.getDataType());
        buffer.append(", scanlineStride: ");
        buffer.append(sampleModel.getScanlineStride());
        buffer.append(", transferType: ");
        buffer.append(sampleModel.getTransferType());
        buffer.append(", numDataElements: ");
        buffer.append(sampleModel.getNumDataElements());
        buffer.append(", bandOffsets: ");
        buffer.append(toString(sampleModel.getBandOffsets(), 10));
        buffer.append(", bankIndices: ");
        buffer.append(toString(sampleModel.getBankIndices(), 10));
        buffer.append("]");

        return buffer.toString();
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnegative
    protected int normalizedAngle (int angle)
      {
        while (angle < 0)
          {
            angle += 360;  
          }
        
        return angle % 360;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected static ICC_Profile getICCProfile (RenderedImage image)
      {
        ColorSpace colorSpace = image.getColorModel().getColorSpace();

        if (colorSpace instanceof ICC_ColorSpace)
          {
            ICC_ColorSpace iccColorSpace = (ICC_ColorSpace)colorSpace;
            return iccColorSpace.getProfile();
          }

        return null;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected static String getICCProfileName (ICC_Profile profile)
      {
        if (profile == null)
          return null;

        byte[] xx = profile.getData(ICC_Profile.icSigProfileDescriptionTag);
        int offset = 12;
        int count;

        for (count = 1; xx[offset + count] != 0; count++);

        return new String(xx, 0, offset, count);
      }
  }
