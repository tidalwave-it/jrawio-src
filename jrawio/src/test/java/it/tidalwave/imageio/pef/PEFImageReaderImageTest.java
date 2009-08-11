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
package it.tidalwave.imageio.pef;

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
public class PEFImageReaderImageTest extends NewImageReaderTestSupport
  {
    public PEFImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
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
                            image(3040, 2024, 3, 16, "da955411f632b53b0cb6c51014916766").
                            thumbnail(160, 120).
                            thumbnail(3008, 2000).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            // K10D - FIXME: should be moved to DNG
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.DNG").
                            image(3936, 2624, 3, 16, "c9753f470fa8d87086250e50a07c9e0b").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            issues("JRW-203"),
            // K10D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k10d/RAW_PENTAX_K10D_SRGB.PEF").
                            image(3936, 2624, 3, 16, "5c2b928cc582d022759b2a60b7730fdb").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            // K200D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k200d/RAW_PENTAX_K200D.PEF").
                            image(3936, 2624, 3, 16, "fce5ba4092b032c01a2c8d72f00156fb").
                            thumbnail(160, 120).
                            thumbnail(3872, 2592).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            // K20D
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/k20d/RAW_PENTAX_K20D.PEF").
                            image(4672, 3104, 3, 16, "df3ccf84b3af424a10e3594436317656").
                            thumbnail(160, 120).
                            thumbnail(4672, 3104).
                            thumbnail(640, 480).
                            issues("JRW-203"),
            // *istD
            ExpectedResults.create("http://www.rawsamples.ch/raws/pentax/star_d/RAW_PENTAX_STARISTD_SRGB.PEF").
                            image(3040, 2024, 3, 16, "0f658d7c5754819b21b6dc2cef19de67").
                            thumbnail(160, 120).
                            thumbnail(3008, 2008).
                            thumbnail(640, 480).
                            issues("JRW-203")
          );
      }
  }
