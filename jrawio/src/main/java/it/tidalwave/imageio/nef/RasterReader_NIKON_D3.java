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
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnull;
import java.awt.image.WritableRaster;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.craw.RAWImageReaderSupport;

/***********************************************************************************************************************
 *
 * This class specializes a PixelLoader for the Nikon D100. There is some 
 * trickery to understand if a D100 NEF is compressed and in some cases there
 * are bits to skip while reading the data.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RasterReader_NIKON_D3 extends NEFRasterReader
  {
    @Override
    protected void loadUncompressedRaster (final @Nonnull RAWImageInputStream iis,
                                           final @Nonnull WritableRaster raster,
                                           final @Nonnull RAWImageReaderSupport ir)
      throws IOException
      {
        assert (cfaOffsets != null);
        loadUncompressedRaster16(iis, raster, ir);
      }
   }
