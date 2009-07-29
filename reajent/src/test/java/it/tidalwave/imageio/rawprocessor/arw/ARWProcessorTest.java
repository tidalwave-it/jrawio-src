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
package it.tidalwave.imageio.rawprocessor.arw;

import javax.imageio.ImageReader;
import it.tidalwave.imageio.ImageReaderTestSupport;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWProcessorTest extends ImageReaderTestSupport
  {
    // JIRA issue JRW-127
    @Test(timeout=60000)
    public void testJRW127() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3880, 2608); // FIXME should be cropped
        // FIXME: you should swap them, smaller first
        assertThumbnail(ir, 0, 3872, 2592);
        assertThumbnail(ir, 1, 160, 120);
        final BufferedImage image = assertLoadImage(ir, 3872, 2592, 3, 8); // FIXME: should be 16
        assertLoadThumbnail(ir, 0, 640, 480); // FIXME: this is wrong, should be the 3872x2592
        assertLoadThumbnail(ir, 1, 160, 120);
        
        assertRaster(image, path, "7c26db64e13c8cabb251cc77ba2a042d");
        
//        close(ir);
      }
  }
