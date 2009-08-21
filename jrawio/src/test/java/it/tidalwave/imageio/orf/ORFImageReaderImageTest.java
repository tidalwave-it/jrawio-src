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
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import javax.imageio.ImageReader;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFImageReaderImageTest extends NewImageReaderTestSupport
  {
    public ORFImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // Olympus C5050Z
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/c5050z/RAW_OLYMPUS_C5050Z.ORF").
                            image(2576, 1925, 3, 16, "227f76c084406d378d41144e1ed86383").
                            issues("JRW-231", "JRW-236"),
            // Olympus C8080
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/c8080/RAW_OLYMPUS_C8080.ORF").
                            image(3280, 2453, 3, 16, "666fd9b9387a70f40eed219f59f35c6c").
                            issues("JRW-231"),
            // Olympus E1
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e1/RAW_OLYMPUS_E1.ORF").
                            image(2624, 1966, 3, 16, "26ba31c2808b41f28d32b584a4a8fa24").
                            thumbnail(160, 120).
                            thumbnail(1280, 960).
                            issues("JRW-231"),
            // Olympus E20
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e20/RAW_OLYMPUS_E20.ORF").
                            image(2576, 1924, 3, 16, "d1014c28d519b947f046487d2187bc20").
                            thumbnail(160, 120).
                            issues("JRW-231"),
            // Olympus E3
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e3/RAW_OLYMPUS_E3.ORF").
                            image(3720, 2800, 3, 16, "025a705534ae469edecc5cc9395413f9").
                            thumbnail(1600, 1200).
                            issues("JRW-231", "JRW-236"),
            // Olympus E300
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e300/RAW_OLYMPUS_E300.ORF").
                            image(3360, 2504, 3, 16, "81979f8756494d246075e063520542b5").
                            thumbnail(160, 120).
                            thumbnail(1600, 1200).
                            issues("JRW-231"),
            // Olympus E410
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e410/RAW_OLYMPUS_E410.ORF").
                            image(3720, 2800, 3, 16, "51306e0c95ba333961335de94a0cfe13").
                            thumbnail(1600, 1200).
                            issues("JRW-231"),
            // Olympus E500
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/e500/RAW_OLYMPUS_E500.ORF").
                            image(3360, 2504, 3, 16, "824c2db682ad99c19054ef733b3e309d").
                            thumbnail(160, 120).
                            thumbnail(1600, 1200).
                            issues("JRW-231"),
            // Olympus SP350
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/sp350/RAW_OLYMPUS_SP350.ORF").
                            image(3280, 2453, 3, 16, "d6c7fa2afe61b7a565a9c82aaf8e0298").
                            issues("JRW-231", "JRW-236"),
            // Olympus SP500UZ
            ExpectedResults.create("http://www.rawsamples.ch/raws/olympus/sp500uz/RAW_OLYMPUS_SP500UZ.ORF").
                            image(2832, 2117, 3, 16, "de6f05f09bd271508cd18c6bd8e223e8").
                            issues("JRW-231", "JRW-236"),
            // Olympus E500
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/victoriagracia/Olympus/E500/ORF/V7020205.ORF").
                            image(3360, 2504, 3, 16, "4dfff3c25f1fdc8940ace2d079a09c6f").
                            thumbnail(160, 120).
                            thumbnail(1600, 1200).
                            issues("JRW-160", "JRW-202").
                            metadata("metadata.olympusMakerNote.imageWidth", 3264).
                            metadata("metadata.olympusMakerNote.imageHeight", 2448).
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
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
                                  }
                              }),
            // Olympus E510
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/josephandre/Olympus/E510/ORF/_2090037.ORF").
                            image(3720, 2800, 3, 16, "f823981ab27195c2db002ee03a65af84").
                            thumbnail(1600, 1200).
                            issues("JRW-151", "JRW-154", "JRW-155", "JRW-159").
                            metadata("metadata.olympusMakerNote.olympusImageProcessing.imageWidth", 3648).
                            metadata("metadata.olympusMakerNote.olympusImageProcessing.imageHeight", 2736).

                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
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
                                  }
                              })
          );
      }
  }
