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
package it.tidalwave.imageio.rawprocessor.nef;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFProcessorTest  extends NewImageReaderTestSupport
  {
    public NEFProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // D1x
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d1x/RAW_NIKON_D1X.NEF").
                            image(2000, 1312, 3, 8, "d3d3b27908bc6f9ed97d1f68c9d7a4af").
                            thumbnail(160, 120),
            // D3
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3/RAW_NIKON_D3.NEF").
                            image(4288, 2844, 3, 8, "ab2d2c8779c7938de85b5023d933811a").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832),
            // D3x
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3x/RAW_NIKON_D3X.NEF").
                            image(6080, 4044, 3, 8, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(6048, 4032),
            // D300
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d300/RAW_NIKON_D300.NEF").
                            image(4352, 2868, 3, 8, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D700
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d700/RAW_NIKON_D700.NEF").
                            image(4288, 2844, 3, 8, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832),
            // D60
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d60/RAW_NIKON_D60.NEF").
                            image(4320, 2868, 3, 8, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848)

//            // D1
//            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d1/RAW_NIKON_D1.NEF").
//                            image(2000, 1312, 3, 8, "f62836d70fab86475a155178f18cd1aa").
//                            thumbnail(160, 120),
//            // D2X v1.0.1
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2X/1.01/_DSC0733.NEF").
//                            image(4288, 2848, 3, 8, "f0c8a971dc82b31f1de739d7fb3d8a10").
//                            thumbnail(160, 120).
//                            thumbnail(4288, 2848),
//            // D2Xs v1.0.0
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef").
//                            image(4288, 2848, 3, 8, "f7d94c239bcc9bf2c8fd1b180049b401").
//                            thumbnail(160, 120).
//                            thumbnail(4288, 2848).
//                            issues("JRW-215"),
//            // D40 v1.0.0
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF").
//                            image(2000, 3008, 3, 8, "51f5ec81eff835ac17061b050f247fe5").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000).
//                            issues("JRW-215"),
//            // D50 v1.0.0
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF").
//                            image(3008, 2000, 3, 8, "5b785ffd3a34430c58e56ea5b204d7d3").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000),
//            // D50
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF").
//                            image(2000, 3008, 3, 8, "3a5207518665f8a39abfbee7d5b39a1b").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000).
//                            issues("JRW-129"),
//            // D70 v1.0.1
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF").
//                            image(3008, 2000, 3, 8, "72f0034d835dbbc0afe9d65255de9423").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000),
//            // D70 v1.0.2
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF").
//                            image(2000, 3008, 3, 8, "2a431f6635b9f51b33aab9854a3c5dac").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000),
//            // D70 v2.0.0
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF").
//                            image(3008, 2000, 3, 8, "7cdf5a744af7bf39d6988dfc29f5bb17").
//                            thumbnail(160, 120).
//                            thumbnail(3008, 2000),
//            // D70s
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF").
//                            image(2000, 3008, 3, 8, "5f72336ecb735433fb379fcc830b6ae6").
//                            thumbnail(106, 160).
//                            thumbnail(384, 255).
//                            issues("JRW-149", "JRW-150"),
//            // D80
//            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d80/RAW_NIKON_D80_SRGB.NEF").
//                            image(2592, 3872, 3, 8, "bfce1c1d61ea4769e8ba20e050f3be70").
//                            thumbnail(160, 120).
//                            thumbnail(3872, 2592),
//            // D90
//            ExpectedResults.create("http://jalbum.net/download/DSC_0067.NEF").
//                            image(4288, 2848, 3, 8, "f2bced57381390ff015bf0fe671fdeab").
//                            thumbnail(160, 120).
//                            thumbnail(4288, 2848).
//                            issues("JRW-187", "JRW-215"),
//            // D100
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
//                            image(2000, 3008, 3, 8, "f9fab4ee10aa950bfa30426783ea3fda").
//                            thumbnail(120, 160).
//                            issues("JRW-146"),
//            // D100
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF").
//                            image(3008, 2000, 3, 8, "7b376e9dd911ab94e0d0a6e20123c582").
//                            thumbnail(160, 120).
//                            issues("JRW-148"),
//            // D200
//            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF").
//                            image(3872, 2592, 3, 8, "05845e10ae91f7870c51e98a28d4ad80").
//                            thumbnail(160, 120).
//                            thumbnail(3872, 2592),
//            // D200
//            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF").
//                            image(3872, 2592, 3, 8, "6775afb3b9401950a7afe301497acd07").
//                            thumbnail(160, 120).
//                            thumbnail(3872, 2592)
          );
      }
  }
