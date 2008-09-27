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

import it.tidalwave.imageio.ImageReaderSupport;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.minolta.MinoltaRawData.RIF;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import it.tidalwave.imageio.raw.TagRational;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWImageReaderTest extends ImageReaderSupport
  {
    @Test
    public void testMIMEType()
      {
        assertMIMETypes("mrw", "image/x-minolta-mrw");
      }

    @Test
    public void testJRW152() 
      throws Exception 
      {
        final String path = "others/theoheinze/Minolta/Dynax7D/MRW/PICT0652.MRW";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));

        final MRWMetadata metadata = (MRWMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);
        final MinoltaMakerNote makerNote = metadata.getMinoltaMakerNote();
        assertNotNull(makerNote);
        final MinoltaRawData minoltaRawData = metadata.getMinoltaRawData();
        assertNotNull(minoltaRawData);
        final PRD prd = minoltaRawData.getPRD();
        assertEquals("21810002", prd.getVersion());
        assertEquals(new Dimension(3016, 2008), prd.getCcdSize());
        assertEquals(new Dimension(3008, 2000), prd.getImageSize());
        assertEquals(12, prd.getDataSize());
        assertEquals(12, prd.getPixelSize());
        assertEquals(0x59, prd.getStorageMethod());
        assertEquals(0, prd.getUnknown1());
        assertEquals(0, prd.getUnknown2());
        assertEquals(0, prd.getUnknown3());
        assertNotNull(prd);
        final RIF rif = minoltaRawData.getRIF();
        assertNotNull(rif);
        // TODO: assert rif stuff
        final WBG wbg = minoltaRawData.getWBG();
        assertNotNull(wbg);
        assertEquals(new TagRational(447, 256), wbg.getRedCoefficient());
        assertEquals(new TagRational(260, 256), wbg.getGreen1Coefficient());
        assertEquals(new TagRational(260, 256), wbg.getGreen2Coefficient());
        assertEquals(new TagRational(454, 256), wbg.getBlueCoefficient());
        assertEquals(1.74609375, wbg.getRedCoefficient().doubleValue());
        assertEquals(1.015625, wbg.getGreen1Coefficient().doubleValue());
        assertEquals(1.015625, wbg.getGreen2Coefficient().doubleValue());
        assertEquals(1.7734375, wbg.getBlueCoefficient().doubleValue());

        assertImage(ir, 3008, 2000);
        assertThumbnail(ir, 0, 640, 480);
        final BufferedImage image = assertLoadImage(ir, 3016, 2008, 3, 16);
        assertLoadThumbnail(ir, 0, 640, 480);
        close(ir);
        
        assertRaster(image, path, "9d72c3bed12267308c5914d48aec5197");
      }
  }
