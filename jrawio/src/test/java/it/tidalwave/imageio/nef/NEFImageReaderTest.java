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
package it.tidalwave.imageio.nef;

import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.ImageReaderTestSupport;
import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFImageReaderTest extends ImageReaderTestSupport
  {
    @Test
    public void testMIMEType()
      {
        assertMIMETypes("nef", "image/x-nikon-nef");
      }

    @Test(timeout=60000)
    public void testJRW146() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3034, 2024);
        assertThumbnail(ir, 0, 120, 160);
        final BufferedImage image = assertLoadImage(ir, 3034, 2024, 3, 16);
        assertLoadThumbnail(ir, 0, 120, 160);
        close(ir);
        
        assertRaster(image, path, "3659664029723dc8ea29b09a923fca7d");
      }
    
    @Test(timeout=60000)
    public void testJRW148() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 160, 120);
        final BufferedImage image = assertLoadImage(ir, 3008, 2000, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        close(ir);
        
        assertRaster(image, path, "03383e837402452f7dc553422299f057");
      }
    
    @Test(timeout=60000)
    public void testJRW149_JRW150()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 106, 160);
        assertThumbnail(ir, 1, 384, 255);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 106, 160);
        assertLoadThumbnail(ir, 1, 384, 255);
        close(ir);

        assertRaster(image, path, "fa9bbd9ebe4b5f652c385c84ce33fd56");
      }

    @Test(timeout=60000)
    public void testJRW129()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "8fcbf7e059735e80e0bc2f1211794221");
      }

    @Test(timeout=60000)
    public void testNikonD70_1_0_1()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "f5d7d487f69fee3ef0b6b344729cf2cf");
      }

    @Test(timeout=60000)
    public void testNikonD70_1_0_2()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "e6f4da41f81d0565509b84d89f0d678e");
      }

    @Test(timeout=60000)
    public void testNikonD70_2_0_0()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "4cd1e5dc546289ced51b97996a04893f");
      }

    @Test(timeout=60000)
    public void testNikonD40_1_0_0()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "3b565723bb5b3c7db33fc2e69cca040c");
      }

    @Test(timeout=60000)
    public void testNikonD50_1_0_0()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3008, 2000);
        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3008, 2000);
        close(ir);

        assertRaster(image, path, "067d8ab1983f4e8801f13046fe426baf");
      }

    @Test(timeout=60000)
    public void testNikonD2X_1_0_1()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D2X/1.01/_DSC0733.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 4320, 2868);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 4288, 2848); 
        final BufferedImage image = assertLoadImage(ir, 4320, 2868, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 4288, 2848);
        close(ir);

        assertRaster(image, path, "0cb29a0834bb2293ee4bf0c09b201631");
      }

    @Test(timeout=60000)
    public void testNikonD2Xs_1_0_0()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 4320, 2868);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 4288, 2848); 
        final BufferedImage image = assertLoadImage(ir, 4320, 2868, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 4288, 2848);
        close(ir);

        assertRaster(image, path, "37d5aa7aab4e2d4fd667efb674f558ed");
      }

//    @Test(timeout=60000)
//    public void testNikonD80()
//      throws Exception
//      {
//        final String path = "http://rapidshare.de/files/31631976/DSC_0001.NEF";
//        final ImageReader ir = getImageReader(path);
//        assertEquals(1, ir.getNumImages(false));
//        assertEquals(2, ir.getNumThumbnails(0));
//        assertImage(ir, 3040, 2014);
//        assertThumbnail(ir, 0, 106, 160);
//        assertThumbnail(ir, 1, 384, 255);
//        final BufferedImage image = assertLoadImage(ir, 3040, 2014, 3, 16);
//        assertLoadThumbnail(ir, 0, 106, 160);
//        assertLoadThumbnail(ir, 1, 384, 255);
//        close(ir);
//
//        assertRaster(image, path, "fabdb471b9a972acf313ebe29bf338ea");
//      }

    @Test(timeout=60000)
    public void testNikonD200()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3904, 2616);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3872, 2592);
        final BufferedImage image = assertLoadImage(ir, 3904, 2616, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3872, 2592);
        close(ir);

        assertRaster(image, path, "fabdb471b9a972acf313ebe29bf338ea");
      }

    @Test(timeout=60000)
    public void testNikonD200_2()
      throws Exception
      {
        final String path = "http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3904, 2616);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 3872, 2592);
        final BufferedImage image = assertLoadImage(ir, 3904, 2616, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 3872, 2592);
        close(ir);

        assertRaster(image, path, "eca27d2b7ade85c4803f287b8c413844");
      }

    @Test(timeout=60000)
    public void testNikonD90_1()
      throws Exception
      {
        final String path = "http://jalbum.net/download/DSC_0067.NEF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 4352, 2868);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 4288, 2848);
        final BufferedImage image = assertLoadImage(ir, 4352, 2868, 3, 16, 0 /*BufferedImage.TYPE_INT_RGB*/);
        assertLoadThumbnail(ir, 0, 160, 120);
        assertLoadThumbnail(ir, 1, 4288, 2848);
        close(ir);

        assertRaster(image, path, "d780f19ff45049ba3d0fe17a8e96a08e");
      }
  }
