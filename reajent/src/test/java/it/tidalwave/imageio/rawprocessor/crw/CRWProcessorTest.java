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
            // EOS 300D
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/300d/RAW_CANON_300D.CRW").
                            image(3072, 2048, 3, 8, "d9e8cb87977403cf0c04e32dc0bdfec9").
                            thumbnail(3072, 2048).
                            issues("JRW-234"),
            // EOS D30
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d30/RAW_CANON_D30_SRGB.CRW").
                            image(2160, 1440, 3, 8, "6df92e1d4589fb9fb2b353aaa9efc83b").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            // EOS D60
            ExpectedResults.create("http://www.rawsamples.ch/raws/canon/d60/RAW_CANON_D60_ARGB.CRW").
                            image(2224, 1456, 3, 8, "b499a77c82e5289d043e2f330c6fffba").
                            thumbnail(1440, 960).
                            issues("JRW-234"),
            // EOS300D
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
                            image(3072, 2048, 3, 8, "67994f630322d2bede87a2884f5f0965").
                            thumbnail(2048, 1360).
                            issues("JRW-104", "JRW-218", "JRW-219")
          );
      }
  }
