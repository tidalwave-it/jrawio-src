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
            // D2X v1.0.1
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2X/1.01/_DSC0733.NEF").
                            image(4288, 2848, 3, 8, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D2Xs v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef").
                            image(4288, 2848, 3, 8, "37d5aa7aab4e2d4fd667efb674f558ed").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D40 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF").
                            image(3008, 2000, 3, 8, "3b565723bb5b3c7db33fc2e69cca040c").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D50 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF").
                            image(3008, 2000, 3, 8, "067d8ab1983f4e8801f13046fe426baf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D50
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF").
                            image(2000, 3008, 3, 8, "328f3bdabe9ba80a3e4abe7237fa4084").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            issues("JRW-129"),
            // D70 v1.0.1
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF").
                            image(3008, 2000, 3, 8, "f5d7d487f69fee3ef0b6b344729cf2cf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v1.0.2
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF").
                            image(3008, 2000, 3, 8, "e6f4da41f81d0565509b84d89f0d678e").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v2.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF").
                            image(3008, 2000, 3, 8, "4cd1e5dc546289ced51b97996a04893f").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70s
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF").
                            image(2000, 3008, 3, 8, "d62309eab197bb9aced6a56e5fa966a9").
                            thumbnail(106, 160).
                            thumbnail(384, 255).
                            issues("JRW-149", "JRW-150"),
            // D90
            ExpectedResults.create("http://jalbum.net/download/DSC_0067.NEF").
                            image(4288, 2848, 3, 8, "a23d77ce9ad5e6a666407c4230ce49e9").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-187"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
                            image(2000, 3008, 3, 8, "6e7c4edc6a5389ab6f0887ee5a6f7527").
                            thumbnail(120, 160).
                            issues("JRW-146"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF").
                            image(3008, 2000, 3, 8, "7b376e9dd911ab94e0d0a6e20123c582").
                            thumbnail(160, 120).
                            issues("JRW-148"),
            // D200
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF").
                            image(3872, 2592, 3, 8, "fabdb471b9a972acf313ebe29bf338ea").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            // D200
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF").
                            image(3872, 2592, 3, 8, "eca27d2b7ade85c4803f287b8c413844").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592)
          );
      }
  }
