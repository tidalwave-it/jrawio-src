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
import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;
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
            // D1
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d1/RAW_NIKON_D1.NEF").
                            image(2000, 1312, 3, 8, "f62836d70fab86475a155178f18cd1aa").
                            thumbnail(160, 120),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d1/RAW_NIKON_D1.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(2012, 1324, 3, 16, "69c3916e9a583f7e48ca3918d31db135").
                            thumbnail(160, 120),
            // D1x
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d1x/RAW_NIKON_D1X.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4028, 1324, 3, 16, "d3d3b27908bc6f9ed97d1f68c9d7a4af").
                            thumbnail(160, 120),
            // D2X v1.0.1
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2X/1.01/_DSC0733.NEF").
                            image(4288, 2848, 3, 8, "f0c8a971dc82b31f1de739d7fb3d8a10").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2X/1.01/_DSC0733.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4320, 2868, 3, 16, "0cb29a0834bb2293ee4bf0c09b201631").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D2Xs v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef").
                            image(4288, 2848, 3, 8, "f7d94c239bcc9bf2c8fd1b180049b401").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-215"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D2Xs/1.00/DSC_1234.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4320, 2868, 3, 16, "37d5aa7aab4e2d4fd667efb674f558ed").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848),
            // D3
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3/RAW_NIKON_D3.NEF").
                            image(4256, 2832, 3, 8, "185f9482ee52c259201e0ab3e144d36c").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3/RAW_NIKON_D3.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "fadead8af5aefe88b4ca8730cfb7392c").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3/RAW_NIKON_D3.NEF").
                            param(new RAWImageReadParam(Source.FULL_SIZE_PREVIEW)).
                            image(4256, 2832, 3, 8, "a09bd596f4f4cb0a55e923871ef3cb7d").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832),
            // D3x
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3x/RAW_NIKON_D3X.NEF").
                            image(4032, 6048, 3, 8, "cc3c6dc29cc167453186b248754be1d9").
                            thumbnail(160, 120).
                            thumbnail(6048, 4032).
                            issues("JRW-221"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3x/RAW_NIKON_D3X.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(6080, 4044, 3, 16, "2a0cfc36cea7c3346b8d39355bf786e6").
                            thumbnail(160, 120).
                            thumbnail(6048, 4032).
                            issues("JRW-221"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d3x/RAW_NIKON_D3X.NEF").
                            param(new RAWImageReadParam(Source.FULL_SIZE_PREVIEW)).
                            image(4032, 6048, 3, 8, "ac88f2274c75c37f293140e78a9d1030").
                            thumbnail(160, 120).
                            thumbnail(6048, 4032).
                            issues(),
            // D40 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF").
                            image(2000, 3008, 3, 8, "51f5ec81eff835ac17061b050f247fe5").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            issues("JRW-215"),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D40/1.00/DSC_0108.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "3b565723bb5b3c7db33fc2e69cca040c").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D40x
            ExpectedResults.create("http://raw.fotosite.pl/download-Nikon_D40X_Nikkor_18-135_f3.5-5.6_AFS/DSC_0001.NEF").
                            image(3872, 2592, 3, 8, "0c1eacfa819622783264372248618df3").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-256"),
            ExpectedResults.create("http://raw.fotosite.pl/download-Nikon_D40X_Nikkor_18-135_f3.5-5.6_AFS/DSC_0001.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "b28044d4f1405f1718332a1253aec8a5").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-256"),
            // D50 v1.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF").
                            image(3008, 2000, 3, 8, "5b785ffd3a34430c58e56ea5b204d7d3").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D50/1.00/DSC_0015.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "067d8ab1983f4e8801f13046fe426baf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D50
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF").
                            image(2000, 3008, 3, 8, "3a5207518665f8a39abfbee7d5b39a1b").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            issues("JRW-129"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/konstantinmaslov/Nikon/D50/NEF/France_Collioure_1.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "8fcbf7e059735e80e0bc2f1211794221").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            issues("JRW-129"),
            // D60 edited with Nikon software data
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d60/RAW_NIKON_D60.NEF").
                            image(3872, 2592, 3, 8, "806c0b0b99683fc08f80cf70229dffd0").
                            thumbnail(320, 214).
                            thumbnail(3872, 2592).
                            issues("JRW-224", "JRW-276", "JRW-277", "JRW-278"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d60/RAW_NIKON_D60.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "1bb9b055b9504ea67993d813995efecf").
                            thumbnail(320, 214).
                            thumbnail(3872, 2592).
                            issues("JRW-224", "JRW-277", "JRW-278"),
            // D70 v1.0.1
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF").
                            image(3008, 2000, 3, 8, "72f0034d835dbbc0afe9d65255de9423").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.01/DSC_2544.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "f5d7d487f69fee3ef0b6b344729cf2cf").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v1.0.2
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF").
                            image(2000, 3008, 3, 8, "2a431f6635b9f51b33aab9854a3c5dac").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/1.02/DSC_1945.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "e6f4da41f81d0565509b84d89f0d678e").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70 v2.0.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF").
                            image(3008, 2000, 3, 8, "7cdf5a744af7bf39d6988dfc29f5bb17").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D70/2.00/_DSC4798.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "4cd1e5dc546289ced51b97996a04893f").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000),
            // D70s
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF").
                            image(2000, 3008, 3, 8, "5f72336ecb735433fb379fcc830b6ae6").
                            thumbnail(106, 160).
                            thumbnail(384, 255).
                            issues("JRW-149", "JRW-150"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/mpetersen/Nikon/D70s/NEF/Nikon_D70s_0001.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2014, 3, 16, "fa9bbd9ebe4b5f652c385c84ce33fd56").
                            thumbnail(106, 160).
                            thumbnail(384, 255).
                            issues("JRW-149", "JRW-150"),
            // D80
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d80/RAW_NIKON_D80_SRGB.NEF").
                            image(2592, 3872, 3, 8, "9a721f53a9c38f5c4b6c7cf610535151").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d80/RAW_NIKON_D80_SRGB.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "856a729b61cdcdb75817b58b3a6d3fc3").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            // D90
            ExpectedResults.create("http://jalbum.net/download/DSC_0067.NEF").
                            image(4288, 2848, 3, 8, "f2bced57381390ff015bf0fe671fdeab").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-187", "JRW-215"),
            ExpectedResults.create("http://jalbum.net/download/DSC_0067.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4352, 2868, 3, 16, "d4e48694534da435072e93f04244a37b").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-187"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
                            image(2000, 3008, 3, 8, "f9fab4ee10aa950bfa30426783ea3fda").
                            thumbnail(120, 160).
                            issues("JRW-146"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3034, 2024, 3, 16, "3659664029723dc8ea29b09a923fca7d").
                            thumbnail(120, 160).
                            issues("JRW-146"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/ccw90.nef").
                            param(new RAWImageReadParam(Source.FULL_SIZE_PREVIEW)).
                            imageException(IllegalArgumentException.class).
                            thumbnailException(IllegalArgumentException.class).
                            thumbnail(120, 160).
                            issues(),
            // D100 cropped with Nikon Capture Editor
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/20030920-0007-Cropped.nef").
                            image(1712, 1316, 3, 8, "e26754dc1627b690ef5e471656a6dbca").
                            thumbnail(160, 122).
                            issues("JRW-276", "JRW-279"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/NEF/NikonCaptureEditor/20030920-0007-Cropped.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3034, 2024, 3, 16, "aa086925ceec36e37f3824ac6861c4f1").
                            thumbnail(160, 122).
                            issues("JRW-276", "JRW-279"),
            // D100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF").
                            image(3008, 2000, 3, 8, "7b376e9dd911ab94e0d0a6e20123c582").
                            thumbnail(160, 120).
                            issues("JRW-148"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3008, 2000, 3, 16, "03383e837402452f7dc553422299f057").
                            thumbnail(160, 120).
                            issues("JRW-148"),
            // D200
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF").
                            image(3872, 2592, 3, 8, "05845e10ae91f7870c51e98a28d4ad80").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/fabriziogiudici/Nikon/D200/NEF/20080427-0029.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "fabdb471b9a972acf313ebe29bf338ea").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            // D200
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF").
                            image(3872, 2592, 3, 8, "6775afb3b9401950a7afe301497acd07").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/nikon/D200/1.00/DCC_0280.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "eca27d2b7ade85c4803f287b8c413844").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592),
            // D300
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d300/RAW_NIKON_D300.NEF").
                            image(4288, 2848, 3, 8, "8b433b9f5bc4e5546185a9f8a4c2397c").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-222"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d300/RAW_NIKON_D300.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4352, 2868, 3, 16, "13318aa72965d3807be5f1e5049c6264").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-222"),
            // D700
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d700/RAW_NIKON_D700.NEF").
                            image(2832, 4256, 3, 8, "cb6139515ee2e1e64716c83ccb374332").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832).
                            issues("JRW-223"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/nikon/d700/RAW_NIKON_D700.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "451322b803ec25d9386f0052ada7faae").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832).
                            issues("JRW-223"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/_DSF0297_1.NEF").
                            image(4256, 2832, 3, 8, "629a8c7353e001b89cdb9ac91c91cb3d").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832).
                            issues("JRW-275"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/_DSF0297_1.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "cc061ec5d17a09c77e693d4beb418832").
                            thumbnail(160, 120).
                            thumbnail(4256, 2832).
                            issues("JRW-275"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_2.NEF").
                            image(4256, 2832, 3, 8, "42c3b3172ca75d5ce31c1ce1253aca26").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275", "JRW-276"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_2.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "e4dcadc578c2dabe33b1c11f7e2e0497").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_3.NEF").
                            image(4256, 2832, 3, 8, "42c3b3172ca75d5ce31c1ce1253aca26").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275", "JRW-276", "JRW-277", "JRW-278"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_3.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "e4dcadc578c2dabe33b1c11f7e2e0497").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275", "JRW-277", "JRW-278"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_4.NEF").
                            image(4256, 2832, 3, 8, "42c3b3172ca75d5ce31c1ce1253aca26").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275", "JRW-277", "JRW-278"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_4.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "e4dcadc578c2dabe33b1c11f7e2e0497").
                            thumbnail(320, 212).
                            thumbnail(4256, 2832).
                            issues("JRW-275", "JRW-277", "JRW-278"),
            // D700 edited with Nikon software
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_2-Crop1.NEF").
                            image(956, 980, 3, 8, "c6513250271d0317b589424d6e4c3da2").
                            thumbnail(312, 320).
                            thumbnail(956, 980).
                            issues("JRW-276"),
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/others/jeromebernard/Nikon/D700/NEF/NikonNX2/_DSF0297_2-Crop1.NEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4288, 2844, 3, 16, "e4dcadc578c2dabe33b1c11f7e2e0497").
                            thumbnail(312, 320).
                            thumbnail(956, 980).
                            issues("JRW-276"),
            // D3000
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d3000/sample_images/nikon_d3000_02.nef").
                            image(3872, 2592, 3, 8, "ff17523a7823b9fd8124186e2e263b63").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-272"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d3000/sample_images/nikon_d3000_02.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "14461fd23f073d3277d8e1b3b139bb2d").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-272"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d3000/sample_images/nikon_d3000_04.nef").
                            image(3872, 2592, 3, 8, "01b483dd2b492943e361b1bc9f59272f").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-272"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d3000/sample_images/nikon_d3000_04.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3904, 2616, 3, 16, "8269404d3ac6c2e1e33d8c0a3f6f79b8").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-272"),
            // D5000
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d5000/sample_images/nikon_d5000_02.nef").
                            image(4288, 2848, 3, 8, "70d9edd7c5328337a6ea81a7875df629").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-273"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d5000/sample_images/nikon_d5000_02.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4352, 2868, 3, 16, "3c31d071636a22692cd083f8aaa6723a").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-273"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d5000/sample_images/nikon_d5000_03.nef").
                            image(2848, 4288, 3, 8, "96c49002bccef008395f503f4b74b8b9").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-273"),
            ExpectedResults.create("http://img.photographyblog.com/reviews/nikon_d5000/sample_images/nikon_d5000_03.nef").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4352, 2868, 3, 16, "8cdc202a71292fb222afb7ddc9ac050c").
                            thumbnail(160, 120).
                            thumbnail(4288, 2848).
                            issues("JRW-273")
         );
      }
  }

