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
package it.tidalwave.imageio.rawprocessor.nef;

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
public class NEFProcessorTest extends ImageReaderTestSupport
  {
//    @Test(timeout=60000)
    public void test1() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3034, 2024);
        assertThumbnail(ir, 0, 120, 160);
        final BufferedImage image = assertLoadImage(ir, 2000, 3008, 3, 8, BufferedImage.TYPE_INT_RGB); // FIXME: WRONG, should be 16 bits
        assertLoadThumbnail(ir, 0, 120, 160);
        close(ir);
        
        assertRaster(image, path, "76f41c2dd922e79ae605f5e042aa4d9c");
      }
    
//    @Test(timeout=60000)
    public void test2() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 160, 120);
        // FIXME: this is wrong, the TIF is being processed as it were a RAW
        final BufferedImage image = assertLoadImage(ir, 2982, 1976, 3, 8, BufferedImage.TYPE_INT_RGB); // FIXME: WRONG, should be 16 bits
        assertLoadThumbnail(ir, 0, 160, 120);
        close(ir);
        
        assertRaster(image, path, "d2693ae031e35e2899a11cd817d7c255");
      }
    
//    @Test(timeout=60000)
    public void test3()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 106, 160);
        assertThumbnail(ir, 1, 384, 255);
        final BufferedImage image = assertLoadImage(ir, 2000, 3008, 3, 8, BufferedImage.TYPE_INT_RGB); // FIXME: WRONG, should be 16 bits
        assertLoadThumbnail(ir, 0, 106, 160);
        assertLoadThumbnail(ir, 1, 384, 255);
        close(ir);

        assertRaster(image, path, "b151891922a90a61ca93dd849107a593");
      }

//    @Test(timeout=60000)
    public void testJSR129()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 2000, 3008, 3, 8, BufferedImage.TYPE_INT_RGB); // FIXME: WRONG, should be 16 bits
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "f5befb9e45972491aa951afa98216d25");
      }

    @Test(timeout=60000)
    public void testJSR187()
      throws Exception
      {
        final String path = "http://jalbum.net/download/DSC_0067.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 4352, 2868);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 4288, 2848);
        final BufferedImage image = assertLoadImage(ir, 4352, 2868, 3, 8, 0 /*BufferedImage.TYPE_INT_RGB*/); // FIXME: WRONG, should be 16 bits
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 4288, 2848);
        close(ir);

        assertRaster(image, path, "64f04a68a3806e4f31f47f40175bf974");
      }
  }
