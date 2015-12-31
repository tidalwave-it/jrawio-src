/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.arw;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import it.tidalwave.imageio.test.ExpectedResults;
import it.tidalwave.imageio.test.NewImageReaderTestSupport;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;
import it.tidalwave.imageio.raw.TagRational;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWProcessorTest extends NewImageReaderTestSupport
  {
    public ARWProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // A100 v1.04
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW").
                            image(3872, 2592, 3, 8, "0fadabd72e76c535e39216d68a55d4b9").
                            thumbnail(640, 480).
                            thumbnail(160, 120).
                            issues("JRW-127", "JRW-198", "JRW-209"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3880, 2608, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(640, 480).
                            thumbnail(160, 120).
                            issues("JRW-127", "JRW-198").
                            metadata("metadata.minoltaRawData.PRD.version", "21870002").
                            metadata("metadata.minoltaRawData.PRD.ccdSize", new Dimension(3880, 2608)).
                            metadata("metadata.minoltaRawData.PRD.imageSize", new Dimension(3872, 2592)).
                            metadata("metadata.minoltaRawData.PRD.dataSize", 16).
                            metadata("metadata.minoltaRawData.PRD.pixelSize", 12).
                            metadata("metadata.minoltaRawData.PRD.storageMethod", 0x52).
                            metadata("metadata.minoltaRawData.PRD.unknown1", 1).
                            metadata("metadata.minoltaRawData.PRD.unknown2", 1).
                            metadata("metadata.minoltaRawData.PRD.unknown3", 0).
                                    // TODO: assert rif stuff
                            metadata("metadata.minoltaRawData.WBG.redCoefficient", new TagRational(359, 256)).
                            metadata("metadata.minoltaRawData.WBG.green1Coefficient", new TagRational(256, 256)).
                            metadata("metadata.minoltaRawData.WBG.green2Coefficient", new TagRational(256, 256)).
                            metadata("metadata.minoltaRawData.WBG.blueCoefficient", new TagRational(736, 256)),
//                                    assertEquals(1.40234375, wbg.getRedCoefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen1Coefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen2Coefficient().doubleValue(), 0);
//                                    assertEquals(2.875, wbg.getBlueCoefficient().doubleValue(), 0);
            // A200
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a200/RAW_SONY_A200.ARW").
                            image(3880, 2600, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-127", "JRW-198").
                            metadata("metadata.minoltaRawData.PRD.version", "21870002").
                            metadata("metadata.minoltaRawData.PRD.ccdSize", new Dimension(3880, 2608)).
                            metadata("metadata.minoltaRawData.PRD.imageSize", new Dimension(3872, 2592)).
                            metadata("metadata.minoltaRawData.PRD.dataSize", 16).
                            metadata("metadata.minoltaRawData.PRD.pixelSize", 12).
                            metadata("metadata.minoltaRawData.PRD.storageMethod", 0x52).
                            metadata("metadata.minoltaRawData.PRD.unknown1", 1).
                            metadata("metadata.minoltaRawData.PRD.unknown2", 1).
                            metadata("metadata.minoltaRawData.PRD.unknown3", 0).
                                    // TODO: assert rif stuff
                            metadata("metadata.minoltaRawData.WBG.redCoefficient", new TagRational(359, 256)).
                            metadata("metadata.minoltaRawData.WBG.green1Coefficient", new TagRational(256, 256)).
                            metadata("metadata.minoltaRawData.WBG.green2Coefficient", new TagRational(256, 256)).
                            metadata("metadata.minoltaRawData.WBG.blueCoefficient", new TagRational(736, 256)).
//                                    assertEquals(1.40234375, wbg.getRedCoefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen1Coefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen2Coefficient().doubleValue(), 0);
//                                    assertEquals(2.875, wbg.getBlueCoefficient().doubleValue(), 0);
                            issues("JRW-257", "JRW-258"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a200/RAW_SONY_A200.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3880, 2600, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A300
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a300/RAW_SONY_A300.ARW").
                            image(3880, 2600, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a300/RAW_SONY_A300.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3880, 2600, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A350
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a350/RAW_SONY_A350.ARW").
                            image(4600, 3064, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1920, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a350/RAW_SONY_A350.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4600, 3064, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1920, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A700
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A700_35mm_f1.4G_aperture_test_Ninik/DSC01592.ARW").
                            image(4288, 2856, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A700_35mm_f1.4G_aperture_test_Ninik/DSC01592.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2856, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A900
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A900_Carl_Zeiss_85mm_f1.4_by_Ninik/DSC08682.ARW").
                            image(6080, 4048, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A900_Carl_Zeiss_85mm_f1.4_by_Ninik/DSC08682.ARW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(6080, 4048, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258")
          );
      }
  }
