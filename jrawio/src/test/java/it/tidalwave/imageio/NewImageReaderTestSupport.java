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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RunWith(value=Parameterized.class)
public class NewImageReaderTestSupport extends ImageReaderTestSupport
  {
    private final static String CLASS = NewImageReaderTestSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnull
    private final ExpectedResults expectedResults;

    protected NewImageReaderTestSupport (final @Nonnull ExpectedResults expectedResults)
      {
        this.expectedResults = expectedResults;
      }

    @Test(timeout=60000)
    public final void testImage()
      throws Exception
      {
        final List<Throwable> errors = new ArrayList<Throwable>();
        
        final ImageReader ir = getImageReader(expectedResults.getPath());
        final int imageCount = expectedResults.getImageCount();
        final int thumbnailCount = expectedResults.getThumbnailCount();
        assertEquals("image count", imageCount, ir.getNumImages(false));
        assertEquals("thumbnail count", thumbnailCount, ir.getNumThumbnails(0));

        for (int i = 0; i < imageCount; i++)
          {
            try
              {
                final ExpectedResults.Image expectedImage = expectedResults.getImage(i);
                final Dimension size = expectedImage.getSize();
                assertImage(ir, size.width, size.height);
              }
            catch (Throwable e)
              {
                errors.add(e);
              }
          }

        for (int t = 0; t < thumbnailCount; t++)
          {
            try
              {
                final ExpectedResults.Image expectedThumbnail = expectedResults.getThumbnail(t);
                final Dimension size = expectedThumbnail.getSize();
                assertThumbnail(ir, t, size.width, size.height);
              }
            catch (Throwable e)
              {
                errors.add(e);
              }
          }

        for (int i = 0; i < imageCount; i++)
          {
            try
              {
                final ExpectedResults.Image expectedImage = expectedResults.getImage(i);
                final Dimension size = expectedImage.getSize();
                final BufferedImage image = assertLoadImage(ir, size.width, size.height, expectedImage.getBandCount(), expectedImage.getBitsPerSample());
                assertRaster(image, expectedResults.getPath(), expectedImage.getFingerPrint());
              }
            catch (Throwable e)
              {
                errors.add(e);
              }
          }

        for (int t = 0; t < thumbnailCount; t++)
          {
            try
              {
                final ExpectedResults.Image expectedThumbnail = expectedResults.getThumbnail(t);
                final Dimension size = expectedThumbnail.getSize();
                assertLoadThumbnail(ir, t, size.width, size.height);
              }
            catch (Throwable e)
              {
                errors.add(e);
              }
          }

        try
          {
            expectedResults.getExtra().run(ir);
          }
        catch (Throwable e)
          {
            errors.add(e);
          }

        close(ir);

        if (!errors.isEmpty())
          {
            for (final Throwable error : errors)
              {
                logger.throwing(CLASS, "================================================================", error);
              }

            fail("" + errors.toString());
          }
      }

    @Nonnull
    protected static Collection<Object[]> fixed (final @Nonnull ExpectedResults ... er)
      {
        final List<Object[]> result = new ArrayList<Object[]>();

        for (final ExpectedResults e : er)
          {
            result.add(new Object[]{ e });
          }

        return result;
      }
  }
