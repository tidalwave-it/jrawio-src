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
package it.tidalwave.imageio.rawprocessor.arw;

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
public class ARWProcessorTest extends NewImageReaderTestSupport
  {
    public ARWProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW").
//                            image(3880, 2608, 3, 8, "8ad77dee2cbec75f462b5b19b9e8fe76").
                            image(3872, 2592, 3, 8, "0fadabd72e76c535e39216d68a55d4b9").
                            thumbnail(640, 480).
                            thumbnail(160, 120).
                            issues("JRW-127", "JRW-198", "JRW-209")
          );
      }
  }
