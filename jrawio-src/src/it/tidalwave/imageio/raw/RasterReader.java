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
 * $Id: RasterReader.java,v 1.6 2006/02/25 18:53:32 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.nio.ByteOrder;
import java.util.logging.Logger;
import java.io.IOException;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
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
 * @version CVS $Id: RasterReader.java,v 1.6 2006/02/25 18:53:32 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class RasterReader
  {
    private final static String CLASS = "it.tidalwave.imageio.raw.RasterReader";

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
    
    /*******************************************************************************
     * 
     * Sets the width of the raster to read.
     * 
     * @param width  the width of the raster
     * 
     *******************************************************************************/
    public void setWidth (int width)
      {
        this.width = width;
      }

    /*******************************************************************************
     * 
     * Sets the height of the raster to read.
     * 
     * @param height  the height of the raster
     * 
     *******************************************************************************/
    public void setHeight (int height)
      {
        this.height = height;
      }

    /*******************************************************************************
     * 
     * Sets the CFA pattern of the raster to read
     * 
     * @param cfaPattern  the CFA pattern
     * 
     *******************************************************************************/
    // Warning, this relies upon the fact that RGB offsets share the same value as TIFF CFA_PATTERN.
    // FIXME: decouple them
    public void setCFAPattern (byte[] cfaPattern)
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

    /*******************************************************************************
     * 
     * Sets the bits per sample of the raster to read.
     * 
     * @param bitsPerSample  the bits per sample value
     * 
     *******************************************************************************/
    public void setBitsPerSample (int bitsPerSample)
      {
        this.bitsPerSample = bitsPerSample;
      }

    /*******************************************************************************
     * 
     * Sets the tile width of tiled rasters.
     * 
     * @param tileWidth  the tile width
     * 
     *******************************************************************************/
    public void setTileWidth (int tileWidth)
      {
        this.tileWidth = tileWidth;
      }

    /*******************************************************************************
     * 
     * Sets the tile height of tiled rasters.
     * 
     * @param tileHeight  the tile height
     * 
     *******************************************************************************/
    public void setTileHeight (int tileHeight)
      {
        this.tileHeight = tileHeight;
      }

    /*******************************************************************************
     * 
     * Sets the count of horizontal tiles for tiled rasters.
     * 
     * @param tilesAcross  the count of horizontal tiles
     * 
     *******************************************************************************/
    public void setTilesAcross (int tilesAcross)
      {
        this.tilesAcross = tilesAcross;
      }

    /*******************************************************************************
     * 
     * Sets the count of vertical tiles for tiled rasters.
     * 
     * @param tilesDown  the count of vertical tiles
     * 
     *******************************************************************************/
    public void setTilesDown (int tilesDown)
      {
        this.tilesDown = tilesDown;
      }

    /*******************************************************************************
     * 
     * Sets the offsets to tile data for tiled rasters. The size of the array must
     * be equal to the total number of tiles, which is tilesAcross * tilesDown.
     * 
     * @param tileOffsets  the tile offsets
     * 
     *******************************************************************************/
    public void setTileOffsets (int[] tileOffsets)
      {
        this.tileOffsets = tileOffsets;
      }

    /*******************************************************************************
     * 
     * Sets the offset of the raster data
     * 
     * @param rasterOffset  the offset of raster data
     * 
     *******************************************************************************/
    public void setRasterOffset (long rasterOffset)
      {
        this.rasterOffset = rasterOffset;
      }
    
    /*******************************************************************************
     * 
     * Sets the number of strips for stripped rasters.
     * 
     * @param stripByteCount  the number of strips
     * 
     *******************************************************************************/
    public void setStripByteCount (int stripByteCount)
      {
        this.stripByteCount = stripByteCount;
      }

    /*******************************************************************************
     * 
     * Sets the compression type.
     * 
     * @param compression  the compression type
     * 
     *******************************************************************************/
    public void setCompression (int compression)
      {
        this.compression = compression;
      }

    /*******************************************************************************
     * 
     * Sets the linearization table. The table should hold 2^16 entries. If it is 
     * shorter, is padded to 2^16 entries equals to the last specified value.
     * 
     * @param linearizationTable  the linearization table
     * 
     *******************************************************************************/
    public void setLinearizationTable (int[] linearizationTable)
      {
        if (linearizationTable == null)
          {
            this.linearizationTable = null;
          }

        else
          {
            this.linearizationTable = new int[1 << 16];
            int length = linearizationTable.length;
            System.arraycopy(linearizationTable, 0, this.linearizationTable, 0, length);

            for (int i = length; i < this.linearizationTable.length; i++)
              {
                this.linearizationTable[i] = linearizationTable[length - 1];
              }
          }
      }

    /*******************************************************************************
     * 
     * If not set defaults to the stream order.
     * 
     *******************************************************************************/
    public void setByteOrder (ByteOrder byteOrder)
      {
        this.byteOrder = byteOrder;
      }
    
    /*******************************************************************************
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
     *******************************************************************************/
    public final WritableRaster loadRaster (RAWImageInputStream iis,
                                            RAWImageReaderSupport ir) throws IOException
      {
        assert width > 0 : "width not set";
        assert height > 0 : "height not set";
        assert bitsPerSample > 0 : "bitsPerSample not set";
        assert (stripByteCount > 0) || (tileOffsets.length > 0) : "strips or tiles not set";
        assert cfaOffsets != null : "cfaOffsets not set";
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

    /*******************************************************************************
     *
     * Reads an uncompressed raster. This method provides a reasonable 
     * implementation for most RAW types which use plain, uncompressed rasters.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected void loadUncompressedRaster (RAWImageInputStream iis,
                                           WritableRaster raster,
                                           RAWImageReaderSupport ir) throws IOException
      {
        if (bitsPerSample == 16)
          {
            loadUncompressedRaster16(iis, raster, ir);  
          }
        
        else
          {
            loadUncompressedRasterNot16(iis, raster, ir);  
          }
      }

    /*******************************************************************************
     *
     * Reads an uncompressed raster. 
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected void loadUncompressedRasterNot16 (RAWImageInputStream iis,
                                                WritableRaster raster,
                                                RAWImageReaderSupport ir) throws IOException      
      {
        logger.fine("loadUncompressedRaster()");
        logger.finer(">>>> CFA pattern: " + cfaOffsets[0] + " " + cfaOffsets[1] + " " + cfaOffsets[2] + " " + cfaOffsets[3]);

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

    /*******************************************************************************
     *
     * Reads an uncompressed raster. 
     * TODO: Refactor out, hide details in RAWImageInputStream.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected void loadUncompressedRaster16 (RAWImageInputStream iis,
                                             WritableRaster raster,
                                             RAWImageReaderSupport ir) throws IOException
      {
        logger.fine("loadUncompressedRaster16()");
        logger.finer(">>>> CFA pattern: " + cfaOffsets[0] + " " + cfaOffsets[1] + " " + cfaOffsets[2] + " " + cfaOffsets[3]);

        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        selectBitReader(iis, raster, bitsPerSample);
        
        if (byteOrder == null)
          {
            byteOrder = iis.getByteOrder();  
          }
        
        logger.finer(">>>> BYTE ORDER: " + byteOrder);
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
                int sample = (int)iis.readBits(bitsPerSample) % 0xFFFF;
                
                if (swap16)
                  {
                    sample = ((sample >>> 8) | (sample << 8)) & 0xFFFF;
                  }
                
                data[i + cfaOffsets[j + k]] = (short)sample;
                i += pixelStride;
              }

            ir.processImageProgress((100.0f * y) / height);
          }
      }    
    
    /*******************************************************************************
     *
     * Reads a compressed raster. This method is empty and specific subclasses for
     * various RAW formats should override it, if a compressed raster is available.
     * 
     * @param  iis          the image input stream
     * @param  raster       the raster to read data into
     * @param  ir           the image reader
     * @throws IOException  if an I/O error occurs
     *
     ******************************************************************************/
    protected void loadCompressedRaster (RAWImageInputStream iis,
                                         WritableRaster raster,
                                         RAWImageReaderSupport ir) throws IOException
      {
      }

    /*******************************************************************************
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
     *******************************************************************************/
    protected WritableRaster createRaster ()
      {
        int type = DataBuffer.TYPE_USHORT;
        int[] bandOffsets = { RasterReader.R_OFFSET, RasterReader.G_OFFSET, RasterReader.B_OFFSET };
        int bandCount = bandOffsets.length;
        int pixelStride = bandCount;
        int scanlineStride = pixelStride * width;

        return Raster.createInterleavedRaster(type, width, height, scanlineStride, pixelStride, bandOffsets, null);
      }

    /*******************************************************************************
     * 
     * Creates the most efficient {@link it.tidalwave.imageio.io.BitReader}, which
     * is the faster for the given number of bits per sample.
     * 
     * @param iis            the image input stream
     * @param raster         the raster to read data into
     * @param bitsPerSample  the number of bits per sample
     * 
     *******************************************************************************/
    protected void selectBitReader (RAWImageInputStream iis,
                                    WritableRaster raster,
                                    int bitsPerSample)
      {
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
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

    /*******************************************************************************
     * 
     * This method returns true if a compressed raster is supported. It returns 
     * <code>false</code> by default: subclasses should implement a test and return
     * the most appropriate value.
     * 
     * @return  true if a compressed raster is supported
     * 
     *******************************************************************************/
    protected boolean isCompressedRaster ()
      {
        return false;
      }

    /*******************************************************************************
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
     *******************************************************************************/
    protected int getRow (int interlacedRow, int height)
      {
        return interlacedRow;
      }

    /*******************************************************************************
     * 
     * Some RAW formats require that a certain number of bits is skipped at certain
     * columns. This method controls this behaviour. It returns 0 by default, and 
     * should be overridden by subclasses that need a different behaviour.
     * 
     * @param x		the column
     * @return 		the number of bits to skip
     * 
     *******************************************************************************/
    protected int getSkipCountAtColumn (int x)
      {
        return 0;
      }

    /*******************************************************************************
     * 
     * Some RAW formats require that a certain number of bits s skipped at the end
     * of each row. This method controls this behaviour. It returns 0 by default, 
     * and should be overridden by subclasses that need a different behaviour.
     * 
     * @return 		the number of bits to skip
     * 
     *******************************************************************************/
    protected int getSkipCountAtEndOfRow (int y, int height)
      {
        return 0;
      }
  }
