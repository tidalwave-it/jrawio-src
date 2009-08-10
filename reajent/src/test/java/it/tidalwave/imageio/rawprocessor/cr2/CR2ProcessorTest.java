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
    private final static Dimension EOS20D_SENSOR_SIZE = new Dimension(3596, 2360);
    private final static Dimension EOS30D_SENSOR_SIZE = new Dimension(3596, 2360);
    private final static Dimension EOS40D_SENSOR_SIZE = new Dimension(3944, 2622);
    private final static Dimension EOS350D_SENSOR_SIZE = new Dimension(3516, 2328);

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
            // EOS20D v1.1.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            image(3504, 2336, 3, 8, "77bd8c04f2895087aefbfb022bfd3b51").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
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
                            image(3504, 2336, 3, 8, "6200fba42ece0cced0604e41226384b0").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/2.0.3/XXXX0000.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
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
//             EOS30D v1.0.4
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2").
                            image(3504, 2336, 3, 8, "d8cad9ef2f3cb8b3e82f0529057ff6e3").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
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
                            image(3888, 2592, 3, 8, "2e28cfb6f50551a031b142b013460de5") .
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            issues("JRW-208", "JRW-216", "JRW-217"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS40D/1.0.3/img_0003.cr2").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
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
