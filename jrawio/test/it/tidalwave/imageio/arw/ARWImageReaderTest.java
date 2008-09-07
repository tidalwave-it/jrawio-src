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
package it.tidalwave.imageio.arw;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.LoadTestSupport;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.minolta.MinoltaRawData.RIF;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import it.tidalwave.imageio.mrw.MinoltaMakerNote;
import it.tidalwave.imageio.raw.TagRational;
import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWImageReaderTest extends LoadTestSupport
  {
    // JIRA issue JRW-127
    @Test
    public void testJRW127() 
      throws Exception 
      {
        final String path = "kijiro/Sony/A100/ARW/DSC00041.ARW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        final ARWMetadata metadata = (ARWMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);
//        final MinoltaMakerNote makerNote = metadata.getMinoltaMakerNote();
//        assertNotNull(makerNote);
        final MinoltaRawData minoltaRawData = metadata.getMinoltaRawData();
        assertNotNull(minoltaRawData);
        final PRD prd = minoltaRawData.getPRD();
        assertEquals("21870002", prd.getVersion());
        assertEquals(new Dimension(3880, 2608), prd.getCcdSize());
        assertEquals(new Dimension(3872, 2592), prd.getImageSize());
        assertEquals(16, prd.getDataSize());
        assertEquals(12, prd.getPixelSize());
        assertEquals(0x52, prd.getStorageMethod());
        assertEquals(1, prd.getUnknown1());
        assertEquals(1, prd.getUnknown2());
        assertEquals(0, prd.getUnknown3());
        assertNotNull(prd);
        final RIF rif = minoltaRawData.getRIF();
        assertNotNull(rif);
        // TODO: assert rif stuff
        final WBG wbg = minoltaRawData.getWBG();
        assertNotNull(wbg);
        assertEquals(new TagRational(359, 256), wbg.getRedCoefficient());
        assertEquals(new TagRational(256, 256), wbg.getGreen1Coefficient());
        assertEquals(new TagRational(256, 256), wbg.getGreen2Coefficient());
        assertEquals(new TagRational(736, 256), wbg.getBlueCoefficient());
        assertEquals(1.40234375, wbg.getRedCoefficient().doubleValue());
        assertEquals(1.0, wbg.getGreen1Coefficient().doubleValue());
        assertEquals(1.0, wbg.getGreen2Coefficient().doubleValue());
        assertEquals(2.875, wbg.getBlueCoefficient().doubleValue());
        
        assertImage(ir, 3880, 2608);
        // FIXME: you should swap them, smaller first
        assertThumbnail(ir, 0, 3872, 2592);
        assertThumbnail(ir, 1, 160, 120);
        final BufferedImage image = assertLoadImage(ir, 3880, 2608, 3, 16);
        assertLoadThumbnail(ir, 0, 640, 480); // FIXME: this is wrong, should be the 3872x2592
        assertLoadThumbnail(ir, 1, 160, 120);
        
        assertRaster(image, path, "43375f3493629d6ee80e13441d018649");
        
        close(ir);
      }
    

  }
