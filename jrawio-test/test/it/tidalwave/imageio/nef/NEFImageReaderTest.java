/*******************************************************************************
 * 
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 * 
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 * 
 *******************************************************************************
 * 
 * $Id: NEFImageReaderTest.java,v 1.4 2006/02/14 22:19:24 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestCase;
import it.tidalwave.imageio.test.ImageReaderTest;
import it.tidalwave.imageio.test.util.FileFinder;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: NEFImageReaderTest.java,v 1.4 2006/02/14 22:19:24 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFImageReaderTest extends TestCase
  {
    public static Test suite() 
      {
        Collection list = FileFinder.findTestFiles(ImageReaderTest.testFolder, "NEF");
        list.addAll(FileFinder.findTestFiles(ImageReaderTest.testFolder, "NDF"));
        return ImageReaderTest.createTestSuite("NEF", list, NEFImageReader.class);
      }
  }
