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
package it.tidalwave.imageio.raf;

import javax.annotation.Nonnull;
import java.util.Collection;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFImageReaderImageTest extends NewImageReaderTestSupport
  {
    public RAFImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // S6500fd
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/tmlehto/Fujifilm/S6500fd/RAF/DSCF6315.RAF").
                            image(3592, 3591, 3, 16, "8c256e68fe9897a4fac12a06f1a07fb4").
                            thumbnail(160, 120).
                            thumbnail(1600, 1200).
                            issues("JRW-204").
                            metadata("metadata.fujiRawData.JPEGImageOffset", 148).
                            metadata("metadata.fujiRawData.JPEGImageLength", 650086).
                            metadata("metadata.fujiRawData.table1Offset", 650246).
                            metadata("metadata.fujiRawData.table1Length", 4490).
                            metadata("metadata.fujiRawData.CFAOffset", 654736).
                            metadata("metadata.fujiRawData.CFALength", 13043712).
                            metadata("metadata.fujiRawData.unused1", 0).
                            metadata("metadata.fujiRawData.unused2", 13043712).
                            metadata("metadata.fujiRawData.unused3", 0).
                            metadata("metadata.fujiRawData.unused4", 0).
                            metadata("metadata.fujiRawData.unused5", 0).
                            metadata("metadata.fujiRawData.unused6", 0).
                            metadata("metadata.fujiRawData.unused7", 0).
                            metadata("metadata.fujiRawData.unused8", 0).
                            metadata("metadata.fujiRawData.unused9", 0).
                            metadata("metadata.fujiRawData.unused10", 0).
                            metadata("metadata.fujiRawData.fujiTable1.width", 4096).
                            metadata("metadata.fujiRawData.fujiTable1.height", 1544).
                            metadata("metadata.fujiRawData.fujiTable1.rawWidth", 4224).
                            metadata("metadata.fujiRawData.fujiTable1.rawHeight", 1544).
                            metadata("metadata.fujiRawData.fujiTable1.fujiLayout", false).
                            metadata("metadata.fujiRawData.fujiTable1.coefficients", new short[]{336, 518, 336, 489}).

                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final RAFMetadata metadata = (RAFMetadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                    final FujiRawData fujiRawData = metadata.getFujiRawData();
                            //        assertEquals("FUJIFILMCCD-RAW 0201FF389701FinePix S6500fd", fujiRawData.getHeader());
                            //        assertEquals("", fujiRawData.getB1());
                            //        assertEquals("0100", fujiRawData.getVersion());
                            //        assertEquals("", fujiRawData.getB2());                            
                                  }
                              })
          );
      }
  }
