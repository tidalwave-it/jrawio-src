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
package it.tidalwave.imageio.rawprocessor.mrw;

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
public class MRWProcessorTest extends ImageReaderTestSupport
  {
    @Test(timeout=60000)
    public void testPICT0652_MRW() 
      throws Exception 
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/others/theoheinze/Minolta/Dynax7D/MRW/PICT0652.MRW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000); // FIXME should be cropped
        // FIXME: you should swap them, smaller first
        assertThumbnail(ir, 0, 640, 480);
        final BufferedImage image = assertLoadImage(ir, 3008, 2000, 3, 8); // FIXME: should be 16
        assertLoadThumbnail(ir, 0, 640, 480);
        
        assertRaster(image, path, "667cdf726ccfbd97294bcc5578bf4d85");
        
//        close(ir);
      }
  }
