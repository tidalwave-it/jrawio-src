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
package it.tidalwave.imageio.rawprocessor.arw;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
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
                            metadata("metadata.minoltaRawData.WBG.blueCoefficient", new TagRational(736, 256))
//                                    assertEquals(1.40234375, wbg.getRedCoefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen1Coefficient().doubleValue(), 0);
//                                    assertEquals(1.0, wbg.getGreen2Coefficient().doubleValue(), 0);
//                                    assertEquals(2.875, wbg.getBlueCoefficient().doubleValue(), 0);
          );
      }
  }
