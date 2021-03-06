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
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.test;

import it.tidalwave.thesefoolishthings.junit.MyParameterized;
import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RunWith(value=MyParameterized.class)
public class NewImageReaderTestSupport extends ImageReaderTestSupport
  {
    private final static String CLASS = NewImageReaderTestSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    @Nonnull
    private final ExpectedResults expectedResults;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected NewImageReaderTestSupport (final @Nonnull ExpectedResults expectedResults)
      {
        this.expectedResults = expectedResults;
      }

    /*******************************************************************************************************************
     *
     * Makes sure the test file is loaded outside the time-guarded window.
     *
     ******************************************************************************************************************/
    @Before
    public final void ensureTestFileIsLoaded()
      throws IOException
      {
        getTestFile(expectedResults.getPath());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Test(timeout=60000)
    public final void testImage()
      throws Exception
      {
        final long time = System.currentTimeMillis();
        final ImageReader ir = getImageReader(expectedResults.getPath(), expectedResults.getReadParam());
        final int imageCount = expectedResults.getImageCount();
        final int thumbnailCount = expectedResults.getThumbnailCount();
        assertEquals("image count", imageCount, ir.getNumImages(false));
        assertEquals("thumbnail count", thumbnailCount, ir.getNumThumbnails(0));
        Class<? extends Throwable> imageException = expectedResults.getImageException();
        Class<? extends Throwable> thumbnailException = expectedResults.getThumbnailException();
        Class<? extends Throwable> metadataException = expectedResults.getMetadataException();

        for (int i = 0; i < imageCount; i++)
          {
            try
              {
                final ExpectedResults.Image expectedImage = expectedResults.getImage(i);
                final Dimension size = expectedImage.getSize();
                assertImageMetadataSize(ir, size.width, size.height);

                if (metadataException != null)
                  {
                    errors.addError(new AssertionError("Expected " + metadataException));
                  }
              }
            catch (Throwable e)
              {
                if ((metadataException == null) || !(metadataException.equals(e.getClass())))
                  {
                    errors.addError(e);
                  }
              }
          }

        for (int t = 0; t < thumbnailCount; t++)
          {
            try
              {
                final ExpectedResults.Image expectedThumbnail = expectedResults.getThumbnail(t);
                final Dimension size = expectedThumbnail.getSize();
                assertThumbnailMetadataSize(ir, t, size.width, size.height);

                if (metadataException != null)
                  {
                    errors.addError(new AssertionError("Expected " + metadataException));
                  }
              }
            catch (Throwable e)
              {
                if ((metadataException == null) || !(metadataException.equals(e.getClass())))
                  {
                    errors.addError(e);
                  }
              }
          }

        final RAWImageReadParam readParam = expectedResults.getReadParam();
        final String suffix = (readParam == null) ? "NO_PARAM" : readParam.lookup(Source.class).toString();

        for (int i = 0; i < imageCount; i++)
          {
            try
              {
                final ExpectedResults.Image expectedImage = expectedResults.getImage(i);
                final Dimension size = expectedImage.getSize();
                final BufferedImage image = assertLoadImage(ir, expectedResults.getReadParam(), size.width, size.height, expectedImage.getBandCount(), expectedImage.getBitsPerSample());
                assertRaster(image, expectedResults.getPath(), expectedImage.getFingerPrint(), "-" + suffix);

                if (imageException != null)
                  {
                    errors.addError(new AssertionError("Expected " + imageException));
                  }
              }
            catch (Throwable e)
              {
                if ((imageException == null) || !(imageException.equals(e.getClass())))
                  {
                    errors.addError(e);
                  }
              }
          }

        for (int t = 0; t < thumbnailCount; t++)
          {
            try
              {
                final ExpectedResults.Image expectedThumbnail = expectedResults.getThumbnail(t);
                final Dimension size = expectedThumbnail.getSize();
                final BufferedImage thumbnail = assertLoadThumbnail(ir, t, size.width, size.height);
                assertRaster(thumbnail, expectedResults.getPath(), null, "-" + suffix + "-thumb" + t);

                if (thumbnailException != null)
                  {
                    errors.addError(new AssertionError("Expected " + thumbnailException));
                  }
              }
            catch (Throwable e)
              {
                if ((thumbnailException == null) || !(thumbnailException.equals(e.getClass())))
                  {
                    errors.addError(e);
                  }
              }
          }

        for (final Entry<String, Object> entry : expectedResults.getProperties().entrySet())
          {
            final String[] path = entry.getKey().split("\\.");
            Object object = null;

            for (final String property : path)
              {
                if ((object == null) && "metadata".equals(property))
                  {
                    object = ir.getImageMetadata(0);
                  }
                else
                  {
                    Method getter = null;
                    
                    try
                      {
                        getter = object.getClass().getMethod("get" + capitalized(property));
                      }
                    catch (NoSuchMethodException e1)
                      {
                        try
                          {
                            getter = object.getClass().getMethod("is" + capitalized(property));
                          }
                        catch (NoSuchMethodException e2)
                          {
                            throw new NoSuchMethodException("Can't find getter for " + property);
                          }
                      }

                    object = getter.invoke(object);
                  }
              }

            try
              {
                final Object expectedValue = entry.getValue();

                if ((expectedValue != null) && expectedValue instanceof byte[])
                  {
                    assertArrayEquals(entry.getKey(), (byte[])expectedValue, (byte[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof short[])
                  {
                    assertArrayEquals(entry.getKey(), (short[])expectedValue, (short[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof int[])
                  {
                    assertArrayEquals(entry.getKey(), (int[])expectedValue, (int[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof long[])
                  {
                    assertArrayEquals(entry.getKey(), (long[])expectedValue, (long[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof char[])
                  {
                    assertArrayEquals(entry.getKey(), (char[])expectedValue, (char[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof float[])
                  {
                    assertFArrayEquals(entry.getKey(), (float[])expectedValue, (float[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof double[])
                  {
                    assertDArrayEquals(entry.getKey(), (double[])expectedValue, (double[])object);
                  }
                else if ((expectedValue != null) && expectedValue instanceof Object[])
                  {
                    assertArrayEquals(entry.getKey(), (Object[])expectedValue, (Object[])object);
                  }
                else
                  {
                    assertEquals(entry.getKey(), expectedValue, object);
                  }
              }
            catch (Throwable e)
              {
                errors.addError(e);
              }
          }

        try
          {
            expectedResults.getExtra().run(ir);
          }
        catch (Throwable e)
          {
            errors.addError(e);
          }

        close(ir);
        System.err.printf(">>>> Completed in %d msec.\n", System.currentTimeMillis() - time);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private final static String capitalized (final @Nonnull String string)
      {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void assertFArrayEquals (final @Nonnull String message,
                                     final @Nonnull float[] expected,
                                     final @Nonnull float[] actual)
      {
        assertEquals(message + ": arrays different size", expected.length, actual.length);

        for (int i = 0; i < expected.length; i++)
          {
            assertEquals(message + ": arrays first differed at element [" + i + "]", expected[i], actual[i], 0.0f);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void assertDArrayEquals (final @Nonnull String message,
                                     final @Nonnull double[] expected,
                                     final @Nonnull double[] actual)
      {
        assertEquals(message + ": arrays different size", expected.length, actual.length);

        for (int i = 0; i < expected.length; i++)
          {
            assertEquals(message + ": arrays first differed at element [" + i + "]", expected[i], actual[i], 0.0);
          }
      }
  }
