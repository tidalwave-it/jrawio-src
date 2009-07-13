/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import javax.imageio.ImageReader;
import it.tidalwave.imageio.ImageReaderTestSupport;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFImageReaderTest extends ImageReaderTestSupport
  {
    @Test
    public void testMIMEType()
      {
        assertMIMETypes("nef", "image/x-nikon-nef");
      }

    @Test
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
        
        assertRaster(image, path, "a370faff7f544f8ec51d872cf2d227f1");
      }
    
    @Test
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
        
        assertRaster(image, path, "09b14dcaaabb6004ad9df4d4999904eb");
      }
    
    @Test
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

        assertRaster(image, path, "3c9baff5d7f0fc4cd0ad5f381a518e7a");
      }

    @Test
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

        assertRaster(image, path, "13c348ef117741937f77f9971e4d52fb");
      }

    @Test
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

//        assertRaster(image, path, "13c348ef117741937f77f9971e4d52fb");
      }

    @Test
    public void testNikonXXX()
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

//        assertRaster(image, path, "13c348ef117741937f77f9971e4d52fb");
      }
  }
