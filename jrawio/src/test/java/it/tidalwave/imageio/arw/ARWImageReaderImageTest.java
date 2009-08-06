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
package it.tidalwave.imageio.arw;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.minolta.MinoltaRawData.RIF;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWImageReaderImageTest extends NewImageReaderTestSupport
  {
    public ARWImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // D2X v1.0.1
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW").
                            image(3880, 2608, 3, 16, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(3872, 2592).
                            thumbnail(160, 120).
                            issues("JRW-127").
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
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
                                    assertEquals(1.40234375, wbg.getRedCoefficient().doubleValue(), 0);
                                    assertEquals(1.0, wbg.getGreen1Coefficient().doubleValue(), 0);
                                    assertEquals(1.0, wbg.getGreen2Coefficient().doubleValue(), 0);
                                    assertEquals(2.875, wbg.getBlueCoefficient().doubleValue(), 0);                                  }
                              })
          );
      }
  }
