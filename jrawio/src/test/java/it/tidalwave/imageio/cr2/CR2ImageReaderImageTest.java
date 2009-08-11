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
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2ImageReaderImageTest extends NewImageReaderTestSupport
  {
    private final static Dimension EOS20D_SENSOR_SIZE = new Dimension(3596, 2360);
    private final static Dimension EOS30D_SENSOR_SIZE = new Dimension(3596, 2360);
    private final static Dimension EOS40D_SENSOR_SIZE = new Dimension(3944, 2622);
    private final static Dimension EOS350D_SENSOR_SIZE = new Dimension(3516, 2328);

    public CR2ImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // EOS 1D mkII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm2/RAW_CANON_1DM2.CR2").
                            image(3596, 2360, 3, 16, "4550a2de42fbaf3306c391e763ce9575").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1D mkII N
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm2n/RAW_CANON_1DM2N.CR2").
                            image(3596, 2360, 3, 16, "ed66afbf08943834ba7985a32e039d28").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1D mkIII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm3/RAW_CANON_1DMARK3.CR2").
                            image(3984, 2622, 3, 16, "6465ca47a83ad90702af2a1ee299bd3e").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-233"),
            // EOS 1Ds mkII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm2/RAW_CANON_1DSM2.CR2").
                            image(5108, 3349, 3, 16, "65fd3c49fd56f7f6af136ccf98391151").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1Ds mkIII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm3/RAW_CANON_1DSM3.CR2").
                            image(5712, 3774, 3, 16, "c0ef91e296adabf920a504ff53e4ba59").
                            thumbnail(2784, 1856).
                            thumbnail(160, 120).
                            thumbnail(476, 312).
                            issues("JRW-233"),
            // EOS 5D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/5d/RAW_CANON_5D_ARGB.CR2").
                            image(4476, 2954, 3, 16, "876c2311b9544283107e2f537b8f4adf").
                            thumbnail(2496, 1664).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 50D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/50d/RAW_CANON_50D.CR2").
                            image(4832, 3228, 3, 16, "876c2311b9544283107e2f537b8f4adf").
                            thumbnail(4752, 3168).
                            thumbnail(160, 120).
                            thumbnail(604, 396).
                            issues("JRW-233"),
            // EOS 400D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/400d/RAW_CANON_400D_ARGB.CR2").
                            image(3948, 2622, 3, 16, "b335bb3bc9267a5d3b86e1a3a2e8f15f").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 450D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/450d/RAW_CANON_450D.CR2").
                            image(4312, 2876, 3, 16, "00fd4c4caa39a80f9173a5ab9a1034f8").
                            thumbnail(2256, 1504).
                            thumbnail(160, 120).
                            thumbnail(539, 356).
                            issues("JRW-233"),

            // EOS20D v1.1.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            image(EOS20D_SENSOR_SIZE, 3, 16, "992a061e12ac559e49e040344a9e3bb4").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-199").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3596).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2360).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 84).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3587).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2354),
            // EOS20D v2.0.3
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/2.0.3/XXXX0000.CR2").
                            image(EOS20D_SENSOR_SIZE, 3, 16, "d15a94cfc3615c0a399696f1af2cdbd1").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-199").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3596).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2360).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 84).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3587).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2354),
            // EOS30D v1.0.4
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2").
                            image(EOS30D_SENSOR_SIZE, 3, 16, "67872b80eca784066a997f0bd6d00d29").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-199").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3596).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2360).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 84).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3587).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2354),
            // EOS40D v1.0.3
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS40D/1.0.3/img_0003.cr2").
                            image(EOS40D_SENSOR_SIZE, 3, 16, "1ea35bbc10cf900fca0457ea531226d5").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-199").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3944).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2622).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 40).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3927).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 23).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2614),
            // EOS350D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/marioivankovits/Canon/EOS305D/CR2/IMG_4707.CR2").
                            image(EOS350D_SENSOR_SIZE, 3, 16, "b629606121712938f27e60004880052c").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-206").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3516).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2328).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 52).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3507).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2322)
          );
      }
  }
