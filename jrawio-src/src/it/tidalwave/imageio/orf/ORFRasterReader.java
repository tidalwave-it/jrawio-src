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
 * $Id: ORFRasterReader.java,v 1.5 2006/02/25 16:17:07 fabriziogiudici Exp $
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
 * @version CVS $Id: ORFRasterReader.java,v 1.5 2006/02/25 16:17:07 fabriziogiudici Exp $
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
