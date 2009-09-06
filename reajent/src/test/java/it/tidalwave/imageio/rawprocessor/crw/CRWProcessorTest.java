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
package it.tidalwave.imageio.rawprocessor.crw;

import javax.annotation.Nonnull;
import java.util.Collection;
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
public class CRWProcessorTest extends NewImageReaderTestSupport
  {
    public CRWProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // Powershot G1
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g1/RAW_CANON_G1.CRW").
                            image(2048, 1536, 3, 8, "a38f045797f22b677fd290c0b9cea655").
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g1/RAW_CANON_G1.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2144, 1560, 3, 16, "355abf0c7c3721dbcbca3d452246e4e9").
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot G2
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g2/RAW_CANON_G2.CRW").
                            image(2272, 1704, 3, 8, "32a8cb46214a2eee44c1dd76f4b66c74").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g2/RAW_CANON_G2.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2376, 1728, 3, 16, "69ff6953ab3e8d4ade384c2adfd746de").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot G3
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g3/RAW_CANON_G3.CRW").
                            image(2272, 1704, 3, 8, "dd74edbb050d6374d88997f6effdedfe").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g3/RAW_CANON_G3.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2376, 1728, 3, 16, "80a365d5d638ba25f3b768048543059e").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot G5
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g5/RAW_CANON_G5_SRGB.CRW").
                            image(2592, 1944, 3, 8, "dc2badc860943ca9c6b0073726f6d7ca").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g5/RAW_CANON_G5_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2672, 1968, 3, 16, "e61e6e6a7b375911d7b993610a9150a7").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot G6
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g6/RAW_CANON_G6_SRGB.CRW").
                            image(3072, 2304, 3, 8, "002cd85abc3a8f42075b61270772f837").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g6/RAW_CANON_G6_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3160, 2344, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot G7
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g7/RAW_CANON_G7.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/g7/RAW_CANON_G7.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot Pro1
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/pro1/RAW_CANON_PRO1_ARGB.CRW").
                            image(3264, 2448, 3, 8, "7e2182ceaec2477a1f144babf299fe98").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/pro1/RAW_CANON_PRO1_ARGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3344, 2484, 3, 16, "15a9e9675854b748c81745fdca843a50").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot Pro70
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/pro70/RAW_CANON_PRO70_SRGB.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/pro70/RAW_CANON_PRO70_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot S2IS
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s2is/RAW_CANON_S2IS.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s2is/RAW_CANON_S2IS.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot S3IS
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s3is/RAW_CANON_S3IS.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s3is/RAW_CANON_S3IS.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot S5IS
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s5is/RAW_CANON_S5IS.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s5is/RAW_CANON_S5IS.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot SD750
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd750/RAW_CANON_SD750.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd750/RAW_CANON_SD750.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot SD900
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd900/RAW_CANON_IXUS900TI_CHDK.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd900/RAW_CANON_IXUS900TI_CHDK.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot SD950
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd950/RAW_CANON_SD950.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/sd950/RAW_CANON_SD950.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A550
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a550/RAW_CANON_A550.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a550/RAW_CANON_A550.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A570
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a570/RAW_CANON_A570IS.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a570/RAW_CANON_A570IS.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A610
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a610/RAW_CANON_A610.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a610/RAW_CANON_A610.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A620
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a620/RAW_CANON_A620.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a620/RAW_CANON_A620.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A710
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a710/RAW_CANON_A710.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a710/RAW_CANON_A710.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot A720IS
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a720is/RAW_CANON_A720IS.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/a720is/RAW_CANON_A720IS.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-264"),
            // Powershot S30
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s30/RAW_CANON_S30.CRW").
                            image(2048, 1536, 3, 8, "3f63be26c16fbfc4cc0804f9bbdb1583").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s30/RAW_CANON_S30.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2144, 1560, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot S40
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s40/RAW_CANON_S40.CRW").
                            image(2272, 1704, 3, 8, "de3c47c490443ec3f2dd758208e5a4da").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s40/RAW_CANON_S40.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2376, 1728, 3, 16, "62235d04c09dab11eda1dd060ee5b8c3").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot S45
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s45/RAW_CANON_S45.CRW").
                            image(2272, 1704, 3, 8, "dbe30788b80cbe5d1ae9a18a265c70f8").
                            thumbnail(160, 120).
                            thumbnail(2272, 1704).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s45/RAW_CANON_S45.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2376, 1728, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(160, 120).
                            thumbnail(2272, 1704).
                            issues("JRW-264"),
            // Powershot S50
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s50/RAW_CANON_S50.CRW").
                            image(2592, 1944, 3, 8, "3ef727aba4eed4dafc681e3bebd6f88b").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s50/RAW_CANON_S50.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2672, 1968, 3, 16, "7b7911d942138d6b9a80b39830976020").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot S60
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s60/RAW_CANON_S60_SRGB.CRW").
                            image(2592, 1944, 3, 8, "9e98b1d0dd5ed40474fb49a392ce0eba").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s60/RAW_CANON_S60_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2672, 1968, 3, 16, "7aa4fd5f8cd53073cc21243186dbc150").
                            thumbnail(160, 120).
                            thumbnail(640, 480).
                            issues("JRW-264"),
            // Powershot S70
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s70/RAW_CANON_S70_SRGB.CRW").
                            image(3072, 2304, 3, 8, "31aacdf1e4e24ab4d0aedc30672edcea").
                            thumbnail(160, 120).
                            thumbnail(3072, 2304).
                            issues("JRW-264"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/s70/RAW_CANON_S70_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3160, 2344, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(160, 120).
                            thumbnail(3072, 2304).
                            issues("JRW-264")

//            // EOS 10D
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/10d/RAW_CANON_10D.CRW").
//                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
//                            thumbnail(3072, 2048).
//                            issues("JRW-234"),
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/10d/RAW_CANON_10D.CRW").
//                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
//                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
//                            thumbnail(3072, 2048).
//                            issues("JRW-234"),
//            // EOS 300D
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/300d/RAW_CANON_300D.CRW").
//                            image(3072, 2048, 3, 8, "d9e8cb87977403cf0c04e32dc0bdfec9").
//                            thumbnail(3072, 2048).
//                            issues("JRW-234"),
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/300d/RAW_CANON_300D.CRW").
//                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
//                            image(3152, 2068, 3, 16, "b79a5a68ff43667883487cf8489d7fc7").
//                            thumbnail(3072, 2048).
//                            issues("JRW-234"),
//            // EOS D30
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d30/RAW_CANON_D30_SRGB.CRW").
//                            image(2160, 1440, 3, 8, "6df92e1d4589fb9fb2b353aaa9efc83b").
//                            thumbnail(1440, 960).
//                            issues("JRW-234"),
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d30/RAW_CANON_D30_SRGB.CRW").
//                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
//                            image(2224, 1456, 3, 16, "54d2db66bee9df2075527f405e1c651f").
//                            thumbnail(1440, 960).
//                            issues("JRW-234"),
//            // EOS D60
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d60/RAW_CANON_D60_ARGB.CRW").
//                            image(2224, 1456, 3, 8, "b499a77c82e5289d043e2f330c6fffba").
//                            thumbnail(1440, 960).
//                            issues("JRW-234"),
//            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d60/RAW_CANON_D60_ARGB.CRW").
//                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
//                            image(2224, 1456, 3, 16, "b499a77c82e5289d043e2f330c6fffba").
//                            thumbnail(1440, 960).
//                            issues("JRW-234"),
//            // EOS300D
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
//                            image(3072, 2048, 3, 8, "67994f630322d2bede87a2884f5f0965").
//                            thumbnail(2048, 1360).
//                            issues("JRW-104", "JRW-218", "JRW-219"),
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
//                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
//                            image(3152, 2068, 3, 16, "b499a77c82e5289d043e2f330c6fffba").
//                            thumbnail(2048, 1360).
//                            issues("JRW-104", "JRW-200").
//                            metadata("metadata.width", 3152).
//                            metadata("metadata.height", 2068).
//                            metadata("metadata.fileNumber", 1000056).
//                            metadata("metadata.imageWidth", 3072).
//                            metadata("metadata.imageHeight", 2048).
//                            metadata("metadata.thumbnailWidth", 2048).
//                            metadata("metadata.thumbnailHeight", 1360).
//                            metadata("metadata.pixelAspectRatio", 1.0f).
//                            metadata("metadata.rotation", 0).
//                            metadata("metadata.componentBitDepth", 8).
//                            metadata("metadata.colorBitDepth", 24).
//                            metadata("metadata.colorBW", 257).
////                            metadata("metadata.timeStampAsDate", 0).
//                            metadata("metadata.baseISO", 100).
//                            metadata("metadata.firmwareVersion", "Firmware Version 1.1.1").
//                            metadata("metadata.model", "Canon EOS 300D DIGITAL").
//                            metadata("metadata.sensorWidth", 3152).
//                            metadata("metadata.sensorHeight", 2068).
//                            metadata("metadata.sensorLeftBorder", 72).
//                            metadata("metadata.sensorTopBorder", 16).
//                            metadata("metadata.sensorRightBorder", 3143).
//                            metadata("metadata.sensorBottomBorder", 2063).
//                            metadata("metadata.focalLength", 28.0f).
//                            metadata("metadata.serialNumber", 1330526302).
//                            metadata("metadata.decoderTable", new int[] {0, 0, 0x202, 0x52B336}).
//                            metadata("metadata.whiteBalance", 0).
//                            metadata("metadata.whiteBalanceAsString", "auto").
//                            metadata("metadata.RBCoefficients", new double[] { 1.9963898916967509, 1.1778846153846154}).
//                            metadata("metadata.colorTemperatureAvailable", true).
//                            metadata("metadata.colorTemperature", 5200)
          );
      }
  }
