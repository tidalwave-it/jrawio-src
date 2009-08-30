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
package it.tidalwave.imageio;

import java.util.logging.Level;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import it.tidalwave.imageio.util.Logger;
import java.awt.Dimension;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ImageReaderTestSupport extends TestSupport
  {
    private final static String CLASS = ImageReaderTestSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    public final static String PROP_TESTSET_CACHED_FOLDER = "testset.cached.folder";
    
    private static String testFolder;
        
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @BeforeClass
    public static void setupTestFolderPath() 
      {                
        testFolder = System.getProperty(PROP_TESTSET_CACHED_FOLDER);
        assertNotNull("You must set a property named '" + PROP_TESTSET_CACHED_FOLDER + "' " +
                      "to point to the test files", testFolder);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected void assertMIMETypes (final String extension, final String ... mimeTypes)
      {
        final Iterator<ImageReader> i = ImageIO.getImageReadersBySuffix(extension);
        assertTrue(i.hasNext());
        final ImageReader ir = i.next();
        assertNotNull(ir);
        assertFalse(i.hasNext());
        final ImageReaderSpi provider = ir.getOriginatingProvider();
        assertEquals(mimeTypes, provider.getMIMETypes());
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    protected void assertImage (final ImageReader ir, final int width, final int height) 
      throws IOException 
      {
        final Dimension imageSize = new Dimension(ir.getWidth(0), ir.getHeight(0));
        final Dimension expectedSize = new Dimension(width, height);
        assertEquals("metadata image size: ", expectedSize, imageSize);
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    protected void assertThumbnail (final ImageReader ir, final int thumbnailIndex, final int width, final int height) 
      throws IOException 
      {
        final Dimension thumbnailSize = new Dimension(ir.getThumbnailWidth(0, thumbnailIndex),
                                                      ir.getThumbnailHeight(0, thumbnailIndex));
        final Dimension expectedSize = new Dimension(width, height);
        assertEquals("metadata thumbnail size: ", expectedSize, thumbnailSize);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    @Deprecated
    protected BufferedImage assertLoadImage (final @Nonnull ImageReader ir,
                                             final @Nonnegative int width,
                                             final @Nonnegative int height,
                                             final @Nonnegative int bandCount,
                                             final @Nonnegative int sampleSize)
      throws IOException
      {
        final BufferedImage image = ir.read(0);
        assertNotNull(image);
        final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        final Dimension expectedSize = new Dimension(width, height);
        assertEquals("loaded image size: ", expectedSize, imageSize);
        assertEquals("loaded image band count", bandCount, image.getData().getNumBands());

        for (int i = 0; i < bandCount; i++)
          {
            assertEquals("loaded image sample size for band " + i, sampleSize, image.getData().getSampleModel().getSampleSize(i));
          }

        return image;
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage assertLoadImage (final @Nonnull ImageReader ir,
                                             final @Nonnegative int width,
                                             final @Nonnegative int height,
                                             final @Nonnegative int bandCount,
                                             final @Nonnegative int sampleSize,
                                             final int type)
      throws IOException 
      {
        final BufferedImage image = ir.read(0);
        assertNotNull(image);
        final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        final Dimension expectedSize = new Dimension(width, height);
        assertEquals("loaded image size: ", expectedSize, imageSize);
        assertEquals("loaded image band count", bandCount, image.getData().getNumBands());
        assertEquals(type, image.getType());

        for (int i = 0; i < bandCount; i++)
          {
            assertEquals("loaded image sample size for band " + i, sampleSize, image.getData().getSampleModel().getSampleSize(i));
          }
        
        return image;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage assertLoadThumbnail (final @Nonnull ImageReader ir,
                                                 final @Nonnegative int thumbnailIndex,
                                                 final @Nonnegative int width,
                                                 final @Nonnegative int height)
      throws IOException 
      {
        final BufferedImage thumbnail = ir.readThumbnail(0, thumbnailIndex);
        assertNotNull("loaded thumbnail is null", thumbnail);
        final Dimension thumbnailSize = new Dimension(thumbnail.getWidth(), thumbnail.getHeight());
        final Dimension expectedSize = new Dimension(width, height);
        assertEquals("loaded thumbnail size: ", expectedSize, thumbnailSize);
        return thumbnail;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected File getTestFile (final @Nonnull String path)
      throws IOException
      {
        File file = null;

        if (path.startsWith("http"))
          {
            final String tmp = System.getProperty("java.io.tmpdir");
            final File cacheFolder = new File(System.getProperty(PROP_TESTSET_CACHED_FOLDER, tmp + "/TestFolder"));
            file = new File(cacheFolder, path.replace("http://", "").
                                              replace("https://", "").
                                              replace(':', '_'));

            if (!file.exists())
              {
                // With Hudson, this could be executed by multiple processes at a time - processes, not threads, so
                // we can't use Java thread-based synchronization.
                final File lockFile = new File(file.getPath() + ".lck");
                boolean alreadyLocked = !lockFile.createNewFile();

                if (lockFile.lastModified() - System.currentTimeMillis() > 10 * 60 * 1000)
                  {
                    logger.info(">>>> stale lock file %s", lockFile.getAbsolutePath());
                    alreadyLocked = false;
                    // FIXME: unfortunately, both processes would do the same
                  }

                if (!alreadyLocked)
                  {
                    logger.info(">>>> downloading to %s...", file.getAbsolutePath());
                    file.getParentFile().mkdirs();
                    FileUtils.copyURLToFile(new URL(path), file);
                    lockFile.delete(); // TODO: consider using a try / finally
                  }
                else
                  {
                    final long time = System.currentTimeMillis();

                    while (lockFile.exists())
                      {
                        logger.info(">>>> waiting for the download of %s to complete...", file.getAbsolutePath());
                       
                        if (System.currentTimeMillis() - time > 10 * 60 * 1000)
                          {
                            fail("Timeout while waiting for another thread to download " + file.getAbsolutePath());
                          }

                        try
                          {
                            Thread.sleep(1000);
                          }
                        catch (InterruptedException e)
                          {
                          }
                      }
                  }
              }
            else
              {
                logger.info(">>>> using cached file %s...", file.getAbsolutePath());
              }
          }
        else
          {
            file = new File(path.startsWith("/") ? path : (testFolder + "/" + path));
          }

        assertTrue("File not found: " + file, file.exists());

        return file;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected ImageReader getImageReader (final @Nonnull String path)
      throws IOException
      {
        logger.info("************* TESTING FILE: %s", path);

        final File file = getTestFile(path);
        final ImageReader imageReader = ImageIO.getImageReaders(file).next();
        assertNotNull(imageReader);
        imageReader.setInput(ImageIO.createImageInputStream(file));
        return imageReader;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    protected void assertRaster (final BufferedImage image, final String path, final String expectedRasterMD5) 
      throws IOException, NoSuchAlgorithmException
      {
        final File targetDirectory = new File(System.getProperty("java.io.tmpdir") + "/jrawio-test");
        final File tiffFile = new File(targetDirectory, path.replace("https://", "").replace("http://", "") + ".tiff");
        tiffFile.getParentFile().mkdirs();
        logger.info("***************** Writing %s...", tiffFile.getAbsolutePath());
        ImageIO.write(image, "TIFF", tiffFile);
        
        final Raster raster = image.getData();

        if (Boolean.getBoolean("jrawio.dumpRasterAsText"))
          {
            final File textDumpFile = new File(tiffFile.getAbsolutePath() + "-" + System.getProperty("java.version") + ".txt");
            dumpRasterAsText(raster, textDumpFile);
          }

        final MessageDigest md5 = md5(raster);

        // Comparisons are broken with JDK 1.5.0, don't make tests fail under Hudson.
        // See http://jrawio.tidalwave.it/issues/browse/JRW-162
//        if (System.getProperty("java.version").contains("1.5.0"))
//          {
//            logger.warning("Not testing raster's MD5 on Java 5 because of JRW-162");
//          }
//        else
//          {
            assertEquals(expectedRasterMD5, asString(md5.digest()));
//          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static MessageDigest md5 (final @Nonnull Raster raster)
      throws NoSuchAlgorithmException
      {
        final MessageDigest md5 = MessageDigest.getInstance("MD5");

        for (int b = 0; b < raster.getNumBands(); b++)
          {
            for (int y = 0; y < raster.getHeight(); y++)
              {
                for (int x = 0; x < raster.getWidth(); x++)
                  {
                    final int sample = raster.getSample(x, y, b) & 0xffff;
                    md5.update((byte)((sample >>> 24) & 0xff));
                    md5.update((byte)((sample >>> 16) & 0xff));
                    md5.update((byte)((sample >>>  8) & 0xff));
                    md5.update((byte)((sample >>>  0) & 0xff));
                  }
              }
          }

        return md5;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static void dumpRasterAsText (final @Nonnull Raster raster,
                                         final @Nonnull File file)
      throws FileNotFoundException
      {
        file.getParentFile().mkdir();
        logger.info("***************** Writing %s...", file.getAbsolutePath());
        final PrintWriter pw = new PrintWriter(file);
        dumpRasterAsText(raster, pw);
        pw.close();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static void dumpRasterAsText (final @Nonnull Raster raster,
                                         final @Nonnull PrintWriter pw)
      {
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int bandCount = raster.getNumBands();
        logger.fine("Dumping raster %d x %d x %d", width, height, bandCount);

        for (int y = 0; y < height; y++)
          {
            for (int b = 0; b < bandCount; b++)
              {
                pw.printf("y=%04d b=%1d : ", y, b);

                for (int x = 0; x < width; x++)
                  {
                    final int sample = raster.getSample(x, y, b) & 0xffff;
                    pw.printf("%04x ", sample);
                  }

                pw.println();
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    protected void close (final ImageReader ir)
      throws IOException
      {
        final ImageInputStream iis = (ImageInputStream)ir.getInput(); 
        iis.close();
        ir.dispose();
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
//    private static void dump (final File file, final byte[] buffer)
//      throws IOException
//      {
//        final FileOutputStream fos = new FileOutputStream(file);
//        fos.write(buffer);
//        fos.close();
//      }
//
//    /*******************************************************************************************************************
//     *
//     *
//     ******************************************************************************************************************/
//    private static byte[] asBytes (final short[] buffer)
//      throws IOException
//      {
//        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        final DataOutputStream dos = new DataOutputStream(baos);
//
//        for (final short value : buffer)
//          {
//            dos.writeShort(value);
//          }
//
//        dos.close();
//        return baos.toByteArray();
//      }

    /*******************************************************************************************************************
     * 
     * 
     ******************************************************************************************************************/
    private static String asString (final byte[] bytes)
      {
        final StringBuilder builder = new StringBuilder();
        
        for (final byte b : bytes)
          {
            final String s = Integer.toHexString(b & 0xff);  
            assertTrue(s.length() <= 2);
            
            if (s.length() < 2)
              {
                builder.append('0');
              }
            
            builder.append(s);
          }
        
        return builder.toString();
      }
  }
