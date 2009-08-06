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
package it.tidalwave.imageio.rawprocessor.dng;

import javax.annotation.Nonnull;
import java.util.Collection;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import it.tidalwave.imageio.dng.DNGMetadata;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGProcessorTest extends NewImageReaderTestSupport
  {
    public DNGProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // EOS300D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/Adobe/DNG/100_0056.DNG").
                            image(3024, 2016, 3, 16, "a5ef709c014ce348c3e7932430bd93e0").
                            thumbnail(256, 171).
                            thumbnail(1024, 683).
                            issues("JRW-144").
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final DNGMetadata metadata = (DNGMetadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              }),
            // EOS300D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/Adobe/DNG/100_0043.DNG").
                            image(3024, 2016, 3, 16, "b96e7f37a44071e8a8c675cf97e55dda").
                            thumbnail(256, 171).
                            thumbnail(1024, 683).
                            issues("JRW-145").
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final DNGMetadata metadata = (DNGMetadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              })
          );
      }

    /*
    @Test(timeout=60000)
    public void testJRW165()
      throws Exception
      {
        final String path = "/home/fritz/Desktop/DSCF0001.dng";

        if (!new File(path).exists()) // DSCF0001.dng can't be disclosed - FIXME
          {
            System.err.println("WARNING: JRW165 skipped");
            return;
          }

        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, );
        assertThumbnail(ir, 0, 256, 171);
        assertThumbnail(ir, 1, 1024, 683);
        final BufferedImage image = assertLoadImage(ir, 3024, 2016, 3, 16);
        assertLoadThumbnail(ir, 0, 256, 171);
        assertLoadThumbnail(ir, 1, 1024, 683);
        close(ir);

        assertRaster(image, path, "9e4d2ce859bcb61601e118d0cd08a19b");
      }*/
  }
