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
package it.tidalwave.imageio.rawprocessor.pef;

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
public class PEFProcessorTest extends NewImageReaderTestSupport
  {
    public PEFProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // K100D Super
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k100dsuper/RAW_PENTAX_K100DSUPER.PEF").
                            image(3008, 2008, 3, 8, "3a221a9c7a4fac3b9ee0d95d98dc91e7").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k100dsuper/RAW_PENTAX_K100DSUPER.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2024, 3, 16, "da955411f632b53b0cb6c51014916766").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            // K10D - FIXME: should be moved to DNG
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.DNG").
                            image(3872, 2592, 3, 8, "c9753f470fa8d87086250e50a07c9e0b").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-203"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.DNG").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3936, 2624, 3, 16, "c9753f470fa8d87086250e50a07c9e0b").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-203"),
            // K10D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.PEF").
                            image(3872, 2592, 3, 8, "9dea3a21d70774aba56e84167f6c2bc7").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3936, 2624, 3, 16, "e64482dbeb55462b8511643ed22cb45f").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            // K100D
            ExpectedResults.create("http://raw.fotosite.pl/download-Pentax_K100D_Pentax_DA_12-24f4/IMGP7119.PEF").
                            image(3008, 2008, 3, 8, "707ce3098d8cc2f7a001f4f4e7531457").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            thumbnail(640, 480).
                            issues("JRW-259"),
            ExpectedResults.create("http://raw.fotosite.pl/download-Pentax_K100D_Pentax_DA_12-24f4/IMGP7119.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2024, 3, 16, "4268e361a4d94bdd88c2d57bcc0366a1").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            thumbnail(640, 480).
                            issues("JRW-259"),
            // K200D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k200d/RAW_PENTAX_K200D.PEF").
                            image(3872, 2592, 3, 8, "41f65a7b77f3bdc60c775dad7646eb6a").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k200d/RAW_PENTAX_K200D.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3936, 2624, 3, 16, "2895ac93979706d029991443e898376d").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            // K20D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k20d/RAW_PENTAX_K20D.PEF").
                            image(4672, 3104, 3, 8, "722b817c92d5945eb2f38c4bb696700d").
                            thumbnail(160, 120).
                            thumbnail(4672, 3104).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k20d/RAW_PENTAX_K20D.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(4864, 3136, 3, 16, "863b104f6dc7b9bb80630a34a2d4c326").
                            thumbnail(160, 120).
                            thumbnail(4672, 3104).
                            thumbnail(640, 480).
                            issues("JRW-203", "JRW-240"),
            // *istD
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/star_d/RAW_PENTAX_STARISTD_SRGB.PEF").
                            image(3008, 2008, 3, 8, "1da5a949a787c9819b64495778e2c592").
                            thumbnail(160, 120).
                            thumbnail(3008, 2008).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/star_d/RAW_PENTAX_STARISTD_SRGB.PEF").
                            param(new RAWImageReadParam(Source.RAW_IMAGE)).
                            image(3040, 2024, 3, 16, "0f658d7c5754819b21b6dc2cef19de67").
                            thumbnail(160, 120).
                            thumbnail(3008, 2008).
                            thumbnail(640, 480).
                            issues("JRW-203")
          );
      }
  }

