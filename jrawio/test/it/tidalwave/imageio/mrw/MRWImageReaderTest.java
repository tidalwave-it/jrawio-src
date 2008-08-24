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
package it.tidalwave.imageio.mrw;

import it.tidalwave.imageio.LoadTestSupport;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageReader;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWImageReaderTest extends LoadTestSupport
  {
    @Test
    public void testJRW152() 
      throws Exception 
      {
        final String path = "others/theoheinze/Minolta/Dynax7D/MRW/PICT0652.MRW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 640, 480);
        final BufferedImage image = assertLoadImage(ir, 3016, 2008);
        assertLoadThumbnail(ir, 0, 640, 480);
        close(ir);
        
        assertRaster(image, path, "1f83f407e5afe3299c4a6df1c9fd1710");
      }
  }
