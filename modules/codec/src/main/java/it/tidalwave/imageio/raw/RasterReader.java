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
package it.tidalwave.imageio.raw;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * A <code>RasterReader</code> is capable of reading raster data from an image 
 * stream. Different format SPI will subclass it to implement specialized
 * versions. This class should be invoked by a subclass of 
 * {@link RAWImageReaderSupport}.
 * <br>
 * This class must be filled with the proper raster attributes, then the method
 * {@link #loadRaster(RAWImageInputStream, RAWImageReaderSupport)}
 * must be called.
 * <br>
 * The following attributes are mandatory:
 * 
 * <ul>
 * <li>width: the raster width;</li>
 * <li>height: the raster height:</li>
 * <li>bitsPerSample: the number of bits per sample;</li>
 * <li>either stripByteCount for stripped rasters or tile attributes (tileWidth,
 *     tileHeight and tileOffsets) for tiled rasters;</li>
 * <li>cfaOffsets: the description of the Bayer array;</li>
 * <li>compression: the compression type</li>
 * </ul>
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RasterReader
  {
    private final static String CLASS = RasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    public static final int R_OFFSET = 0;

    public static final int G_OFFSET = 1;

    public static final int B_OFFSET = 2;

    /** The width of the raster. */
    private int width;

    /** The height of the raster. */
    private int height;

    /** The number of bits per sample. */
    protected int bitsPerSample = -1;

    /** The CFA offsets. */
    protected int[] cfaOffsets;

    /** The number of strips for stripped rasters. */
    protected int stripByteCount = -1;

    /** The width of a tile for tiled rasters.*/
    protected int tileWidth;

    /** The height of a tile for tiled rasters.*/
    protected int tileHeight;

    /** The count of tiles that span horizontally. */
    protected int tilesAcross;

    /** The count of tiles that span vertically. */
    protected int tilesDown;

    /** The offsets of tiles. */
    protected int[] tileOffsets;

    /** The linearization table. */
    protected int[] linearizationTable;

    /** The compression type. */
    protected int compression = -1; // Don't use an IFD enumeration since depends on TIFF	
    
    /** The offset of the raster data. */
    protected long rasterOffset;
    
    protected ByteOrder byteOrder;

    /*******************************************************************************************************************
     * 
     * Sets the width of the raster to read.
     * 
     * @param width  the width of the raster
     * 
     *******************************************************************************/
    public void setWidth (final @Nonnegative int width)
      {
        this.width = width;
      }

    /*******************************************************************************************************************
     * 
     * Sets the height of the raster to read.
     * 
     * @param height  the height of the raster
     * 
     *******************************************************************************/
    public void setHeight (final @Nonnegative int height)
      {
        this.height = height;
      }

    /*******************************************************************************************************************
     * 
     * Sets the CFA pattern of the raster to read
     * 
     * @param cfaPattern  the CFA pattern
     * 
     ******************************************************************************************************************/
    // Warning, this relies upon the fact that RGB offsets share the same value as TIFF CFA_PATTERN.
    // FIXME: decouple them
    public void setCFAPattern (final @Nonnull byte[] cfaPattern)
      {
        int size = cfaPattern.length;
        
        if ((size != 4) && (size != 8))
          {
            throw new IllegalArgumentException("Badly sized CFA data");    
          }
        
        this.cfaOffsets = new int[4];
        int base = (size == 4) ? 0 : 4;

        for (int i = 0; i < this.cfaOffsets.length; i++)
          {
            int o = cfaPattern[i + base];

            while (o > 3) // FIXME
              o -= 3;

            this.cfaOffsets[i] = o;
          }
      }

    /*******************************************************************************************************************
     * 
     * Sets the bits per sample of the raster to read.
     * 
     * @param bitsPerSample  the bits per sample value
     * 
     ******************************************************************************************************************/
    public void setBitsPerSample (final @Nonnegative int bitsPerSample)
      {
        this.bitsPerSample = bitsPerSample;
      }

    /*******************************************************************************************************************
     * 
     * Sets the tile width of tiled rasters.
     * 
     * @param tileWidth  the tile width
     * 
     ******************************************************************************************************************/
    public void setTileWidth (final @Nonnegative int tileWidth)
      {
        this.tileWidth = tileWidth;
      }

    /*******************************************************************************************************************
     * 
     * Sets the tile height of tiled rasters.
     * 
     * @param tileHeight  the tile height
     * 
     ******************************************************************************************************************/
    public void setTileHeight (final @Nonnegative int tileHeight)
      {
        this.tileHeight = tileHeight;
      }

    /*******************************************************************************************************************
     * 
     * Sets the count of horizontal tiles for tiled rasters.
     * 
     * @param tilesAcross  the count of horizontal tiles
     * 
     ******************************************************************************************************************/
    public void setTilesAcross (final @Nonnegative int tilesAcross)
      {
        this.tilesAcross = tilesAcross;
      }

    /*******************************************************************************************************************
     * 
     * Sets the count of vertical tiles for tiled rasters.
     * 
     * @param tilesDown  the count of vertical tiles
     * 
     ******************************************************************************************************************/
    public void setTilesDown (final @Nonnegative int tilesDown)
      {
        this.tilesDown = tilesDown;
      }

    /*******************************************************************************************************************
     * 
     * Sets the offsets to tile data for tiled rasters. The size of the array must
     * be equal to the total number of tiles, which is tilesAcross * tilesDown.
     * 
     * @param tileOffsets  the tile offsets
     * 
     ******************************************************************************************************************/
    public void setTileOffsets (final @Nonnull int[] tileOffsets)
      {
        this.tileOffsets = tileOffsets;
      }

    /*******************************************************************************************************************
     * 
     * Sets the offset of the raster data
     * 
     * @param rasterOffset  the offset of raster data
     * 
     ******************************************************************************************************************/
    public void setRasterOffset (final @Nonnegative long rasterOffset)
      {
        this.rasterOffset = rasterOffset;
      }
    
    /*******************************************************************************************************************
     * 
     * Sets the number of strips for stripped rasters.
     * 
     * @param stripByteCount  the number of strips
     * 
     ******************************************************************************************************************/
    public void setStripByteCount (final @Nonnegative int stripByteCount)
      {
        this.stripByteCount = stripByteCount;
      }

    /*******************************************************************************************************************
     * 
     * Sets the compression type.
     * 
     * @param compression  the compression type
     * 
     ******************************************************************************************************************/
    public void setCompression (final int compression)
      {
        this.compression = compression;
      }

    /*******************************************************************************************************************
     * 
     * Sets the linearization table. The table should hold 2^16 entries. If it is 
     * shorter, is padded to 2^16 entries equals to the last specified value.
     * 
     * @param linearizationTable  the linearization table
     * 
     ******************************************************************************************************************/
    public void setLinearizationTable (final @CheckForNull int[] linearizationTable)
      {
        logger.fine("setLinearizationTable(%d items)", (linearizationTable != null) ? linearizationTable.length : 0);
        
        if (linearizationTable == null)
          {
            this.linearizationTable = null;
          }

        else
          {
            this.linearizationTable = new int[1 << 16];
            final int length = linearizationTable.length;
            System.arraycopy(linearizationTable, 0, this.linearizationTable, 0, length);

            for (int i = length; i < this.linearizationTable.length; i++)
              {
                this.linearizationTable[i] = linearizationTable[length - 1];
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * If not set defaults to the stream order.
     * 
     ******************************************************************************************************************/
    public void setByteOrder (final @Nonnull ByteOrder byteOrder)
      {
        this.byteOrder = byteOrder;
      }
    
    /*******************************************************************************************************************
     * 
     * Loads the raster from the stream and creates a {@link java.awt.image.WritableRaster}.
     * This method makes sure that the mandatory attributes have been set, then 
     * asks to {@link #isCompressedRaster()} if the current raster is compressed. If
     * it is, the method 
     * {@link #loadCompressedRaster(RAWImageInputStream,WritableRaster,RAWImageReaderSupport)}
     * will be invoked; otherwise the method
     * {@link #loadUncompressedRaster(RAWImageInputStream,WritableRaster,RAWImageReaderSupport)}
     * is invoked. The raster is created by calling the method {@link #createRaster()}.
     * 
     * <br>
     * See the class general documentation to learn about mandatory attributes.
     * 
     * @param  iis          the image input stream
     * @param  ir           the image reader
     * @return              the raster
     * @throws IOException  if an I/O error occurs
     * 
     ******************************************************************************************************************/
    public final WritableRaster loadRaster (final @Nonnull RAWImageInputStream iis,
                                            final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        assert width > 0 : "width not set";
        assert height > 0 : "height not set";
        assert bitsPerSample > 0 : "bitsPerSample not set";
        assert (stripByteCount > 0) || (tileOffsets.length > 0) : "strips or tiles not set";
//        assert cfaOffsets != null : "cfaOffsets not set"; can be plain RGB
        assert compression != -1 : "compression not set";

        WritableRaster raster = createRaster();

        if (isCompressedRaster())
          {
            loadCompressedRaster(iis, raster, ir);
          }

        else
          {
            loadUncompressedRaster(iis, raster, ir);
          }

        return raster;
      }

    /*******************************************************************************************************************
     *
     * Reads an uncompressed raster. This method provides a reasonable 
     * implementation for most RAW types which use plain, uncompressed rasters.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void loadUncompressedRaster (final @Nonnull RAWImageInputStream iis,
                                           final @Nonnull WritableRaster raster,
                                           final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        if (bitsPerSample == 16)
          {
            if (cfaOffsets != null)
              {
                loadUncompressedRaster16(iis, raster, ir);
              }
            else
              {
                loadRGBUncompressedRasterNot16(iis, raster, ir);
              }
          }
        
        else
          {
            if (cfaOffsets != null)
              {
                loadUncompressedRasterNot16(iis, raster, ir);
              }
            else
              {
                loadRGBUncompressedRasterNot16(iis, raster, ir);
              }
          }
      }

    /*******************************************************************************************************************
     *
     * Reads an uncompressed raster. 
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void loadUncompressedRasterNot16 (final @Nonnull RAWImageInputStream iis,
                                                final @Nonnull WritableRaster raster,
                                                final @Nonnull RAWImageReaderSupport ir)
      throws IOException      
      {
        logger.fine("loadUncompressedRasterNot16(%s, %s, %s)", iis, raster, ir);
        logger.finer(">>>> CFA pattern: %d %d %d %d", cfaOffsets[0], cfaOffsets[1], cfaOffsets[2], cfaOffsets[3]);

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        selectBitReader(iis, raster, bitsPerSample);
        //
        // We can rely on the fact that the array has been zeroed by the JVM,
        // so we just set nonzero samples.
        //
        for (int y = 0; y < height; y++)
          {
            int row = getRow(y, height);
            int i = row * scanStride;
            int k = (row % 2) * 2;

            for (int x = 0; x < width; x++)
              {
                iis.skipBits(getSkipCountAtColumn(x));
                int j = x % 2;
                int sample = (int)iis.readBits(bitsPerSample);

                if (linearizationTable != null)
                  {
                    sample = linearizationTable[sample];
                  }

                data[i + cfaOffsets[j + k]] = (short)sample;
                i += pixelStride;
              }

            iis.skipBits(getSkipCountAtEndOfRow(y, height));
            ir.processImageProgress((100.0f * y) / height);
          }
      }

    /*******************************************************************************************************************
     *
     * Reads an uncompressed raster.
     *
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void loadRGBUncompressedRasterNot16 (final @Nonnull RAWImageInputStream iis,
                                                   final @Nonnull WritableRaster raster,
                                                   final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        logger.fine("loadRGBUncompressedRasterNot16(%s, %s, %s)", iis ,raster, ir);

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        selectBitReader(iis, raster, bitsPerSample);
        //
        // We can rely on the fact that the array has been zeroed by the JVM,
        // so we just set nonzero samples.
        //
        for (int y = 0; y < height; y++)
          {
            int row = getRow(y, height);
            int i = row * scanStride;

            for (int x = 0; x < width; x++)
              {
                iis.skipBits(getSkipCountAtColumn(x));

                for (int c = 0; c < 3; c++)
                  {
                    int sample = (int)iis.readBits(bitsPerSample);

                    if (linearizationTable != null)
                      {
                        sample = linearizationTable[sample];
                      }

                    data[i++] = (short)sample;
                  }
              }

            iis.skipBits(getSkipCountAtEndOfRow(y, height));
            ir.processImageProgress((100.0f * y) / height);
          }
      }

    /*******************************************************************************************************************
     *
     * Reads an uncompressed raster. 
     * TODO: Refactor out, hide details in RAWImageInputStream.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void loadUncompressedRaster16 (@Nonnull final RAWImageInputStream iis,
                                             @Nonnull final WritableRaster raster,
                                             @Nonnull final RAWImageReaderSupport ir) 
      throws IOException
      {
        long position = iis.getStreamPosition();
        logger.fine("loadUncompressedRaster16() at %d (0x%x), %d x %d %dbps",
                                                position, position, width, height, bitsPerSample);
        logger.finer(">>>> CFA pattern: %d %d %d %d", cfaOffsets[0], cfaOffsets[1], cfaOffsets[2], cfaOffsets[3]);

        final DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        final short[] data = dataBuffer.getData();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int pixelStride = 3; // FIXME
        final int scanStride = width * pixelStride;
        selectBitReader(iis, raster, 16);
        
        if (byteOrder == null)
          {
            byteOrder = iis.getByteOrder();  
          }
        
        logger.finer(">>>> byte order: %s", byteOrder);
        boolean swap16 = byteOrder == ByteOrder.BIG_ENDIAN;
        //
        // We can rely on the fact that the array has been zeroed by the JVM,
        // so we just set nonzero samples.
        //
        for (int y = 0; y < height; y++)
          {
            int row = getRow(y, height);
            int i = row * scanStride;
            int k = (row % 2) * 2;

            for (int x = 0; x < width; x++)
              {
                int j = x % 2;
//                int sample = iis.readShort() & 0xFFFF; Works, but it's 50% slower'
                int sample = (int)iis.readBits(16) & 0xFFFF;
                
                if (swap16)
                  {
                    sample = ((sample >>> 8) | (sample << 8)) & 0xFFFF;
                  }
                
                data[i + cfaOffsets[j + k]] = (short)sample;
                i += pixelStride;
              }

            ir.processImageProgress((100.0f * y) / height);
          }
        
        position = iis.getStreamPosition();
        logger.fine(">>>> loadUncompressedRaster16() completed at %d (0x%x)", position, position);
      }    
    
    /*******************************************************************************************************************
     *
     * Reads a compressed raster. This method is empty and specific subclasses for
     * various RAW formats should override it, if a compressed raster is available.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void loadCompressedRaster (final @Nonnull RAWImageInputStream iis,
                                         final @Nonnull WritableRaster raster,
                                         final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
      }

    /*******************************************************************************************************************
     * 
     * Creates a raster compatible with the specified attributes. The current
     * implementation creates a {@link java.awt.image.PixelInterleavedSampleModel} 
     * rather than a <code>sun.awt.image.ShortBandedRaster</code>, since the latter 
     * triggers bugs in the image filtering engine. 
     * <br>
     * TODO: it should be possible to specify the kind of raster to use, so that
     * the best performer can be used. 
     * 
     * @return  the raster
     * 
     ******************************************************************************************************************/
    @Nonnull
    final protected WritableRaster createRaster()
      {
        final int type = DataBuffer.TYPE_USHORT;
        final int[] bandOffsets = getBandOffsets();
        final int bandCount = bandOffsets.length;
        final int pixelStride = bandCount;
        final int scanlineStride = pixelStride * width;

        return Raster.createInterleavedRaster(type, width, height, scanlineStride, pixelStride, bandOffsets, null);
      }

    /*******************************************************************************************************************
     * 
     * Creates the most efficient {@link it.tidalwave.imageio.io.BitReader}, which
     * is the faster for the given number of bits per sample.
     * 
     * @param iis            the image input stream
     * @param raster         the raster to read data into
     * @param bitsPerSample  the number of bits per sample
     * 
     ******************************************************************************************************************/
    protected void selectBitReader (final @Nonnull RAWImageInputStream iis,
                                    final @Nonnull WritableRaster raster,
                                    final @Nonnegative int bitsPerSample)
      {
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        // FIXME: typeBits should be indeed bitsPerSample
        int typeBits = DataBuffer.getDataTypeSize(dataBuffer.getDataType());

        if (isCompressedRaster())
          {
            iis.selectBitReader(-1, 0);
          }

        else if ((bitsPerSample == 12) && ((stripByteCount % height) == 0))
          {
            iis.selectBitReader(12, stripByteCount / height);
          }

        else if (bitsPerSample == 16)
          {
            iis.selectBitReader(16, (width * typeBits) / 16);
          }

        else
          {
            iis.selectBitReader(-1, 0);
          }
      }

    /*******************************************************************************************************************
     * 
     * This method returns true if a compressed raster is supported. It returns 
     * <code>false</code> by default: subclasses should implement a test and return
     * the most appropriate value.
     * 
     * @return  true if a compressed raster is supported
     * 
     ******************************************************************************************************************/
    protected boolean isCompressedRaster()
      {
        return false;
      }

    /*******************************************************************************************************************
     * 
     * This method returns the row number given the current row. It returns the
     * row value unchanged by default. For RAW formats that implements interlaced
     * rasters (i.e. the sequence of rows is not 0,1,2... but something else) 
     * this method should be overridden.
     * 
     * @param interlacedRow  the current row
     * @param height         the height of the raster
     * @return               the row number
     * 
     ******************************************************************************************************************/
    @Nonnegative
    protected int getRow (final @Nonnegative int interlacedRow, final @Nonnegative int height)
      {
        return interlacedRow;
      }

    /*******************************************************************************************************************
     * 
     * Some RAW formats require that a certain number of bits is skipped at certain
     * columns. This method controls this behaviour. It returns 0 by default, and 
     * should be overridden by subclasses that need a different behaviour.
     * 
     * @param x		the column
     * @return 		the number of bits to skip
     * 
     ******************************************************************************************************************/
    @Nonnegative
    protected int getSkipCountAtColumn (final @Nonnegative int x)
      {
        return 0;
      }

    /*******************************************************************************************************************
     * 
     * Some RAW formats require that a certain number of bits s skipped at the end
     * of each row. This method controls this behaviour. It returns 0 by default, 
     * and should be overridden by subclasses that need a different behaviour.
     * 
     * @return 		the number of bits to skip
     * 
     ******************************************************************************************************************/
    @Nonnegative
    protected int getSkipCountAtEndOfRow (final @Nonnegative int y, final @Nonnegative int height)
      {
        return 0;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    protected int[] getBandOffsets()
      {
        return new int[] {RasterReader.R_OFFSET, RasterReader.G_OFFSET, RasterReader.B_OFFSET};
      }
  }
