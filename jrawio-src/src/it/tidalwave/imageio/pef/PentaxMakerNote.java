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
 * $Id: PentaxMakerNote.java,v 1.2 2006/02/25 13:26:58 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.pef;

import java.io.IOException;
import java.nio.ByteOrder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: PentaxMakerNote.java,v 1.2 2006/02/25 13:26:58 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class PentaxMakerNote extends PentaxMakerNoteSupport
  {
    private final static long serialVersionUID = 6347805620960118907L;
    
    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        long baseOffsetSave = iis.getBaseOffset();
        ByteOrder byteOrderSave = iis.getByteOrder();

        iis.seek(offset);
        byte[] buffer = new byte[4];
        iis.read(buffer);
        String s = new String(buffer, 0, 3);

        if (s.equals("AOC")) // Pentax / Asahi Type 2 Makernote
          {
            TIFFImageReaderSupport.setByteOrder(iis);
            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
          }

        iis.setBaseOffset(baseOffsetSave);
        iis.setByteOrder(byteOrderSave);                 
      }
  }
