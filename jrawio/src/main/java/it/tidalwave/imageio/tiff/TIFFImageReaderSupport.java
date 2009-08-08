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
package it.tidalwave.imageio.tiff;

import javax.annotation.Nonnegative;
import java.lang.reflect.Constructor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * This abstract class is provided as a support for implementing, by subclassing,
 * an ImageReader for any TIFF-based image format.
 *
 * @author Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class TIFFImageReaderSupport extends RAWImageReaderSupport
  {
    private final static String CLASS = TIFFImageReaderSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /** The TIFF magic number for TIFF version */
    private final static short TIFF_MAGIC = 42;

    /** The TIFF magic number for little endian style. */
    private final static short TIFF_LITTLE_ENDIAN = 0x4949;

    /** The TIFF magic number for big endiang style. */
    private final static short TIFF_BIG_ENDIAN = 0x4d4d;
    
    /** The class of the maker note. */
    protected Class makerNoteClass;
    
    /** The class of the metadata. */
    private Class metadataClass;

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    protected TIFFImageReaderSupport (ImageReaderSpi originatingProvider, Class makerNoteClass, Class metadataClass)
      {
        super(originatingProvider);
        this.makerNoteClass = makerNoteClass;
        this.metadataClass = metadataClass;
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public int getWidth (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return ((TIFFMetadataSupport)metadata).getWidth();
      }

    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public final int getHeight (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return ((TIFFMetadataSupport)metadata).getHeight();
      }
    
    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    @Override
    public final int getNumThumbnails (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        ThumbnailLoader[] thumbnailInfo = ((TIFFMetadataSupport)metadata).getThumbnailHelper();
        return thumbnailInfo.length;
      }
    
    /*******************************************************************************************************************
     *
     * Overrides the default ImageReader implementation by avoiding to actually
     * load the thumbnail.
     *
     ******************************************************************************************************************/
    @Override
    public int getThumbnailWidth (int imageIndex, int thumbnailIndex) throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);
        return ((TIFFMetadataSupport)metadata).getThumbnailWidth(thumbnailIndex);
      }

    /*******************************************************************************************************************
     *
     * Overrides the default ImageReader implementation to avoid to actually
     * load the thumbnail.
     *
     ******************************************************************************************************************/
    @Override
    public int getThumbnailHeight (int imageIndex, int thumbnailIndex) throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);
        return ((TIFFMetadataSupport)metadata).getThumbnailHeight(thumbnailIndex);
     }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected BufferedImage loadThumbnail (int imageIndex, int thumbnailIndex) throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);
        ThumbnailLoader[] thumbnailInfo = ((TIFFMetadataSupport)metadata).getThumbnailHelper();
        return thumbnailInfo[thumbnailIndex].load(iis);
      }

    /*******************************************************************************************************************
     *
     * A default implementation that just invokes loadRAWImage().
     *
     * @param  imageIndex      the imageIndex of the image to load - 0 is the only
     *                         valid value
     * @return                 the thumbnail
     * @throws IOException     if an I/O error occurs
     *
     ******************************************************************************************************************/
    @Override
    protected BufferedImage loadImage (int imageIndex) throws IOException
      {
        logger.fine("loadImage(%d) - iis: %s", imageIndex, iis);
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        BufferedImage image = loadRAWImage();
        logger.fine(">>>> loadImage() completed ok, returning %s", image);

        return image;
      }

    /*******************************************************************************************************************
     *
     * Processes a TIFF header and sets the endianness property for the given
     * ImageInputStream. Returns the offset of the primary IFD.
     *
     * @param  iis          the ImageInputStream
     * @return              the offset of the primary IFD
     * @throws IOException  if validation errors occur
     *
     ******************************************************************************************************************/
    public static long processHeader (ImageInputStream iis,
                                      HeaderProcessor headerProcessor) throws IOException
      {
        int fileOffset = 0;
        logger.fine("processHeader(%s, %s)", iis, headerProcessor);

        if (headerProcessor != null)
          {
            fileOffset = headerProcessor.getOffset();
          }

        iis.skipBytes(fileOffset);
        logger.finest(">>>> reading byte order at %d", iis.getStreamPosition());
        setByteOrder(iis);

        short magic = iis.readShort();

        if (magic < TIFF_MAGIC)
          {
            throw new IOException("Invalid magic: " + magic);
          }

        long offset = iis.readUnsignedInt() + fileOffset;
        logger.finer(">>>> processHeader() returning offset is %d", offset);

        return offset;
      }

    /*******************************************************************************************************************
     *
     * Sets the byteorder of the given ImageInputStream guessing it by the marker
     * in the next 16-bit integer.
     *
     * @param  iis          the ImageInputSream to set the byte order to
     * @throws IOException  if an error occurs
     *
     ******************************************************************************************************************/
    public static void setByteOrder (ImageInputStream iis) throws IOException
      {
        short byteOrder = iis.readShort();

        if (byteOrder == TIFF_LITTLE_ENDIAN)
          {
            iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
          }

        else if (byteOrder == TIFF_BIG_ENDIAN)
          {
            iis.setByteOrder(ByteOrder.BIG_ENDIAN);
          }

        else
          {
            throw new IOException("Invalid byte order: 0x" + Integer.toHexString(byteOrder));
          }

        logger.finer(">>>> Byte order is %s", iis.getByteOrder());
      }

    /*******************************************************************************************************************
     * 
     * @param bitsPerSample
     * @param rasterReader
     * 
     *******************************************************************************/
    protected void initializeRasterReader (int width,
                                           int height,
                                           int bitsPerSample,
                                           RasterReader rasterReader)
      {
        IFD rasterIFD = ((TIFFMetadataSupport)metadata).getRasterIFD();
        IFD exifIFD = ((TIFFMetadataSupport)metadata).getExifIFD();
        rasterReader.setWidth(width);
        rasterReader.setHeight(height);
        rasterReader.setBitsPerSample(bitsPerSample);
        
        if (exifIFD.isEXIFCFAPatternAvailable())
          {
            rasterReader.setCFAPattern(exifIFD.getEXIFCFAPattern());
          }

        if ((rasterIFD != null) && rasterIFD.isCFAPatternAvailable())
          {
            rasterReader.setCFAPattern(rasterIFD.getCFAPattern());
          }
        //  rasterReader.setLinearizationTable(imageIFD.getLinearizationTable());
        
        if ((rasterIFD != null) && rasterIFD.isCompressionAvailable())
          {
            rasterReader.setCompression(rasterIFD.getCompression().intValue());
          }

        if ((rasterIFD != null) && rasterIFD.isStripByteCountsAvailable())
          {
            rasterReader.setStripByteCount(rasterIFD.getStripByteCounts());
          }

        if ((rasterIFD != null) && rasterIFD.isTileWidthAvailable())
          {
            int imageWidth = rasterIFD.getImageWidth();
            int imageLength = rasterIFD.getImageLength();
            int tileWidth = rasterIFD.getTileWidth();
            int tileLength = rasterIFD.getTileLength();
            rasterReader.setTileWidth(tileWidth);
            rasterReader.setTileHeight(tileLength);
            rasterReader.setTilesAcross((imageWidth + tileWidth - 1) / tileWidth);
            rasterReader.setTilesDown((imageLength + tileLength - 1) / tileLength);
            rasterReader.setTileOffsets(rasterIFD.getTileOffsets());
            //int[] tileByteCounts = imageIFD.getTileByteCounts();
          }

        if ((rasterIFD != null) && rasterIFD.isLinearizationTableAvailable())
          {
            rasterReader.setLinearizationTable(rasterIFD.getLinearizationTable());
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    protected Directory loadPrimaryDirectory() throws IOException
      {
        logger.fine("loadPrimaryDirectory() - %s", iis);
        headerProcessor.process(iis);
        iis.seek(0);
        long directoryOffset = processHeader(iis, headerProcessor); // FIXME: refactor so that processHeader doe snot use headerSkipper

        if (directoryOffset == 20) // FIXME: bad fix for NDF files, must test for NDF instead
          {
            directoryOffset -= 12;
            ((RAWImageInputStream)iis).setBaseOffset(12);
          }

        IFD primaryIFD = createPrimaryIFD();
        primaryIFD.loadAll(iis, directoryOffset);
        
        return primaryIFD;
      }
    
    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    protected IFD createPrimaryIFD()
      {
        return new IFD();
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected RAWMetadataSupport createMetadata (Directory primaryDirectory, Directory imageDirectory)
      {
        try
          {
            Constructor constructor = metadataClass.getConstructor(new Class[]{Directory.class, RAWImageInputStream.class, HeaderProcessor.class});
            return (RAWMetadataSupport)constructor.newInstance(new Object[]{primaryDirectory, iis, headerProcessor});
          }
        catch (Exception e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void processMetadata() throws IOException
      {
        primaryDirectory = loadPrimaryDirectory();
        logger.fine(">>>> primary directory: %s", primaryDirectory);
        processEXIFAndMakerNote(primaryDirectory);
        metadata = createMetadata(primaryDirectory, null);
      }

    /*******************************************************************************************************************
     *
     * Processes the EXIF metadata, if present. The EXIF data is added to the
     * imageMetadata. The MakerNote is processed too.
     *
     * @param   directory    the primary directory
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void processEXIFAndMakerNote (Directory directory) throws IOException
      {
        if (((IFD)directory).isExifIFDPointerAvailable())
          {
            IFD exifIFD = new IFD(); 
            exifIFD.loadAll(iis, ((IFD)directory).getExifIFDPointer());
            directory.addNamedDirectory(IFD.EXIF_NAME, exifIFD);
            logger.fine("EXIF IFD: %s", exifIFD);

            if (exifIFD.isMakerNoteAvailable())
              {
                processMakerNote();
              }

            if (exifIFD.isInteroperabilityIFDAvailable())
              {
                IFD interoperabilityIFD = new IFD();
                interoperabilityIFD.loadAll(iis, exifIFD.getInteroperabilityIFD());
                exifIFD.addNamedDirectory(IFD.INTEROPERABILITY_NAME, interoperabilityIFD); 
                logger.fine("INTEROPERABILITY IFD: %s", interoperabilityIFD);
              }
          }

        if (((IFD)directory).isGPSInfoIFDPointerAvailable())
          {
            IFD gpsIFD = new IFD();
            gpsIFD.loadAll(iis, ((IFD)directory).getGPSInfoIFDPointer());
            directory.addNamedDirectory(IFD.GPS_NAME, gpsIFD); 
            logger.fine("GPS IFD: %s", gpsIFD);
          }
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void processMakerNote() throws IOException
      {
        try
          {
            makerNote = (Directory)makerNoteClass.newInstance();
          }
        catch (Exception e)
          {
            throw new RuntimeException(e);
          }
        
        IFD exifIFD = (IFD)primaryDirectory.getNamedDirectory(IFD.EXIF_NAME);
        int makerNoteOffset = exifIFD.getMakerNoteOffset();        
        makerNote.loadAll(iis, makerNoteOffset);
        exifIFD.addNamedDirectory(IFD.MAKER_NOTE_NAME, makerNote);
        logger.fine("MakerNote: %s", makerNote);
      }

    /*******************************************************************************************************************
     * 
     * Checks the validity of a thumbnail index.
     * 
     * @param   thumbnailIndex              the thumbnail index
     * @throws  IndexOutOfBoundsException   if the index is invalid
     * 
     *******************************************************************************/
    protected void checkThumbnailIndex (int thumbnailIndex) throws IOException
      {
        if (thumbnailIndex >= getNumThumbnails(0)) // FIXME
          {
            throw new IndexOutOfBoundsException("Invalid thumbnail index: " + thumbnailIndex);
          }
      }
  }
