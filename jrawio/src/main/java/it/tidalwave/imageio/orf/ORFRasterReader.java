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
import it.tidalwave.imageio.util.Logger;

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
    private final static String CLASS = ORFRasterReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private static final CSeriesRasterReader C_SERIES_RASTER_READER = new CSeriesRasterReader();
    private static final E300RasterReader E300_RASTER_READER = new E300RasterReader();
    private static final E410RasterReader E410_RASTER_READER = new E410RasterReader();
    private static final SPRasterReader SP_RASTER_READER = new SPRasterReader();
    
    private static final Map<String, RasterReader> RASTER_READER_MAP_BY_MODEL = new HashMap<String, RasterReader>();
    
    static
      {
        // FIXME: instead of this table, try to find a rule from metadata
        RASTER_READER_MAP_BY_MODEL.put("E-300",   E300_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-330",   E300_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-410",   E410_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-420",   E410_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-500",   E300_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-510",   E410_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-620",   E410_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("C5050Z",  SP_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("SP350",   SP_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("SP500UZ", SP_RASTER_READER);
        RASTER_READER_MAP_BY_MODEL.put("E-3",     E410_RASTER_READER);
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
        logger.fine("getInstance(%s)", model);
        
        model = model.toUpperCase().trim();
        final RasterReader rasterReader = RASTER_READER_MAP_BY_MODEL.get(model);
        
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
