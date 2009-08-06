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
package it.tidalwave.imageio;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;

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

    @Nonnull
    private final String path;

    private final List<Image> images = new ArrayList<Image>();
    private final List<Image> thumbnails = new ArrayList<Image>();

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
    public ExpectedResults thumbnail (final @Nonnegative int width,
                                      final @Nonnegative int height)
      {
        thumbnails.add(new Image(new Dimension(width, height), 3, 8, ""));
        return this;
      }

    @Nonnull
    public ExpectedResults issues (final @Nonnull String ... issues)
      {
        return this;
      }
  }
