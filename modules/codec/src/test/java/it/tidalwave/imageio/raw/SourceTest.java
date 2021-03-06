/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import org.junit.Test;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SourceTest
  {
    @Test
    public void testGetDefault1()
      {
        assertEquals(Source.PROCESSED_IMAGE, Source.getDefault());
      }

    @Test
    public void testGetDefault2()
      {
        System.setProperty(Source.PROP_DEFAULT_SOURCE, Source.DEFAULT_SOURCE_PROCESSED_IMAGE);
        assertEquals(Source.PROCESSED_IMAGE, Source.getDefault());
      }

    @Test
    public void testGetDefault3()
      {
        System.setProperty(Source.PROP_DEFAULT_SOURCE, Source.DEFAULT_SOURCE_FULL_SIZE_PREVIEW);
        assertEquals(Source.FULL_SIZE_PREVIEW, Source.getDefault());
      }

    @Test
    public void testGetDefault4()
      {
        System.setProperty(Source.PROP_DEFAULT_SOURCE, Source.DEFAULT_SOURCE_RAW_IMAGE);
        assertEquals(Source.RAW_IMAGE, Source.getDefault());
      }

    @Test(expected=IllegalArgumentException.class)
    public void testGetDefault5()
      {
        System.setProperty(Source.PROP_DEFAULT_SOURCE, "foobar");
        Source.getDefault();
      }
  }