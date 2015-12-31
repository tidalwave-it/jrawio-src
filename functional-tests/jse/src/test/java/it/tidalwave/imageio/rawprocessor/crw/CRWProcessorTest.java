/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.rawprocessor.crw;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.imageio.test.ExpectedResults;
import it.tidalwave.imageio.test.NewImageReaderTestSupport;
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
            // EOS 10D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/10d/RAW_CANON_10D.CRW").
                            image(3072, 2048, 3, 8, "8af30532d1b7911d63852d710d722c5d").
                            thumbnail(3072, 2048).
                            issues("JRW-234"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/10d/RAW_CANON_10D.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "5c4fd5f6b64ddfcc31aaa3af9977a90a").
                            thumbnail(3072, 2048).
                            issues("JRW-234"),
            // EOS 300D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/300d/RAW_CANON_300D.CRW").
                            image(3072, 2048, 3, 8, "d9e8cb87977403cf0c04e32dc0bdfec9").
                            thumbnail(3072, 2048).
                            issues("JRW-234"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/300d/RAW_CANON_300D.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "b79a5a68ff43667883487cf8489d7fc7").
                            thumbnail(3072, 2048).
                            issues("JRW-234"),
            // EOS D30
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d30/RAW_CANON_D30_SRGB.CRW").
                            image(2160, 1440, 3, 8, "6df92e1d4589fb9fb2b353aaa9efc83b").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d30/RAW_CANON_D30_SRGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2224, 1456, 3, 16, "54d2db66bee9df2075527f405e1c651f").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            // EOS D60
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d60/RAW_CANON_D60_ARGB.CRW").
                            image(2224, 1456, 3, 8, "b499a77c82e5289d043e2f330c6fffba").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d60/RAW_CANON_D60_ARGB.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2224, 1456, 3, 16, "b499a77c82e5289d043e2f330c6fffba").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            // EOS300D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
                            image(3072, 2048, 3, 8, "67994f630322d2bede87a2884f5f0965").
                            thumbnail(2048, 1360).
                            issues("JRW-104", "JRW-218", "JRW-219"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3152, 2068, 3, 16, "b499a77c82e5289d043e2f330c6fffba").
                            thumbnail(2048, 1360).
                            issues("JRW-104", "JRW-200").
                            metadata("metadata.width", 3152).
                            metadata("metadata.height", 2068).
                            metadata("metadata.fileNumber", 1000056).
                            metadata("metadata.imageWidth", 3072).
                            metadata("metadata.imageHeight", 2048).
                            metadata("metadata.thumbnailWidth", 2048).
                            metadata("metadata.thumbnailHeight", 1360).
                            metadata("metadata.pixelAspectRatio", 1.0f).
                            metadata("metadata.rotation", 0).
                            metadata("metadata.componentBitDepth", 8).
                            metadata("metadata.colorBitDepth", 24).
                            metadata("metadata.colorBW", 257).
//                            metadata("metadata.timeStampAsDate", 0).
                            metadata("metadata.baseISO", 100).
                            metadata("metadata.firmwareVersion", "Firmware Version 1.1.1").
                            metadata("metadata.model", "Canon EOS 300D DIGITAL").
                            metadata("metadata.sensorWidth", 3152).
                            metadata("metadata.sensorHeight", 2068).
                            metadata("metadata.sensorLeftBorder", 72).
                            metadata("metadata.sensorTopBorder", 16).
                            metadata("metadata.sensorRightBorder", 3143).
                            metadata("metadata.sensorBottomBorder", 2063).
                            metadata("metadata.focalLength", 28.0f).
                            metadata("metadata.serialNumber", 1330526302).
                            metadata("metadata.decoderTable", new int[] {0, 0, 0x202, 0x52B336}).
                            metadata("metadata.whiteBalance", 0).
                            metadata("metadata.whiteBalanceAsString", "auto").
                            metadata("metadata.RBCoefficients", new double[] { 1.9963898916967509, 1.1778846153846154}).
                            metadata("metadata.colorTemperatureAvailable", true).
                            metadata("metadata.colorTemperature", 5200)
          );
      }
  }
