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
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RunWith(value=Parameterized.class)
public class NEFImageReaderImageTest extends NewImageReaderTestSupport
  {
    public NEFImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
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
                            image(4320, 2868, 3, 16, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D2Xs v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef").
                            image(4320, 2868, 3, 16, "37d5aa7aab4e2d4fd667efb674f558ed").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D40 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF").
                            image(3040, 2014, 3, 16, "3b565723bb5b3c7db33fc2e69cca040c").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D50 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF").
                            image(3040, 2014, 3, 16, "067d8ab1983f4e8801f13046fe426baf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D50
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF").
                            image(3040, 2014, 3, 16, "8fcbf7e059735e80e0bc2f1211794221").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            issues("JRW-129"),
            // D70 v1.0.1
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF").
                            image(3040, 2014, 3, 16, "f5d7d487f69fee3ef0b6b344729cf2cf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v1.0.2
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF").
                            image(3040, 2014, 3, 16, "e6f4da41f81d0565509b84d89f0d678e").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v2.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF").
                            image(3040, 2014, 3, 16, "4cd1e5dc546289ced51b97996a04893f").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70s
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF").
                            image(3040, 2014, 3, 16, "fa9bbd9ebe4b5f652c385c84ce33fd56").
                            thumbnail(106, 160).
                            thumbnail(384, 255).
                            issues("JRW-149", "JRW-150"),
            // D90
            ExpectedResults.create("http://jalbum.net/download/DSC_0067.NEF").
                            image(4352, 2868, 3, 16, "d4e48694534da435072e93f04244a37b").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-187"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
                            image(3034, 2024, 3, 16, "3659664029723dc8ea29b09a923fca7d").
                            thumbnail(120, 160).
                            issues("JRW-146"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF").
                            image(3008, 2000, 3, 16, "03383e837402452f7dc553422299f057").
                            thumbnail(160, 120).
                            issues("JRW-148"),
            // D200
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF").
                            image(3904, 2616, 3, 16, "fabdb471b9a972acf313ebe29bf338ea").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            // D200
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF").
                            image(3904, 2616, 3, 16, "eca27d2b7ade85c4803f287b8c413844").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592)
          );
      }
  }