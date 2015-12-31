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
package it.tidalwave.imageio.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import it.tidalwave.imageio.util.Logger;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import it.tidalwave.imageio.test.util.PixelSpeed;

/**
 *
 * @author fritz
 */
public class ImageReaderTest extends TestCase
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.test.ImageReaderTest");
    
    private final static boolean TEST_ONLY_KNOWN_FAILURES = false;
    
    private File testFile;
    
    private String format;
    
    private Class readerClass;
    
    private String fileName;
    
    private ImageReader imageReader;
    
    private ImageInputStream is;
    
    private static TestContext c = new TestContext();
    
    public static File testFolder;
    
    //   private MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    static
      {
        testFolder = c.testFolder;
      }

    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public ImageReaderTest (File testFile, String format, Class readerClass)
      {
        this.testFile = testFile;
        this.format = format;
        this.readerClass = readerClass;
        fileName = testFile.getAbsolutePath();
        fileName = fileName.substring(c.testFolder.getAbsolutePath().length());
      }
    
    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void setUp() throws IOException
      {
        Iterator i = ImageIO.getImageReaders(testFile);
        
        if (i.hasNext())
          {
            imageReader = (ImageReader)i.next();
            is = ImageIO.createImageInputStream(testFile);
            imageReader.setInput(is);
          }
      }
    
    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void tearDown () throws IOException
      {
        if (imageReader != null)
          {
            imageReader.reset();
            imageReader = null;
          }
        
        if (is != null)
          {
            is.close();
            is = null;
          }
      }
    
    public void testImageReader()
      {
        assertNotNull("No imagereader for " + testFile, imageReader);
        assertEquals("Bad ImageReader: " + imageReader.getClass(), imageReader.getClass(), readerClass);
      }
    
    public void testGetImageReadersByImageInputStream() throws IOException
      {
        ImageInputStream iis2 = ImageIO.createImageInputStream(testFile); 
        Iterator i = ImageIO.getImageReaders(iis2);
        assertTrue(i.hasNext());
        ImageReader imageReader2 = (ImageReader)i.next();
        assertEquals("Bad ImageReader: " + imageReader2.getClass(), imageReader2.getClass(), readerClass);
        imageReader2.reset();
        iis2.close();
      }
/*    
    public void testGetImageReadersByInputStream() throws IOException
      {
        FileInputStream fis2 = new FileInputStream(testFile);
        Iterator i = ImageIO.getImageReaders(fis2);
        assertTrue(i.hasNext());
        ImageReader imageReader2 = (ImageReader)i.next();
        assertEquals("Bad ImageReader: " + imageReader2.getClass(), imageReader2.getClass(), readerClass);
        imageReader2.reset();
        fis2.close();
      }
*/    
    public void testGetExtension()
      {
        List list = Arrays.asList(ImageIO.getReaderFormatNames());   
        assertTrue("Missing lowercase format name", list.contains(format.toLowerCase()));
        assertTrue("Missing uppercase format name", list.contains(format.toUpperCase()));
      }
 
    public void testGetNumImages() throws IOException
      {
        assertEquals("getNumImages() != 1", imageReader.getNumImages(true), 1);    
      }
    
    public void testGetWidth() throws IOException
      {
        imageReader.getWidth(0);
      }
    
    public void testGetHeight() throws IOException
      {
        imageReader.getHeight(0);
      }
    
    public void testGetNumThumbnails() throws IOException
      {
        c.propertyTracker.updateProperty(testFile, "thumbnailCount", new Integer(imageReader.getNumThumbnails(0)));       
      }
    
    public void testGetThumbnailWidth() throws IOException
      {
        int thumbnailCount = imageReader.getNumThumbnails(0);
        
        for (int t = 0; t < thumbnailCount; t++)
          {
            imageReader.getThumbnailWidth(0, t);        
          }
      }
    
    public void testGetThumbnailHeight() throws IOException
      {
        int thumbnailCount = imageReader.getNumThumbnails(0);
        
        for (int t = 0; t < thumbnailCount; t++)
          {
            imageReader.getThumbnailHeight(0, t);        
          }
      }
    
    public void testReadThumbnail() throws IOException
      {
        int thumbnailCount = imageReader.getNumThumbnails(0);
        
        for (int t = 0; t < thumbnailCount; t++)
          {
            BufferedImage thumbnail = imageReader.readThumbnail(0, t);
            c.propertyTracker.updateProperty(testFile, "thumbnail." + t + ".width", new Integer(thumbnail.getWidth()));        
            c.propertyTracker.updateProperty(testFile, "thumbnail." + t + ".height", new Integer(thumbnail.getHeight()));        
          }
      }
    
    public void testGetThumbnailWidthIsCorrect() throws IOException
      {
        int thumbnailCount = imageReader.getNumThumbnails(0);
        
        for (int t = 0; t < thumbnailCount; t++)
          {
            c.propertyTracker.updateProperty(testFile, "thumbnail." + t + ".width", new Integer(imageReader.getThumbnailWidth(0, t)));        
          }
      }
    
    public void testGetThumbnailHeightIsCorrect() throws IOException
      {
        int thumbnailCount = imageReader.getNumThumbnails(0);
        
        for (int t = 0; t < thumbnailCount; t++)
          {
            c.propertyTracker.updateProperty(testFile, "thumbnail." + t + ".height", new Integer(imageReader.getThumbnailHeight(0, t)));        
          }
      }
    
    public void testRead() throws IOException
      {
//            Iterator j = ImageIO.getImageReaders(testFile);
//            TestCase.assertTrue(j.hasNext());
//            TestCase.assertEquals(imageReader.getClass(), j.next().getClass());
            
        long time = System.currentTimeMillis();
        BufferedImage image = imageReader.read(0);
        time = System.currentTimeMillis() - time;
        int width = imageReader.getWidth(0);
        int height = imageReader.getHeight(0);
        c.propertyTracker.updateProperty(testFile, "width", new Integer(image.getWidth()));
        c.propertyTracker.updateProperty(testFile, "height", new Integer(image.getHeight()));

        PixelSpeed pixelSpeed = (PixelSpeed)c.speedMapByFormat.get(format);

        if (pixelSpeed == null)
          {
            pixelSpeed = new PixelSpeed();
            c.speedMapByFormat.put(format, pixelSpeed);
          }
        
        pixelSpeed.addToStatistics(width * height, time);
        c.propertyTracker.setProperty(testFile, c.dateTag + "." + c.computerTag + "." + c.osTag + ".ELAPSED", new Long(time));
      }

    public void testGetWidthIsCorrect() throws IOException
      {
        c.propertyTracker.updateProperty(testFile, "width", new Integer(imageReader.getWidth(0)));
      }
    
    public void testGetHeightIsCorrect() throws IOException
      {
        c.propertyTracker.updateProperty(testFile, "height", new Integer(imageReader.getHeight(0)));
      }
    
    public void testGetImageMetadata() throws IOException
      {
        IIOMetadata metadata = imageReader.getImageMetadata(0);
        String[] names = metadata.getMetadataFormatNames();

        for (int m = 0; m < names.length; m++)
          {
            Node node = metadata.getAsTree(names[m]);
            process(testFile, names[m], node);
          }
      }
    
    public void testJRW105() throws IOException
      {
        imageReader.getImageMetadata(0);
        imageReader.setInput(is);
        imageReader.read(0);
      }
    
    interface Runnable2
      {
        public void run (ImageReader ir) throws IOException;  
      }
    
    public void testGetNumImagesWithNoInput() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getNumImages(true);    
              }
          });
      }
    
    public void testGetWidthWithNoInput() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getWidth(0);
              }
          });
      }
    
    public void testGetHeightWithNoInput() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getHeight(0);
              }
          });
      }
    
    public void testGetNumThumbnailsWithNoInput() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getNumThumbnails(0);       
              }
          });
      }
    
/*    public void testGetThumbnailWidthWithNoInput() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getThumbnailWidth(0, t);        
              }
          });
      }
    
    public void testGetThumbnailHeight() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getThumbnailHeight(0, 0);        
              }
          });
      }
    
    public void testReadThumbnail() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.readThumbnail(0, 0);
              }
          });
      }
    
    public void testGetThumbnailWidthIsCorrect() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getThumbnailWidth(0, 0);        
              }
          });
      }
    
    public void testGetThumbnailHeightIsCorrect() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
              }
          });
        for (int t = 0; t < 10; t++)
          {
            imageReader.getThumbnailHeight(0, t);        
          }
      }
    
    public void testRead() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.read(0);
              }
          });
      }

    public void testGetWidthIsCorrect() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getWidth(0);
              }
          });
      }
    
    public void testGetHeightIsCorrect() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getHeight(0);
              }
          });
      }
    
    public void testGetImageMetadata() throws IOException
      {
        _testIllegalState(new Runnable2()
          { 
            public void run (ImageReader imageReader) throws IOException
              {
                imageReader.getImageMetadata(0);
              }
          });
      }*/
    
    private void _testIllegalState (Runnable2 runnable)
      {
        Iterator i = ImageIO.getImageReadersByFormatName(format);
        final ImageReader ir = (ImageReader)i.next();
        
        try
          {
            runnable.run(ir);
          }
        
        catch (IllegalStateException e)
          {
            return;        
          }
        
        catch (Throwable e)
          {
          }

        assertFalse("Did not throw IllegalStateException", true);
      }
    
    /*******************************************************************************************************************
     * 
     * @param file
     * @param node
     * 
     *******************************************************************************/
    public void process (File file,
                         String pfx,
                         Node node)
      {
        Node ifdNode = node.getFirstChild();

        while (ifdNode != null)
          {
            if (ifdNode.getNodeName().equals("TIFFIFD"))
              {
                NamedNodeMap map = ifdNode.getAttributes();
                String name = getValue(map.getNamedItem("name"));
                processIFD(file, pfx + "." + name, ifdNode);
              }

            ifdNode = ifdNode.getNextSibling();
          }
      }

    /*******************************************************************************************************************
     * 
     * @param file
     * @param pfx
     * @param node
     * 
     *******************************************************************************/
    private void processIFD (File file,
                             String pfx,
                             Node node)
      {
        Node fieldNode = node.getFirstChild();

        while (fieldNode != null)
          {
            if (fieldNode.getNodeName().equals("TIFFField"))
              {
                processField(file, pfx, fieldNode);
              }

            fieldNode = fieldNode.getNextSibling();
          }

        Node subIfdNode = node.getFirstChild();

        while (subIfdNode != null)
          {
            if (subIfdNode.getNodeName().equals("TIFFIFD"))
              {
                NamedNodeMap map = subIfdNode.getAttributes();
                String name = getValue(map.getNamedItem("name"));
                processIFD(file, pfx + "." + name, subIfdNode);
              }

            subIfdNode = subIfdNode.getNextSibling();
          }
      }

    /*******************************************************************************************************************
     * 
     * @param pfx
     * @param node
     * 
     *******************************************************************************/
    private void processField (File file,
                               String pfx,
                               Node node)
      {
        NamedNodeMap map = node.getAttributes();
        String tagNumber = getValue(map.getNamedItem("number"));
        String tagName = getValue(map.getNamedItem("name"));
        String ttt = ".tag_" + tagNumber;
        c.propertyTracker.updateProperty(file, pfx + ttt + ".name", tagName);

        Node typeNode = node.getFirstChild();

        if (typeNode != null)
          {
            String nodeName = typeNode.getNodeName();
            int j = nodeName.indexOf("TIFF");
            
            if (j >= 0)
              {
                nodeName = nodeName.substring(0, j) + nodeName.substring(j + 4);
              }
            
            c.propertyTracker.updateProperty(file, pfx + ttt + ".type", nodeName);

            Node valueNode = typeNode.getFirstChild();
            int i = 0;

            while (valueNode != null)
              {
                map = valueNode.getAttributes();
                String value = getValue(map.getNamedItem("value"));
                c.propertyTracker.updateProperty(file, pfx + ttt + ".value" + i++, value);
                valueNode = valueNode.getNextSibling();
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * @param node
     * @return
     * 
     *******************************************************************************/
    private static String getValue (Node node)
      {
        return (node != null) ? node.getNodeValue() : null;
      }
    
    public void runTest() throws Throwable
      {
        String string = getName() + "(" + fileName + ")";
        
        if (TEST_ONLY_KNOWN_FAILURES ^ c.failure.isFailedTest(getName(), fileName))
          {
            System.err.println(string + " -- skipping");  
          }
        
        else
          {
            try
              {
                super.runTest();    
                System.err.println(string + " -- passed");
                c.failure.addPassedTest(getName(), fileName);
              } 
        
            catch (Throwable t)
              {
                System.err.println(string + " *** FAILED");
                c.failure.addFailedTest(getName(), fileName);
                logger.throwing("", "", t);
                throw t;
              } 
          }
      }


    public static TestSuite createTestSuite (String format, Collection collection, Class imageReaderClass)
      {
        TestSuite suite = new TestSuite();
        
        for (Iterator i = collection.iterator(); i.hasNext(); )
          {
            File file = (File)i.next();
            Method[] methods = ImageReaderTest.class.getMethods();

            for (int j = 0; j < methods.length; j++)
              {
                String methodName = methods[j].getName();
                
                if (methodName.startsWith("test"))
                  {
                    TestCase testCase = new ImageReaderTest(file, format, imageReaderClass);
                    testCase.setName(methodName);
                    suite.addTest(testCase);  
                  }
              }
          }

        return suite;
      }
    
  }
