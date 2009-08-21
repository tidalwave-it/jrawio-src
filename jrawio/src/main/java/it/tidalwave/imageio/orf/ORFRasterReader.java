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
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import it.tidalwave.imageio.raw.RasterReader;

/***********************************************************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ORFRasterReader extends RasterReader
  {
    private static final CSeriesRasterReader C_SERIES_RASTER_READER = new CSeriesRasterReader();
    private static final E300RasterReader E300_RASTER_READER = new E300RasterReader();
    private static final E410RasterReader E410_RASTER_READER = new E410RasterReader();
    private static final SPRasterReader SP_RASTER_READER = new SPRasterReader();
    
    private static final Map<String, RasterReader> rasterReaderMapByModel = new HashMap<String, RasterReader>();
    
    static
      {
        rasterReaderMapByModel.put("E-300", E300_RASTER_READER);
        rasterReaderMapByModel.put("E-410", E410_RASTER_READER);
        rasterReaderMapByModel.put("E-500", E300_RASTER_READER);
        rasterReaderMapByModel.put("E-510", E410_RASTER_READER);

        rasterReaderMapByModel.put("C5050Z",  SP_RASTER_READER);
        rasterReaderMapByModel.put("SP350",   SP_RASTER_READER);
        rasterReaderMapByModel.put("SP500UZ", SP_RASTER_READER);
      }
    
    /*******************************************************************************************************************
     * 
     * Creates the proper {@link RasterReader} for the given model.
     * 
     * @param  model  the camera model
     * @return        the <code>RasterReader</code>
     * 
     ******************************************************************************************************************/
    @Nonnull
    public static RasterReader getInstance (@Nonnull String model)
      {
        model = model.toUpperCase().trim();
        final RasterReader rasterReader = rasterReaderMapByModel.get(model);
        
        if (rasterReader != null)
          {
            return rasterReader;
          }

        if (model.startsWith("C"))
          {
            return C_SERIES_RASTER_READER;    
          }
        
        return new ORFRasterReader();
      }
    
    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        return String.format("ORFRasterReader@%x", System.identityHashCode(this));
      }
  }
