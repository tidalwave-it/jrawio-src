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
 * $Id: ORFRasterReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import it.tidalwave.imageio.io.RAWImageInputStream;
import java.awt.image.WritableRaster;
import java.util.logging.Logger;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * This class implements the ORF (Olympus raw Format) raster loading.
 * 
 * @author  Fabrizio Giudici
 * @version CVS $Id: ORFRasterReader.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFRasterReader extends RasterReader
  {
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public static ORFRasterReader getInstance (String model)
      {
        model = model.toUpperCase().trim();
        
        if (model.startsWith("C"))
          {
            return new CSeriesRasterReader();    
          }
        
        if (model.equals("E-300"))
          {
            return new E300RasterReader();    
          }
        
        return new ORFRasterReader();
      }
  }
