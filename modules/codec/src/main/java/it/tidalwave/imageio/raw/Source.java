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
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * This class allows to implement a dynamic strategy to choose where to load the bits from. In fact, sometimes one might
 * wish that the read() method of RAWImageReader returns a reasonably large thumbnail, rather than the raw data.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class Source implements Serializable
  {
    private final static String CLASS = Source.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /*******************************************************************************************************************
     *
     * This source represents the image processed by the post-processor.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static final Source PROCESSED_IMAGE = new Source()
      {
        @Override
        @Nonnull
        protected Dimension getDimension (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            return new Dimension(ir.getWidth(0), ir.getHeight(0));
          }

        @Override
        @Nonnull
        protected BufferedImage readImage (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            return ((RAWImageReaderSupport)ir).loadImage(0);
          }

        @Override
        protected int getImageCount (final @Nonnull RAWImageReader ir)
          {
            return 1;
          }

        @Override
        protected boolean needsPostProcessor()
          {
            return true;
          }

        @Override
        @Nonnull
        public String toString()
          {
            return "PROCESSED_IMAGE";
          }
      };

    /*******************************************************************************************************************
     *
     * This source represents the raw image, not processed by the post-processor.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static final Source RAW_IMAGE = new Source()
      {
        @Override
        @Nonnull
        protected Dimension getDimension (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            return new Dimension(ir.getWidth(0), ir.getHeight(0));
          }

        @Override
        @Nonnull
        protected BufferedImage readImage (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            return ((RAWImageReaderSupport)ir).loadImage(0);
          }

        @Override
        protected boolean needsPostProcessor()
          {
            return false;
          }

        @Override
        protected int getImageCount (final @Nonnull RAWImageReader ir)
          {
            return 1;
          }

        @Override
        @Nonnull
        public String toString()
          {
            return "RAW_IMAGE";
          }
      };

    /*******************************************************************************************************************
     *
     * This source represents the full-size preview.
     *
     ******************************************************************************************************************/
    @Nonnull
    public static final Source FULL_SIZE_PREVIEW = new Source()
      {
        @Override
        @Nonnull
        protected Dimension getDimension (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            final int t = findFullSizeThumbnailIndex(ir);
            return new Dimension(ir.getThumbnailWidth(0, t), ir.getThumbnailHeight(0, t));
          }

        @Override
        @Nonnull
        protected BufferedImage readImage (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            final int t = findFullSizeThumbnailIndex(ir);
            return ir.readThumbnail(0, t);
          }

        @Override
        protected boolean needsPostProcessor()
          {
            return false;
          }

        @Override
        protected int getImageCount (final @Nonnull RAWImageReader ir)
          {
            try
              {
                findFullSizeThumbnailIndex(ir);
                return 1;
              }
            catch (IOException e)
              {
                return 0;
              }
          }

        @Override
        @Nonnull
        public String toString()
          {
            return "FULL_SIZE_PREVIEW";
          }

        private int findFullSizeThumbnailIndex (final @Nonnull RAWImageReader ir)
          throws IOException
          {
            ir.getImageMetadata(0); // needed to trigger the postprocessor and crop the size
            final int imageWidth = ir.getWidth(0);
            final int imageHeight = ir.getHeight(0);

            for (int t = 0; t < ir.getNumThumbnails(0); t++)
              {
                final int thumbnailWidth = ir.getThumbnailWidth(0, t);
                final int thumbnailHeight = ir.getThumbnailHeight(0, t);
                logger.finest(">>>> expected: %d x %d, probing: %d x %d",
                              imageWidth, imageHeight, thumbnailWidth, thumbnailHeight);
                //
                // Two comparisons to take care of rotated images.
                //
                if (((imageWidth == thumbnailWidth) && (imageHeight == thumbnailHeight)) ||
                    ((imageWidth == thumbnailHeight) && (imageHeight == thumbnailWidth)))
                  {
                    return t;
                  }
              }

            throw new IOException("Can't find full-size preview in this image");
          }
      };

    private final static Map<String, Source> DEFAULT_MAP = new HashMap<String, Source>();

    public final static String PROP_DEFAULT_SOURCE = "it.tidalwave.imageio.raw.defaultSource";
    public final static String DEFAULT_SOURCE_FULL_SIZE_PREVIEW = "fullSizePreview";
    public final static String DEFAULT_SOURCE_PROCESSED_IMAGE = "image";
    public final static String DEFAULT_SOURCE_RAW_IMAGE = "rawImage";

    static
      {
        DEFAULT_MAP.put("", PROCESSED_IMAGE);
        DEFAULT_MAP.put(DEFAULT_SOURCE_PROCESSED_IMAGE, PROCESSED_IMAGE);
        DEFAULT_MAP.put(DEFAULT_SOURCE_RAW_IMAGE, RAW_IMAGE);
        DEFAULT_MAP.put(DEFAULT_SOURCE_FULL_SIZE_PREVIEW, FULL_SIZE_PREVIEW);
      }

    protected Source()
      {
      }

    /*******************************************************************************************************************
     *
     * Returns the default source, that usually is {@link #PROCESSED_IMAGE}.
     *
     * @return  the default source
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Source getDefault()
      {
        final String value = System.getProperty(PROP_DEFAULT_SOURCE, "");
        final Source defaultSource = DEFAULT_MAP.get(value);

        if (defaultSource == null)
          {
            throw new IllegalArgumentException(String.format("Invalid value for %s: %s", PROP_DEFAULT_SOURCE, value));
          }

        return defaultSource;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected abstract Dimension getDimension (final @Nonnull RAWImageReader ir)
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected abstract BufferedImage readImage (final @Nonnull RAWImageReader ir)
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected abstract int getImageCount (final @Nonnull RAWImageReader ir);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected abstract boolean needsPostProcessor();
  }
