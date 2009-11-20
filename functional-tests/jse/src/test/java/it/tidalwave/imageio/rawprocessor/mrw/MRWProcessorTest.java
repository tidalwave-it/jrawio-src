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
package it.tidalwave.imageio.rawprocessor.mrw;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.test.ExpectedResults;
import it.tidalwave.imageio.test.NewImageReaderTestSupport;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.minolta.MinoltaRawData.PRD;
import it.tidalwave.imageio.minolta.MinoltaRawData.RIF;
import it.tidalwave.imageio.minolta.MinoltaRawData.WBG;
import it.tidalwave.imageio.mrw.MRWMetadata;
import it.tidalwave.imageio.mrw.MinoltaMakerNote;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;
import it.tidalwave.imageio.raw.TagRational;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MRWProcessorTest  extends NewImageReaderTestSupport
  {
    public MRWProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // Dynax 5D
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/5d/RAW_MINOLTA_5D.MRW").
                            image(3008, 2000, 3, 8, "17140ec9ceaad36963728c6599f180bd").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/5d/RAW_MINOLTA_5D.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3016, 2008, 3, 16, "de8ed2949e91924c8430f49d32602dc2").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // Dynax 7D
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7d/RAW_MINOLTA_7D_SRGB.MRW").
                            image(3008, 2000, 3, 8, "da688ad8abe48bb9afa9c61d7514b0a9").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7d/RAW_MINOLTA_7D_SRGB.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3016, 2008, 3, 16, "a14650ae72e8f919b2401f285092f99f").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // Dynax 7Hi
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7hi/RAW_MINOLTA_DIMAGE_7HI.MRW").
                            image(2560, 1920, 3, 8, "cbe688cb3c7d7cac88e09866e7bad8a3").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7hi/RAW_MINOLTA_DIMAGE_7HI.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2568, 1928, 3, 16, "b7a556ac8f162daa6a87f85439fa7624").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // Dynax 7i
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7i/RAW_MINOLTA_DIMAGE_7I.MRW").
                            image(2560, 1920, 3, 8, "ff887620cbda15d9bb9fc517731f3e44").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/7i/RAW_MINOLTA_DIMAGE_7I.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2568, 1928, 3, 16, "e277971086f1c786f08f8f93fd8c33ed").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // A1
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/a1/RAW_MINOLTA_A1.MRW").
                            image(2560, 1920, 3, 8, "9762c2585d3b2b73cb1cc54da33f8b44").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/a1/RAW_MINOLTA_A1.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2568, 1928, 3, 16, "f5cab08f838de5aa776a3fe2259dc677").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // A2
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/a2/RAW_MINOLTA_A2.MRW").
                            image(3264, 2448, 3, 8, "3103a7a3c1eccf06efec39d9a6e12792").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/minolta/a2/RAW_MINOLTA_A2.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3272, 2456, 3, 16, "a1b5e8b1821f8401ba49bb9794a2b727").
                            thumbnail(640, 480).
                            issues("JRW-235"),
            // Dynax 7D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/theoheinze/Minolta/Dynax7D/MRW/PICT0652.MRW").
                            image(3008, 2000, 3, 8, "0c011c16b6e2b511417dc484377ca239").
                            thumbnail(640, 480).
                            issues("JRW-152", "JRW-209"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/theoheinze/Minolta/Dynax7D/MRW/PICT0652.MRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3016, 2008, 3, 16, "152b5d95ad3bc6d69eb2f7da721bfc96").
                            thumbnail(640, 480).
                            issues("JRW-152", "JRW-201").
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
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
                                    assertEquals(1.74609375, wbg.getRedCoefficient().doubleValue(), 0);
                                    assertEquals(1.015625, wbg.getGreen1Coefficient().doubleValue(), 0);
                                    assertEquals(1.015625, wbg.getGreen2Coefficient().doubleValue(), 0);
                                    assertEquals(1.7734375, wbg.getBlueCoefficient().doubleValue(), 0);                                  }
                              })
          );
      }
  }
