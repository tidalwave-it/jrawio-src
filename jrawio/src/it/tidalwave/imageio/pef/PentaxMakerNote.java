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
 * $Id: PentaxMakerNote.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
 * @version $Id: PentaxMakerNote.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
