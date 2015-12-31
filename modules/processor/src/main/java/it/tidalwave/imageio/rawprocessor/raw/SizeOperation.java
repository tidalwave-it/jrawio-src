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
package it.tidalwave.imageio.rawprocessor.raw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import it.tidalwave.imageio.raw.Source;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.PipelineArtifact;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class SizeOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(SizeOperation.class);
    
    protected final static TagRational ONE = new TagRational(1, 1);

    protected final static TagRational[] SCALE_UNCHANGED = new TagRational[] { ONE, ONE };

    protected final static Insets NULL_CROP = new Insets(0, 0, 0, 0);
        
    private final Properties properties = new Properties();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @SuppressWarnings("UI_INHERITANCE_UNSAFE_GETRESOURCE")
    public SizeOperation()
      {
        super(Source.Type.RAW);

        try
          {
            final String fileName = "/" + getClass().getName().replace('.', '/') + ".properties";
            final InputStream is = getClass().getResourceAsStream(fileName);

            if (is != null)
              {
                properties.load(is);
                is.close();
              }
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }        
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    public void process (@Nonnull final PipelineArtifact artifact)
      throws Exception
      {
        logger.fine("process(%s)", artifact);
        Insets crop = getCrop(artifact);

        if ((crop != null) && !crop.equals(NULL_CROP))
          {
            logger.finer(">>>> crop: %s", crop);
            artifact.setImage(crop(artifact.getImage(), crop));
          }

        final Dimension size = getSize(artifact);

        if ((size != null)) // FIXME && ((cropRectangle == null) || !cropRectangle.getSize().equals(size)))
          {
            logger.finer(">>>> size: %s", size);
            artifact.setImage(resample(artifact.getImage(), size));
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void processMetadata (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("processMetadata(%s)", artifact);
        artifact.getRAWMetadata().setOverriddenImageSize(getSize(artifact));
      } 
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Insets getCrop (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("getCrop(%s)", artifact);
        final TIFFMetadataSupport metadata = (TIFFMetadataSupport)artifact.getRAWMetadata();
        final IFD primaryIFD = metadata.getPrimaryIFD();
        Insets crop = getStandardCrop(primaryIFD.getModel());
        
        if (crop == null) // e.g. a new camera for which we don't have data
          {
            crop = new Insets(0, 0, 0, 0);
          }
        
        return crop;
      }
      
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Dimension getSize (@Nonnull final PipelineArtifact artifact)
      {
        logger.fine("getSize(%s)", artifact);
        final TIFFMetadataSupport metadata = (TIFFMetadataSupport)artifact.getRAWMetadata();
        final IFD primaryIFD = metadata.getPrimaryIFD();
        return getStandardSize(primaryIFD.getModel());
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage resample (@Nonnull final BufferedImage image, 
                                      @Nonnull final Dimension dimension)
      {
        logger.warning("resample(" + dimension + ") - NOT IMPLEMENTED");
        logImage(logger, ">>>> image: ", image);
        logImage(logger, ">>>> resample returning: ", image);
        return image; // FIXME: RESAMPLE
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage crop (@Nonnull BufferedImage image, 
                                  @Nonnull final Insets crop)
      {
        logger.finer("crop(%s)", crop);
        logImage(logger, ">>>> image: ", image);
        image = image.getSubimage(crop.left,
                                  crop.top, 
                                  image.getWidth() - crop.left - crop.right,
                                  image.getHeight() - crop.top - crop.bottom);
        logImage(logger, ">>>> crop returning: ", image);
        return image;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    protected static Dimension rotate (@Nullable final Dimension size, @Nonnegative int rotation)
      {
        logger.finer("rotate(%s, %d)", size, rotation);

        Dimension result = null;

        if (size != null)
          {
            switch (rotation)
              {
                case 0:
                case 180:
                  result = new Dimension(size);
                  break;

                case 90:
                case 270:
                  result = new Dimension(size.height, size.width);
                  break;

                default:
                  throw new IllegalArgumentException("rotation=" + rotation);
              }
          }

        logger.finest(">>>> returning: %s", result);

        return result;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Insets rotate (@Nonnull final Insets crop, @Nonnegative int rotation)
      {
        logger.finer("rotate(%s, %d)", crop, rotation);
        final Insets result = new Insets(0, 0, 0, 0);
        
        switch (rotation)
          {
            case 0:
              result.left   = crop.left;
              result.top    = crop.top;
              result.right  = crop.right;
              result.bottom = crop.bottom;
              break;
              
            case 90:
              result.left   = crop.top;
              result.top    = crop.right;
              result.right  = crop.bottom;
              result.bottom = crop.left;
              break;
              
            case 180:
              result.left   = crop.right;
              result.top    = crop.bottom;
              result.right  = crop.left;
              result.bottom = crop.top;
              break;
              
            case 270:
              result.left   = crop.bottom;
              result.top    = crop.left;
              result.right  = crop.top;
              result.bottom = crop.right;
              break;
              
            default: 
              throw new IllegalArgumentException("rotation=" + rotation);
          }
        
        logger.finest(">>>> returning: %s", result);
        
        return result;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected static Rectangle rotate (@Nonnull final Rectangle rectangle, 
                                       @Nonnull final Dimension size,
                                       @Nonnegative int rotation)
      {
        logger.finest("rotate(%s, %s, %d)", rectangle, size, rotation);        
        final Rectangle result = new Rectangle(0, 0, 0, 0);
        
        switch (rotation)
          {
            case 0:
              result.x      = rectangle.x;
              result.y      = rectangle.y;
              result.width  = rectangle.width;
              result.height = rectangle.height;
              break;
              
            case 90:
              result.x      = rectangle.y;
              result.y      = size.width - rectangle.x - rectangle.width;
              result.width  = rectangle.height;
              result.height = rectangle.width;
              break;
              
            case 180:
              result.x      = size.width - rectangle.x - rectangle.width;
              result.y      = size.height - rectangle.y - rectangle.height;
              result.width  = rectangle.width;
              result.height = rectangle.height;
              break;
              
            case 270:
              result.x      = size.height - rectangle.y - rectangle.height;
              result.y      = rectangle.x;
              result.width  = rectangle.height;
              result.height = rectangle.width;
              break;
              
            default: 
              throw new IllegalArgumentException("rotation=" + rotation);
          }
        
        logger.finest(">>>> returning: %s", result);
        
        return result;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Dimension getStandardSize (@Nonnull String model)
      {
        model = model.trim();
        logger.fine("getStandardSize(%s)", model);
        String string = properties.getProperty(model);
        Dimension size = null;
        
        if (string != null)
          {
            StringTokenizer st = new StringTokenizer(string, " \t");
            int width = Integer.parseInt(st.nextToken());
            int height = Integer.parseInt(st.nextToken());
            size = new Dimension(width, height);
          }
        
        logger.finer(">>>> size: %s", size);
        
        return size;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Insets getStandardCrop (@Nonnull String model)
      {
        model = model.trim();
        logger.fine("getStandardCrop(%s)", model);
        String string = properties.getProperty(model);
        Insets cropInsets = null;
        
        if (string != null)
          {
            StringTokenizer st = new StringTokenizer(string, " \t");
            st.nextToken();
            st.nextToken();
            int l = Integer.parseInt(st.nextToken());
            int r = Integer.parseInt(st.nextToken());
            int t = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            cropInsets = new Insets(t, l, b, r);
          }

        logger.finer(">>>> cropInsets: %s", cropInsets);
        
        return cropInsets;
      }
  }
