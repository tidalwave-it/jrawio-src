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
package it.tidalwave.imageio.test;

import it.tidalwave.imageio.raw.RAWImageReadParam;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.imageio.ImageReader;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class ExpectedResults
  {
    public static final class Image
      {
        @Nonnull
        private final Dimension size;

        @Nonnegative
        private final int bandCount;

        @Nonnegative
        private final int bitsPerSample;

        @Nonnull
        private final String fingerPrint;

        public Image (final @Nonnull Dimension size,
                      final @Nonnegative int bandCount,
                      final @Nonnegative int bitsPerSample,
                      final @Nonnull String fingerPrint)
          {
            this.size = size;
            this.bandCount = bandCount;
            this.bitsPerSample = bitsPerSample;
            this.fingerPrint = fingerPrint;
          }

        @Nonnull
        public Dimension getSize()
          {
            return size;
          }

        @Nonnegative
        public int getBandCount()
          {
            return bandCount;
          }

        @Nonnegative
        public int getBitsPerSample()
          {
            return bitsPerSample;
          }

        @Nonnull
        public String getFingerPrint()
          {
            return fingerPrint;
          }
      }

    public static interface Extra
      {
        public void run (@Nonnull ImageReader ir)
          throws Exception;
      }

    @Nonnull
    private final String path;

    @CheckForNull
    private RAWImageReadParam readParam;

    private final List<Image> images = new ArrayList<Image>();

    private final List<Image> thumbnails = new ArrayList<Image>();

    private Map<String, Object> properties = new HashMap<String, Object>();

    @CheckForNull
    private Class<? extends Throwable> imageException;

    @CheckForNull
    private Class<? extends Throwable> thumbnailException;

    @CheckForNull
    private Class<? extends Throwable> metadataException;

    @Nonnegative
    private Extra extra = new Extra()
      {
        public void run (@Nonnull final ImageReader ir)
          {
          }
      };

    private ExpectedResults (@Nonnull final String path)
      {
        this.path = path;
      }

    @Nonnull
    public static ExpectedResults create (final @Nonnull String path)
      {
        return new ExpectedResults(path);
      }

    @Nonnull
    public String getPath()
      {
        return path;
      }

    @CheckForNull
    public RAWImageReadParam getReadParam()
      {
        return readParam;
      }

    @Nonnegative
    public int getImageCount()
      {
        return images.size();
      }

    @Nonnegative
    public int getThumbnailCount()
      {
        return thumbnails.size();
      }

    @Nonnull
    public Image getImage (final @Nonnegative int index)
      {
        return images.get(index);
      }

    @Nonnull
    public Image getThumbnail (final @Nonnegative int index)
      {
        return thumbnails.get(index);
      }

    @Nonnull
    public Map<String, Object> getProperties()
      {
        return properties;
      }

    @Nonnull
    public Extra getExtra()
      {
        return extra;
      }

    @CheckForNull
    public Class<? extends Throwable> getImageException()
      {
        return imageException;
      }

    @CheckForNull
    public Class<? extends Throwable> getThumbnailException()
      {
        return thumbnailException;
      }

    @CheckForNull
    public Class<? extends Throwable> getMetadataException()
      {
        return metadataException;
      }

    @Nonnull
    public ExpectedResults param (final @Nonnull RAWImageReadParam readParam)
      {
        this.readParam = readParam;
        return this;
      }

    @Nonnull
    public ExpectedResults image (final @Nonnegative int width,
                                  final @Nonnegative int height,
                                  final @Nonnegative int bandCount,
                                  final @Nonnegative int bitsPerSample,
                                  final @Nonnull String fingerprint)
      {
        images.add(new Image(new Dimension(width, height), bandCount, bitsPerSample, fingerprint));
        return this;
      }

    @Nonnull
    public ExpectedResults image (final @Nonnull Dimension size,
                                  final @Nonnegative int bandCount,
                                  final @Nonnegative int bitsPerSample,
                                  final @Nonnull String fingerprint)
      {
        images.add(new Image(size, bandCount, bitsPerSample, fingerprint));
        return this;
      }

    @Nonnull
    public ExpectedResults thumbnail (final @Nonnegative int width,
                                      final @Nonnegative int height)
      {
        thumbnails.add(new Image(new Dimension(width, height), 3, 8, ""));
        return this;
      }

    @Nonnull
    public ExpectedResults thumbnail (final @Nonnull Dimension size)
      {
        thumbnails.add(new Image(size, 3, 8, ""));
        return this;
      }

    @Nonnull
    public ExpectedResults imageException (final @Nonnull Class<? extends Throwable> exception)
      {
        this.imageException = exception;
        return this;
      }

    @Nonnull
    public ExpectedResults thumbnailException (final @Nonnull Class<? extends Throwable> exception)
      {
        this.thumbnailException = exception;
        return this;
      }

    @Nonnull
    public ExpectedResults metadataException (final @Nonnull Class<? extends Throwable> exception)
      {
        this.metadataException = exception;
        return this;
      }

    @Nonnull
    public ExpectedResults issues (final @Nonnull String ... issues)
      {
        return this;
      }

    @Nonnull
    public ExpectedResults metadata (final @Nonnull String propertyName, final @Nullable Object value)
      {
        properties.put(propertyName, value);
        return this;
      }

    @Nonnull
    public ExpectedResults extra (final @Nonnull Extra extra)
      {
        this.extra = extra;
        return this;
      }

    @Override
    public String toString()
      {
        return path + " " + readParam;
      }
  }
