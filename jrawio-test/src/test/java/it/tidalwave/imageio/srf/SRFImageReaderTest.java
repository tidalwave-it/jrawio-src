/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: SRFImageReaderTest.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import junit.framework.Test;
import junit.framework.TestCase;
import it.tidalwave.imageio.test.ImageReaderTest;
import it.tidalwave.imageio.test.util.FileFinder;

/*******************************************************************************
 *
 * @author  fritz
 * @version $Id: SRFImageReaderTest.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class SRFImageReaderTest extends TestCase
  {
    public static Test suite() 
      {
        return ImageReaderTest.createTestSuite("SRF", FileFinder.findTestFiles(ImageReaderTest.testFolder, "SRF"), SRFImageReader.class);
      }
  }
