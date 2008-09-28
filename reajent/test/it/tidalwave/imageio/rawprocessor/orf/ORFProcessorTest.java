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
package it.tidalwave.imageio.rawprocessor.orf;

import java.io.IOException;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import it.tidalwave.imageio.ImageReaderTestSupport;
import it.tidalwave.imageio.orf.CameraSettings;
import it.tidalwave.imageio.orf.Equipment;
import it.tidalwave.imageio.orf.FocusInfo;
import it.tidalwave.imageio.orf.ImageProcessing;
import it.tidalwave.imageio.orf.ORFMetadata;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.orf.RawDevelopment;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFProcessorTest extends ImageReaderTestSupport
  {
    @Test
    public void test1() 
      throws Exception 
      {
        final String path = "others/josephandre/Olympus/E510/ORF/_2090037.ORF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(1, ir.getNumThumbnails(0));
        assertImage(ir, 3720, 2800);
        assertThumbnail(ir, 0, 1600, 1200);
        final BufferedImage image = assertLoadImage(ir, 3720, 2800, 3, 8); // FIXME: wrong, should be 16 bits
        assertLoadThumbnail(ir, 0, 1600, 1200);
        
        final ORFMetadata metadata = (ORFMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);
        final OlympusMakerNote makerNote = metadata.getOlympusMakerNote();
        assertNotNull(makerNote);
        assertEquals(8, makerNote.getTags().size());
        
        final CameraSettings cameraSettings = makerNote.getOlympusCameraSettings();
        assertNotNull(cameraSettings);
        assertEquals(44, cameraSettings.getTags().size());

        final Equipment equipment = makerNote.getOlympusEquipment();
        assertNotNull(equipment);
        assertEquals(23, equipment.getTags().size());
        
        final FocusInfo focusInfo = makerNote.getOlympusFocusInfo();
        assertNotNull(focusInfo);
        assertEquals(59, focusInfo.getTags().size());
        
        final ImageProcessing imageProcessing = makerNote.getOlympusImageProcessing();
        assertNotNull(imageProcessing);
        assertEquals(142, imageProcessing.getTags().size());
        
        final RawDevelopment rawDevelopment = makerNote.getOlympusRawDevelopment();
        assertNotNull(rawDevelopment);
        assertEquals(14, rawDevelopment.getTags().size());
        
        close(ir);
        
        assertRaster(image, path, "df6dca1db04cb9e85c3429a68fbe88db");
      }
    
    @Test
    // JIRA JRW-160
    public void test2() 
      throws Exception 
      {
        final String path = "others/victoriagracia/Olympus/E500/ORF/V7020205.ORF";
        final ImageReader ir = getImageReader(path);
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3360, 2504);
        assertThumbnail(ir, 0, 160, 120);
        assertThumbnail(ir, 1, 0, 0); //  FIXME: broken
        final BufferedImage image = assertLoadImage(ir, 3360, 2504, 3, 8); // FIXME: wrong, should be 16
        assertLoadThumbnail(ir, 0, 160, 120);
//        assertLoadThumbnail(ir, 1, 0, 0); //  FIXME: broken
       
        assertRaster(image, path, "6ad55b40e6ccda056cb6acb865292abe");
        
        final ORFMetadata metadata = (ORFMetadata)ir.getImageMetadata(0);
        assertNotNull(metadata);
        final OlympusMakerNote makerNote = metadata.getOlympusMakerNote();
        assertNotNull(makerNote);
        assertEquals(27, makerNote.getTags().size());
        
        final CameraSettings cameraSettings = makerNote.getOlympusCameraSettings();
        assertNotNull(cameraSettings);
        assertEquals(40, cameraSettings.getTags().size());

        final Equipment equipment = makerNote.getOlympusEquipment();
        assertNotNull(equipment);
        assertEquals(23, equipment.getTags().size());
        
        final FocusInfo focusInfo = makerNote.getOlympusFocusInfo();
        assertNotNull(focusInfo);
        assertEquals(52, focusInfo.getTags().size());
        
        final ImageProcessing imageProcessing = makerNote.getOlympusImageProcessing();
        assertNotNull(imageProcessing);
        assertEquals(109, imageProcessing.getTags().size());
        
        final RawDevelopment rawDevelopment = makerNote.getOlympusRawDevelopment();
        assertNotNull(rawDevelopment);
        assertEquals(14, rawDevelopment.getTags().size());
        
        close(ir);
      }
  }
