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
 * $Id: RAWImageReaderSupport.java 151 2008-09-13 15:13:22Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import it.tidalwave.imageio.util.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/*******************************************************************************
 *
 * This class provides support for all RAW image readers.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: RAWImageReaderSupport.java 151 2008-09-13 15:13:22Z fabriziogiudici $
 *
 ******************************************************************************/
public abstract class RAWImageReaderSupport extends ImageReader
  {
    private final static String CLASS = "it.tidalwave.imageio.raw.RAWImageReaderSupport";

    private final static Logger logger = Logger.getLogger(CLASS);
    
    /** The input object cast as a RAWImageInputStream. */
    protected RAWImageInputStream iis;

    /** The loaded image(s). */
    private BufferedImage[] image;

    /** The loaded thumbnail(s). */
    private BufferedImage[][] thumbnail;

    /** True if metadata has been loaded. */
    protected boolean metadataLoaded = false;

    /** The image metadata. */
    protected RAWMetadataSupport metadata;

    /** The primary directory. */
    protected Directory primaryDirectory;

    /** The Maker Note. */
    protected Directory makerNote;
    
    protected HeaderProcessor headerProcessor = new HeaderProcessor();

    /*******************************************************************************
     * 
     * @param originatingProvider
     * 
     *******************************************************************************/
    protected RAWImageReaderSupport (ImageReaderSpi originatingProvider)
      {
        super(originatingProvider);
      }

    /*******************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************/
    public final BufferedImage read (int imageIndex, ImageReadParam param) 
      throws IOException
      {
        logger.info("read(imageIndex=" + imageIndex + ", param=" + param + ")");

        int imageCount = getNumImages(true);

        if (image == null)
          {
            image = new BufferedImage[imageCount];
          }

        if (imageIndex >= image.length)
          {
            throw new IllegalArgumentException("Bad imageIndex: " + imageIndex + " max is " + (imageCount - 1));
          }

        if (image[imageIndex] == null)
          {
            processImageStarted(imageIndex);
            processSequenceStarted(imageIndex);

            try
              {
                BufferedImage rawImage = loadImage(imageIndex); 
                image[imageIndex] = ((RAWImageReaderSpiSupport)getOriginatingProvider()).postProcess(rawImage, metadata);
                processImageComplete();
              }

            catch (IOException e)
              {
                abort();
                processReadAborted();
                throw e;
              }
          }

        logger.info(">>>> read() completed ok");

        return image[imageIndex];
      }

    /*******************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************/
    public final BufferedImage readThumbnail (int imageIndex, int thumbnailIndex) 
      throws IOException
      {
        logger.info("readThumbnail(imageIndex=" + imageIndex + ", thumbnailIndex=" + thumbnailIndex + ")");

        if (!readerSupportsThumbnails())
          {
            throw new UnsupportedOperationException("Thumbnails not supported!");
          }

        int imageCount = getNumImages(true);

        if (imageIndex >= imageCount)
          {
            throw new IllegalArgumentException("Invalid imageIndex: " + imageIndex + " max is " + (imageCount - 1));
          }

        int thumbnailCount = getNumThumbnails(imageIndex);

        if (thumbnailIndex >= thumbnailCount)
          {
            throw new IllegalArgumentException("Invalid thumbnailIndex: " + thumbnailIndex + " max is "
                + (thumbnailCount - 1));
          }

        if (thumbnail == null)
          {
            thumbnail = new BufferedImage[imageCount][];
          }

        if (thumbnail[imageIndex] == null)
          {
            thumbnail[imageIndex] = new BufferedImage[thumbnailCount];
          }

        if (thumbnail[imageIndex][thumbnailIndex] == null)
          {
            processThumbnailStarted(imageIndex, thumbnailIndex);
            //            processSequenceStarted(imageIndex);

            try
              {
                thumbnail[imageIndex][thumbnailIndex] = loadThumbnail(imageIndex, thumbnailIndex);
                processThumbnailComplete();
              }

            catch (IOException e)
              {
                abort();
                processReadAborted();
                throw e;
              }
          }

        logger.info(">>>> readThumbnail() completed ok");

        return thumbnail[imageIndex][thumbnailIndex];
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public IIOMetadata getImageMetadata (int imageIndex) 
      throws IOException
      {
        if (metadata == null)
          {
            try
              {
                metadata = loadMetadata(imageIndex);
              }

            catch (IOException e)
              {
                abort();
                throw e;
              }
          }

        return metadata;
      }

    /*******************************************************************************
     * 
     * Overrides original implementation by calling {@link #wrapInput(Object)} to give
     * a chance to use a decorator for the input.
     * 
     *******************************************************************************/
    public void setInput (Object input,
                          boolean seekForwardOnly,
                          boolean ignoreMetadata)
      {
        disposeAll();
        input = wrapInput(input);

        if (input != null)
          {
            logger.finer("Wrapped input is: " + input.getClass());
          }

        super.setInput(input, seekForwardOnly, ignoreMetadata);
        iis = (RAWImageInputStream)input;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public final void reset ()
      {
        logger.fine("reset()");
        super.reset();
        disposeAll();
      }

    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    public final void dispose ()
      {
        logger.fine("dispose()");
        super.dispose();
        disposeAll();
      }

    /*******************************************************************************
     *
     * Never use this method. This is to make it public and available to the
     * RasterReader. FIXME: this is not elegant.
     *
     ******************************************************************************/
    public void processImageProgress (float progress)
      {
        super.processImageProgress(progress);
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public boolean readerSupportsThumbnails()
      {
        return true;
      }

    /*******************************************************************************
     *
     * Returns the number of images managed by this ImageReader. There's only ONE
     * image in a RAW file (thumbnails are not counted here).
     *
     * @param  allowSearch  unused
     * @return              the number of managed images
     *
     ******************************************************************************/
    public int getNumImages (boolean allowSearch)
      {
        return 1;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Nonnull
    public Iterator<ImageTypeSpecifier> getImageTypes (@Nonnegative final int imageIndex)
      {
        checkImageIndex(imageIndex);
        final List<ImageTypeSpecifier> imageTypes = new ArrayList<ImageTypeSpecifier>();
        final ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
        final int type = DataBuffer.TYPE_USHORT;
        final int[] bandOffsets = { RasterReader.R_OFFSET, RasterReader.G_OFFSET, RasterReader.B_OFFSET };
        final ImageTypeSpecifier imageType = ImageTypeSpecifier.createInterleaved(rgb, bandOffsets, type, false, false);
        imageTypes.add(imageType);
        
        return imageTypes.iterator();
      }

    /*******************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************/
    public IIOMetadata getStreamMetadata ()
      {
        return null;
      }

    /*******************************************************************************
     * 
     * This default implementation just ensures that metadata is loaded and then
     * calls {@link #loadRAWImage(RAWImageInputStream)}.
     * 
     * @param imageIndex  the index of the image to load
     * @return            the loaded image
     * 
     *******************************************************************************/
    protected BufferedImage loadImage (int imageIndex) throws IOException
      {
        logger.fine("loadImage(iis: " + iis + ", imageIndex: " + imageIndex + ")");
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return loadRAWImage();
      }

    /*******************************************************************************
     * 
     * This method must be implemented by concrete subclasses to load a thumbnail.
     * 
     * @param  imageIndex     the index of the image 
     * @param  thumbnailIndex the index of the thumbnail to load
     * @return                the loaded thumbnail
     * @throws IOException    if an I/O error occurs
     * 
     *******************************************************************************/
    protected abstract BufferedImage loadThumbnail (int imageIndex, int thumbnailIndex) throws IOException;

    /*******************************************************************************
     * 
     * This method must be implemented by concrete subclasses to load an image raster.
     * 
     * @return                the loaded raster
     * @throws IOException    if an I/O error occurs
     * 
     *******************************************************************************/
    protected abstract WritableRaster loadRAWRaster () throws IOException;

    /*******************************************************************************
     * 
     * This method must be implemented by concrete subclasses to create an instance
     * of {@link RAWMetadataSupport}. Each SPI should use its own subclass.
     * 
     * @param primaryDirectory  the primary directory
     * @param imageDirectory    the image directory
     * @return                  the raw metadata
     * 
     *******************************************************************************/
    protected abstract RAWMetadataSupport createMetadata (Directory primaryDirectory, Directory imageDirectory);

    /*******************************************************************************
     * 
     * Loads the primary directory.
     * 
     * @return                the primary directory
     * @throws IOException    if an I/O error occurs
     * 
     *******************************************************************************/
    protected abstract Directory loadPrimaryDirectory() throws IOException;

    /*******************************************************************************
     *
     * Analyzes and sets various variables to point at different pieces of metadata.
     *
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected abstract void processMetadata() throws IOException;

    /*******************************************************************************
     * 
     * Gives a chance to use a decorator for the input object. By default 
     * a RAWImageInputStream is wrapped. Subclasses may change this behaviour 
     * by overriding this method.
     * 
     * @param  input  the input object
     * @return        the decorated object
     * 
     *******************************************************************************/
    protected Object wrapInput (Object input)
      {
        return new RAWImageInputStream((ImageInputStream)input);
      }

    /*******************************************************************************
     *
     * This method is provided for subclasses that want to abort the loading
     * process. This calls the abortRequested() method of the ImageInputStream and
     * then throws a RuntimeException.
     *
     * @param  message  		  the message relative to the failure
     * @throws RuntimeException  always
     *
     ******************************************************************************/
    protected void failure (String message)
      {
        abortRequested();
        throw new RuntimeException(message);
      }

    /*******************************************************************************
     *
     * Create a BufferedImage given a raster.
     *  
     * @param transferType    the transfer type (one of the {@link java.awt.image.DataBuffer}
     *                        <code>.TYPE</code> values).
     * @param colorSpaceType	the color space type
     * @param raster			the raster
     * @return				the new BufferedImage
     * 
     *******************************************************************************/
    protected BufferedImage createImage (int transferType,
                                         int colorSpaceType,
                                         WritableRaster raster)
      {
        ColorSpace colorSpace = ColorSpace.getInstance(colorSpaceType);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, transferType);
        Properties properties = new Properties();

        return new BufferedImage(colorModel, raster, false, properties);
      }

    /*******************************************************************************
     * 
     * Releases all the allocated resources.
     * 
     *******************************************************************************/
    protected void disposeAll ()
      {
        image = null;
        thumbnail = null;
        metadata = null;
        primaryDirectory = null;
        makerNote = null;
        metadataLoaded = false;
      }

    /*******************************************************************************
     *
     * Checks the validity of an image index.
     * 
     * @param 	imageIndex					the imageindex
     * @throws	IndexOutOfBoundsException  	if the index is invalid
     * 
     ******************************************************************************/
    protected void checkImageIndex (int imageIndex)
      {
        if (imageIndex > 0)
          {
            throw new IndexOutOfBoundsException("Invalid image index: " + imageIndex);
          }
      }

    /*******************************************************************************
     *
     * Ensures that metadata has been loaded; loads it now otherwise.
     * 
     * @param   imageIndex
     * @throws  IllegalStateException  if metadata can't be loaded
     * 
     ******************************************************************************/
    protected void ensureMetadataIsLoaded (int imageIndex) 
      throws IOException 
      {
        if (!metadataLoaded)
          {
            loadMetadata(imageIndex);
            checkMetadataIsLoaded();
          }
      }

    /*******************************************************************************
     *
     * Checks that metadata has been loaded.
     * 
     * @throws  IllegalStateException  if metadata is not loaded
     * 
     ******************************************************************************/
    protected void checkMetadataIsLoaded()
      {
        if (!metadataLoaded)
          {
            throw new IllegalStateException("Metadata was not read yet");
          }
      }

    /*******************************************************************************
     * 
     * Creates a Raster, an image and then invokes loadRAWRaster() to read the 
     * actual image data.
     * 
     * @return              the image
     * @throws IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    protected final BufferedImage loadRAWImage() throws IOException
      {
        logger.fine("loadImage(iis: " + iis + ")");
        WritableRaster raster = loadRAWRaster();
        int dataType = raster.getDataBuffer().getDataType();
        BufferedImage bufferedImage = createImage(dataType, ColorSpace.CS_LINEAR_RGB, raster);
        logger.fine(">>>> loadImage() completed ok");

        return bufferedImage;
      }

    /*******************************************************************************
     *
     * Concrete implementation of the metadata loader.
     * 
     * @param  imageIndex   the imageIndex of the metadata to load
     * @return              the metadata
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected synchronized RAWMetadataSupport loadMetadata (int imageIndex) throws IOException
      {
        logger.fine("loadMetadata(iis: " + iis + ", imageIndex: " + imageIndex + ")");
        checkImageIndex(imageIndex);

        if (!metadataLoaded)
          {
            processMetadata();
            metadataLoaded = true;
          }

        logger.fine(">>>> loadMetadata() completed ok");

        return metadata;
      }

    /*******************************************************************************
     * 
     * Loads an embedded image. This method is usually used to laod thumbnails, which
     * are coded as embedded JPEGs. Anyway this method is able to deal with any
     * image format for which a SPI driver is available.
     * 
     * @param  iis          the image input stream
     * @param  offset       the offset of the image data
     * @param  length       the length of the image data
     * @return              the image
     * @throws IOException  if an I/O error occurs
     * 
     *******************************************************************************/
    protected static BufferedImage loadEmbeddedImage (ImageInputStream iis,
                                                      int offset,
                                                      int length) throws IOException
      {
        logger.fine("loadEmbeddedImage(iis: " + iis + ", offset: " + offset + ", length: " + length + ")");
        long time = System.currentTimeMillis();
        byte[] buffer = new byte[length];
        iis.seek(offset);
        iis.read(buffer, 0, length);

        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        BufferedImage image = ImageIO.read(is);
        is.close();

        logger.fine(">>>> loadEmbeddedImage() completed ok in " + (System.currentTimeMillis() - time)
            + " msec., returning " + image);

        return image;
      }

    /*******************************************************************************
     *
     * Facility method to retrieve the ImageInputStream.
     *
     ******************************************************************************/
    /*
    protected RAWImageInputStream getImageInputStream ()
      {
        RAWImageInputStream iis = null;

        if (input == null)
          {
            failure("Null source");
          }

        else if (input instanceof RAWImageInputStream)
          {
            iis = (RAWImageInputStream)input;
          }

        else
          {
            failure("Unknown source class: " + input.getClass());
          }

        return iis;
      }*/
  }
