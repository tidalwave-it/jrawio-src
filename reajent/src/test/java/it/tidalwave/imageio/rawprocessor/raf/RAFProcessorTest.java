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
package it.tidalwave.imageio.rawprocessor.raf;

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
public class RAFProcessorTest extends ImageReaderTestSupport
  {
    // JIRA issue JRW-127
    @Test(timeout=60000)
    public void testJRW127() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/tmlehto/Fujifilm/S6500fd/RAF/DSCF6315.RAF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3592, 3591); // FIXME wrong
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 1600, 1200);
        final BufferedImage image = assertLoadImage(ir, 2896, 2182, 3, 8); // FIXME: should be 16
        assertLoadThumbnail(ir, 0, 160, 120);
//        assertLoadThumbnail(ir, 1, 1600, 1200); // FIXME
        
        assertRaster(image, path, "47df2750b17f3734d7ba9a7398033c31");
        
//        close(ir);
      }
  }
