/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Packed12RasterReader;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading for E-300.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SPRasterReader extends Packed12RasterReader
  {
    private final static String CLASS = SPRasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private int height;

    @Override
    protected void loadUncompressedRaster (@Nonnull final RAWImageInputStream iis,
                                           @Nonnull final WritableRaster raster,
                                           @Nonnull final RAWImageReaderSupport ir)
      throws IOException
      {
        logger.fine("loadUncompressedRaster(%s, %s, %s)", iis, raster, ir);
        height = raster.getHeight();
        super.loadUncompressedRaster(iis, raster, ir);
      }

    @Nonnegative
    @Override
    protected int getRow (final @Nonnegative int interlacedRow, final @Nonnegative int height)
      {
        return interlacedRow * 2 - ((interlacedRow <= height / 2) ? 0 : (height / 2) * 2 + 1);
      }

    @Override
    protected void endOfRow (final @Nonnegative int y, final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        if (y == height / 2)
          {
            final long offset = (iis.getStreamPosition() + 1023) & -1024; // round to 1k boundary
            logger.finest(">>>> at row %d seeking to %d - current position %d", y, offset, iis.getStreamPosition());
            iis.seek(offset);
          }
      }

    @Override
    @Nonnull
    public String toString()
      {
        return String.format("SPRasterReader@%x", System.identityHashCode(this));
      }
  }
