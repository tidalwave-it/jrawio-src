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
    public void testReadPICT0006_MRW() 
      throws IOException 
      {
        final ImageReader ir = getImageReader("fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef");
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3034, 2024);
        assertThumbnail(ir, 0, 120, 160);
        assertLoadImage(ir, 3034, 2024);
        assertLoadThumbnail(ir, 0, 120, 160);
        close(ir);
      }
    
    @Test
    public void testReadPICT0006_MRW2() 
      throws IOException 
      {
        final ImageReader ir = getImageReader("fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF");
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 160, 120);
        assertLoadImage(ir, 3008, 2000);
        assertLoadThumbnail(ir, 0, 160, 120);
        close(ir);
      }
    
    @Test
    public void testReadPICT0006_MRW3() 
      throws IOException 
      {
        final ImageReader ir = getImageReader("mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF");
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3040, 2014);
        assertThumbnail(ir, 0, 106, 160);
        assertThumbnail(ir, 1, 384, 255);
        assertLoadImage(ir, 3040, 2014);
        assertLoadThumbnail(ir, 0, 106, 160);
        assertLoadThumbnail(ir, 1, 384, 255);
        close(ir);
      }
  }