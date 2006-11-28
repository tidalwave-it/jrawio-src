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
 * $Id: NEFHeaderProcessor.java,v 1.3 2006/02/25 00:05:36 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFHeaderProcessor.java,v 1.3 2006/02/25 00:05:36 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFHeaderProcessor extends HeaderProcessor
  {
    public static final int NDF_OFFSET = 12;
    
    private boolean isNDF;

    /*******************************************************************************
     * 
     * @param iis
     * @throws IOException
     * 
     *******************************************************************************/
    public void process (RAWImageInputStream iis) throws IOException
      {
        iis.mark();
        byte[] buffer = new byte[NDF_OFFSET];
        iis.seek(0);
        iis.read(buffer, 0, buffer.length);
        isNDF = new String(buffer, 0, 3).equals("NDF");         
        iis.reset();
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public int getOffset()
      {
        return isNDF ? NDF_OFFSET : 0;
      }
  }
