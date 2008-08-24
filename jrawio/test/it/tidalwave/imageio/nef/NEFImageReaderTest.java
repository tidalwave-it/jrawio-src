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

import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.LoadTestSupport;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class NEFImageReaderTest extends LoadTestSupport
  {
    @Test
    public void testJRW146() 
      throws Exception 
      {
        final String path = "fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3034, 2024);
        assertThumbnail(ir, 0, 120, 160);
        final BufferedImage image = assertLoadImage(ir, 3034, 2024, 3, 16);
        assertLoadThumbnail(ir, 0, 120, 160);
        close(ir);
        
        assertRaster(image, path, "d06b8c69390bc8d6ec6863376f0911b8");
      }
    
    @Test
    public void testJRW148() 
      throws Exception 
      {
        final String path = "fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 160, 120);
        final BufferedImage image = assertLoadImage(ir, 3008, 2000, 3, 16);
        assertLoadThumbnail(ir, 0, 160, 120);
        close(ir);
        
        assertRaster(image, path, "6c77828177385260c656142b00c5d375");
      }
    
    @Test
    public void testJRW149_JRW150() 
      throws Exception 
      {
        final String path = "mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF";
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
        
        assertRaster(image, path, "a8509d077417e968a982c2579985d0d6");
      }
  }
