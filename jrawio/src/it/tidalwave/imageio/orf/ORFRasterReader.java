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
 * $Id: ORFRasterReader.java 118 2008-08-24 23:09:13Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: ORFRasterReader.java 118 2008-08-24 23:09:13Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFRasterReader extends RasterReader
  {
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
        
        // FIXME: replace with a Map
        if (model.startsWith("C"))
          {
            return new CSeriesRasterReader();    
          }
        
        if (model.equals("E-300"))
          {
            return new E300RasterReader();    
          }
        
        if (model.equals("E-510"))
          {
            return new E410RasterReader();    
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
