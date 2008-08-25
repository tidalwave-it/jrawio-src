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
 * $Id: ORFRasterReader.java 122 2008-08-25 00:15:14Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: ORFRasterReader.java 122 2008-08-25 00:15:14Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFRasterReader extends RasterReader
  {
    private static final CSeriesRasterReader C_SERIES_RASTER_READER = new CSeriesRasterReader();
    private static final E300RasterReader E300_RASTER_READER = new E300RasterReader();
    private static final E410RasterReader E410_RASTER_READER = new E410RasterReader();
    
    private static final Map<String, ORFRasterReader> rasterReaderMapByModel =
            new HashMap<String, ORFRasterReader>();
    
    static
      {
        rasterReaderMapByModel.put("E-300", E300_RASTER_READER);
        rasterReaderMapByModel.put("E-410", E410_RASTER_READER);
        rasterReaderMapByModel.put("E-500", E300_RASTER_READER);
        rasterReaderMapByModel.put("E-510", E410_RASTER_READER);
      }
    
    /***************************************************************************
     * 
     * Creates the proper {@link RasterReader} for the given model.
     * 
     * @param  model  the camera model
     * @return        the <code>RasterReader</code>
     * 
     **************************************************************************/
    @Nonnull
    public static ORFRasterReader getInstance (@Nonnull String model)
      {
        model = model.toUpperCase().trim();
        final ORFRasterReader rasterReader = rasterReaderMapByModel.get(model);
        
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
    
    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        return "ORFRasterReader";  
      }
  }
