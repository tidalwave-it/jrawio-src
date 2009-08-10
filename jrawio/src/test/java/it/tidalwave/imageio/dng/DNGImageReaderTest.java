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
package it.tidalwave.imageio.dng;

import javax.imageio.ImageReader;
import it.tidalwave.imageio.ImageReaderTestSupport;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DNGImageReaderTest extends ImageReaderTestSupport
  {
    @Test
    public void testMIMEType()
      {
        assertMIMETypes("dng", "image/x-adobe-dng");
      }

    @Test(timeout=60000)
    public void testLoadMetadata()
      throws Exception
      {
        final String path = "https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/Adobe/DNG/100_0056.DNG";
        final ImageReader ir = getImageReader(path, RAWImageReadParam.DEFAULT);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3088, 2055);
        assertThumbnail(ir, 0, 256, 171);
        assertThumbnail(ir, 1, 1024, 683);
        close(ir);
      }
  }
