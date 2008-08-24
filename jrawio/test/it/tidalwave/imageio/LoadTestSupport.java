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
 * $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.junit.Before;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class LoadTestSupport extends TestSupport
  {
    private String testFolder = "/home/fritz/Projects/Imaging/trunk/www/TestSets";
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    @Before
    public void setUp() 
      {
      }

    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected void assertImage (final ImageReader ir, final int width, final int height) 
      throws IOException 
      {
        assertEquals(width, ir.getWidth(0));
        assertEquals(height, ir.getHeight(0));
      }

    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected void assertThumbnail (final ImageReader ir, final int thumbnailIndex, final int width, final int height) 
      throws IOException 
      {
        assertEquals(width, ir.getThumbnailWidth(0, thumbnailIndex));
        assertEquals(height, ir.getThumbnailHeight(0, thumbnailIndex));
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected void assertLoadImage (final ImageReader ir, final int width, final int height) 
      throws IOException 
      {
        final BufferedImage image = ir.read(0);
        assertNotNull(image);
        assertEquals(width, image.getWidth());
        assertEquals(height, image.getHeight());
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected void assertLoadThumbnail (final ImageReader ir, final int thumbnailIndex, final int width, final int height) 
      throws IOException 
      {
        final BufferedImage thumbnail = ir.readThumbnail(0, thumbnailIndex);
        assertNotNull(thumbnail);
        assertEquals(width, thumbnail.getWidth());
        assertEquals(height, thumbnail.getHeight());
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected ImageReader getImageReader (final String path)
      throws IOException
      {
        final File file = new File(testFolder + "/" + path); 
        assertTrue("File not found: " + file, file.exists()); 
        final ImageReader imageReader = ImageIO.getImageReaders(file).next();
        assertNotNull(imageReader);
        imageReader.setInput(ImageIO.createImageInputStream(file));
        return imageReader;
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected void close (final ImageReader ir)
      throws IOException
      {
        final ImageInputStream iis = (ImageInputStream)ir.getInput(); 
        iis.close();
        ir.dispose();
      }
  }
