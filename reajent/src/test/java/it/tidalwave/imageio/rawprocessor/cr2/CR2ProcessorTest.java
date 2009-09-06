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
package it.tidalwave.imageio.rawprocessor.cr2;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.awt.Dimension;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2ProcessorTest extends NewImageReaderTestSupport
  {
    public CR2ProcessorTest (final @Nonnull ExpectedResults expectedResults)
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
                            image(3504, 2336, 3, 8, "4fd1614b374c437fcedb66b6576a24dd").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm2/RAW_CANON_1DM2.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3596, 2360, 3, 16, "4550a2de42fbaf3306c391e763ce9575").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1D mkII N
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm2n/RAW_CANON_1DM2N.CR2").
                            image(3504, 2336, 3, 8, "9122989c82f73a2a069dca14437f8c3b").
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
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm2n/RAW_CANON_1DM2N.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3596, 2360, 3, 16, "ed66afbf08943834ba7985a32e039d28").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1D mkIII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm3/RAW_CANON_1DMARK3.CR2").
                            image(3888, 2592, 3, 8, "72693102fc1d49a67f603c2a2569a077").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-233", "JRW-238", "JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dm3/RAW_CANON_1DMARK3.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3984, 2622, 3, 16, "6465ca47a83ad90702af2a1ee299bd3e").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-233"),
            // EOS 1Ds mkII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm2/RAW_CANON_1DSM2.CR2").
                            image(4992, 3328, 3, 8, "8c99e77e527d2c4cf117855635c63ed5").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm2/RAW_CANON_1DSM2.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(5108, 3349, 3, 16, "65fd3c49fd56f7f6af136ccf98391151").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 1Ds mkIII
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm3/RAW_CANON_1DSM3.CR2").
                            image(5616, 3744, 3, 8, "952dbb135790933a06cd16ba3e3ffa2c").
                            thumbnail(2784, 1856).
                            thumbnail(160, 120).
                            thumbnail(476, 312).
                            issues("JRW-233", "JRW-238", "JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/1dsm3/RAW_CANON_1DSM3.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(5712, 3774, 3, 16, "c0ef91e296adabf920a504ff53e4ba59").
                            thumbnail(2784, 1856).
                            thumbnail(160, 120).
                            thumbnail(476, 312).
                            issues("JRW-233"),
            // EOS 5D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/5d/RAW_CANON_5D_ARGB.CR2").
                            image(4368, 2912, 3, 8, "6ac13bff06588c76cc8a511376a62f24").
                            thumbnail(2496, 1664).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233", "JRW-239", "JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/5d/RAW_CANON_5D_ARGB.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4476, 2954, 3, 16, "876c2311b9544283107e2f537b8f4adf").
                            thumbnail(2496, 1664).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 5D mkII
            ExpectedResults.create("http://raw.fotosite.pl/download-Canon_5DMk2_Canon_EF_24-70L_ISO_50-25600_a._stopa/IMG_0001.CR2").
                            image(5616, 3744, 3, 8, "38499c0891c12fa9238272d5773224d5").
                            thumbnail(5616, 3744).
                            thumbnail(160, 120).
                            thumbnail(362, 234).
                            issues("JRW-253"),
            ExpectedResults.create("http://raw.fotosite.pl/download-Canon_5DMk2_Canon_EF_24-70L_ISO_50-25600_a._stopa/IMG_0001.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(5792, 3804, 3, 16, "7550d708623fb2d3cb8a150b09683bd9").
                            thumbnail(5616, 3744).
                            thumbnail(160, 120).
                            thumbnail(362, 234).
                            issues("JRW-253"),
            // EOS 50D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/50d/RAW_CANON_50D.CR2").
                            image(4752, 3168, 3, 8, "7e56badc2a14e995db9c5c0b11d40e4c").
                            thumbnail(4752, 3168).
                            thumbnail(160, 120).
                            thumbnail(604, 396).
                            issues("JRW-237", "JRW-238", "JRW-239", "JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/50d/RAW_CANON_50D.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4832, 3228, 3, 16, "982c9c5bbf8384ea5200aab51f92e8cf").
                            thumbnail(4752, 3168).
                            thumbnail(160, 120).
                            thumbnail(604, 396).
                            issues("JRW-233", "JRW-237"),
            // EOS 400D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/400d/RAW_CANON_400D_ARGB.CR2").
                            image(3888, 2592, 3, 8, "5f64994bb5b5127d5d1410be7e85e6e6").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/400d/RAW_CANON_400D_ARGB.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3948, 2622, 3, 16, "b335bb3bc9267a5d3b86e1a3a2e8f15f").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-233"),
            // EOS 450D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/450d/RAW_CANON_450D.CR2").
                            image(4272, 2848, 3, 8, "c8017a48cf5b2cde0bbb9b01459ea4df").
                            thumbnail(2256, 1504).
                            thumbnail(160, 120).
                            thumbnail(539, 356).
                            issues("JRW-233", "JRW-238", "JRW-248"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/450d/RAW_CANON_450D.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4312, 2876, 3, 16, "00fd4c4caa39a80f9173a5ab9a1034f8").
                            thumbnail(2256, 1504).
                            thumbnail(160, 120).
                            thumbnail(539, 356).
                            issues("JRW-233"),
            // EOS20D v1.1.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            image(3504, 2336, 3, 8, "a2c3ccfb292c6c3feaa4d307992468b2").
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
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3596, 2360, 3, 16, "992a061e12ac559e49e040344a9e3bb4").
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
                            image(3504, 2336, 3, 8, "d03c4f65666cd3f1b87593ecfa8883dc").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217", "JRW-248"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/2.0.3/XXXX0000.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3596, 2360, 3, 16, "d15a94cfc3615c0a399696f1af2cdbd1").
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
                            image(3504, 2336, 3, 8, "1c0f91691a5babeae072637fabc90ad1").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3596, 2360, 3, 16, "67872b80eca784066a997f0bd6d00d29").
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
                            image(3888, 2592, 3, 8, "837f1d34968e8298b8fd5499c5767041") .
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            // EOS40D v1.0.3
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS40D/1.0.3/img_0003.cr2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3944, 2622, 3, 16, "1ea35bbc10cf900fca0457ea531226d5").
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
                            image(3456, 2304, 3, 8, "d502b9e00b3d7507b8d39da9ec612d07").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-206", "JRW-208", "JRW-216", "JRW-217").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3516).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2328).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 52).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3507).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2322),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/marioivankovits/Canon/EOS305D/CR2/IMG_4707.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3516, 2328, 3, 16, "b629606121712938f27e60004880052c").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-206").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3516).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2328).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 52).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3507).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 19).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2322),
            // EOS 1000D
            ExpectedResults.create("http://raw.fotosite.pl/download-Canon_1000D_Canon_EF50_f1.8_by_Darren_Sim/IMG_0086.CR2").
                            image(3888, 2592, 3, 8, "4af6c11afeb3b9945e682fcb41974939").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-206", "JRW-253").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3948).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2622).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 52).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3939).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 23).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2614),
            // EOS 1000D
            ExpectedResults.create("http://raw.fotosite.pl/download-Canon_1000D_Canon_EF50_f1.8_by_Darren_Sim/IMG_0086.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3948, 2622, 3, 16, "42973595ef88a440db287ca72c984fcb").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-253").
                            metadata("metadata.canonMakerNote.sensorInfo.width", 3948).
                            metadata("metadata.canonMakerNote.sensorInfo.height", 2622).
                            metadata("metadata.canonMakerNote.sensorInfo.cropLeft", 52).
                            metadata("metadata.canonMakerNote.sensorInfo.cropRight", 3939).
                            metadata("metadata.canonMakerNote.sensorInfo.cropTop", 23).
                            metadata("metadata.canonMakerNote.sensorInfo.cropBottom", 2614)
          );
      }
  }
