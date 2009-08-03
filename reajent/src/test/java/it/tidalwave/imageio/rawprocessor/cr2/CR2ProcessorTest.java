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
package it.tidalwave.imageio.rawprocessor.cr2;

import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.ImageReaderTestSupport;
import it.tidalwave.imageio.cr2.CR2Metadata;
import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2ProcessorTest extends ImageReaderTestSupport
  {
    @Test(timeout=60000)
    public void testMIMEType()
      {
        assertMIMETypes("cr2", "image/x-canon-cr2");
      }

    @Test(timeout=600000)
    public void testEOS20D_1_1_0()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(3, ir.getNumThumbnails(0));
        final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
        assertNotNull(metadata);

        assertImage(ir, 3504, 2336);
        assertThumbnail(ir, 0, 1536, 1024);
        assertThumbnail(ir, 1, 160, 120);
        assertThumbnail(ir, 2, 384, 256);
        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 8); /** FIXME: must be 16 bits. */
        assertLoadThumbnail(ir, 0, 1536, 1024);
        assertLoadThumbnail(ir, 1, 160, 120);
        assertLoadThumbnail(ir, 2, 384, 256);

        assertRaster(image, path, "b77c62839719971223ff71ef9dc88b6d");

        close(ir);
      }

    @Test(timeout=600000)
    public void testEOS20D_2_0_3()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/2.0.3/XXXX0000.CR2";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(3, ir.getNumThumbnails(0));
        final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
        assertNotNull(metadata);

        assertImage(ir, 3504, 2336);
        assertThumbnail(ir, 0, 1536, 1024);
        assertThumbnail(ir, 1, 160, 120);
        assertThumbnail(ir, 2, 384, 256);
        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 8); /** FIXME: must be 16 bits. */
        assertLoadThumbnail(ir, 0, 1536, 1024);
        assertLoadThumbnail(ir, 1, 160, 120);
        assertLoadThumbnail(ir, 2, 384, 256);

        assertRaster(image, path, "67c7d9692b8f24dfc445896f548f4326");

        close(ir);
      }

    @Test(timeout=600000)
    public void testEOS30D_1_0_4()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(3, ir.getNumThumbnails(0));
        final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
        assertNotNull(metadata);

        assertImage(ir, 3504, 2336);
        assertThumbnail(ir, 0, 1728, 1152);
        assertThumbnail(ir, 1, 160, 120);
        assertThumbnail(ir, 2, 384, 256);
        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 8); /** FIXME: must be 16 bits. */
        assertLoadThumbnail(ir, 0, 1728, 1152);
        assertLoadThumbnail(ir, 1, 160, 120);
        assertLoadThumbnail(ir, 2, 384, 256);

        assertRaster(image, path, "0e020171ca1598e74ce25306fd8b88c3");

        close(ir);
      }

    @Test(timeout=600000)
    public void testEOS40D_1_0_3()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/canon/EOS40D/1.0.3/img_0003.cr2";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(3, ir.getNumThumbnails(0));
        final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
        assertNotNull(metadata);

        assertImage(ir, 3888, 2592);
        assertThumbnail(ir, 0, 1936, 1288);
        assertThumbnail(ir, 1, 160, 120);
        assertThumbnail(ir, 2, 486, 324);
        final BufferedImage image = assertLoadImage(ir, 3944, 2622, 3, 8); /** FIXME: must be 16 bits. */
        assertLoadThumbnail(ir, 0, 1936, 1288);
        assertLoadThumbnail(ir, 1, 160, 120);
        assertLoadThumbnail(ir, 2, 486, 324);

        assertRaster(image, path, "65d8c7c06390331b77576ab03572135f");

        close(ir);
      }
  }
