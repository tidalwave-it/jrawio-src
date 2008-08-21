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
 * $Id: OlympusMakerNote.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: OlympusMakerNote.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class OlympusMakerNote extends OlympusMakerNoteSupport
  {
    private final static long serialVersionUID = 6357805620960118907L;

    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        long baseOffsetSave = iis.getBaseOffset();
        iis.seek(offset);
        byte[] buffer = new byte[8];
        iis.read(buffer);
        String s = new String(buffer, 0, 5);

        if (s.equals("OLYMP"))
          {
            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
          }

        iis.setBaseOffset(baseOffsetSave);
      }
  }
