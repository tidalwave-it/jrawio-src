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
package it.tidalwave.imageio.crw;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.ImageReaderSupport;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class CRWImageReaderTest extends ImageReaderSupport
  {
    @Test
    public void testMIMEType()
      {
        assertMIMETypes("crw", "image/x-canon-crw");
      }

    @Test
    public void testEOS300D()
      throws Exception 
      {
        final String path = "esordini/Canon/EOS300D/CRW/100_0056.CRW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        final CRWMetadata metadata = (CRWMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);
        
        assertImage(ir, 3072, 2048);
        assertThumbnail(ir, 0, 2048, 1360);
        final BufferedImage image = assertLoadImage(ir, 3152, 2068, 3, 16);
        assertLoadThumbnail(ir, 0, 2048, 1360); 
        
        assertRaster(image, path, "5ecda7d8c4120a468a29c8dc5fd135c2");
        
        close(ir);
      }
    

  }
