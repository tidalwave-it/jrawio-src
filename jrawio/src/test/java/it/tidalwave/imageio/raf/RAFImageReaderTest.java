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
package it.tidalwave.imageio.raf;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.ImageReaderTestSupport;
import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFImageReaderTest extends ImageReaderTestSupport
  {
    @Test(timeout=60000)
    public void testMIMEType()
      {
        assertMIMETypes("raf", "image/x-fuji-raf");
      }

    @Test(timeout=60000)
    public void testS6500fd()
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/tmlehto/Fujifilm/S6500fd/RAF/DSCF6315.RAF";
        final ImageReader ir = getImageReader(path);

        final RAFMetadata metadata = (RAFMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);

        final FujiRawData fujiRawData = metadata.getFujiRawData();
//        assertEquals("FUJIFILMCCD-RAW 0201FF389701FinePix S6500fd", fujiRawData.getHeader());
//        assertEquals("", fujiRawData.getB1());
//        assertEquals("0100", fujiRawData.getVersion());
//        assertEquals("", fujiRawData.getB2());
        assertEquals(148, fujiRawData.getJPEGImageOffset());
        assertEquals(650086, fujiRawData.getJPEGImageLength());
        assertEquals(650246, fujiRawData.getTable1Offset());
        assertEquals(4490, fujiRawData.getTable1Length());
        assertEquals(654736, fujiRawData.getCFAOffset());
        assertEquals(13043712, fujiRawData.getCFALength());
        assertEquals(0, fujiRawData.getUnused1());
        assertEquals(13043712, fujiRawData.getUnused2());
        assertEquals(0, fujiRawData.getUnused3());
        assertEquals(0, fujiRawData.getUnused4());
        assertEquals(0, fujiRawData.getUnused5());
        assertEquals(0, fujiRawData.getUnused6());
        assertEquals(0, fujiRawData.getUnused7());
        assertEquals(0, fujiRawData.getUnused8());
        assertEquals(0, fujiRawData.getUnused9());
        assertEquals(0, fujiRawData.getUnused10());

        final FujiTable1 fujiTable1 = fujiRawData.getFujiTable1();
        assertNotNull(fujiTable1);
        assertEquals(4096, fujiTable1.getWidth());
        assertEquals(1544, fujiTable1.getHeight());
        assertEquals(4224, fujiTable1.getRawWidth());
        assertEquals(1544, fujiTable1.getRawHeight());
        assertFalse(fujiTable1.isFujiLayout());

        final short[] coefficients = fujiTable1.getCoefficients();
        assertNotNull(coefficients);
        assertEquals(4, coefficients.length);
        assertEquals((short)336, coefficients[0]);
        assertEquals((short)518, coefficients[1]);
        assertEquals((short)336, coefficients[2]);
        assertEquals((short)489, coefficients[3]);

        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3592, 3591);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 1600, 1200);
        final BufferedImage image = assertLoadImage(ir, 3592, 3591, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
//FIXME        assertLoadThumbnail(ir, 1, 1600, 1200);

        assertRaster(image, path, "8c256e68fe9897a4fac12a06f1a07fb4");
        
        close(ir);
      }
  }
