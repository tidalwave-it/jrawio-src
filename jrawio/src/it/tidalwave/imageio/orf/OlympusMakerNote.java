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
 * $Id: OlympusMakerNote.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: OlympusMakerNote.java 81 2008-08-24 08:44:10Z fabriziogiudici $
 *
 ******************************************************************************/
public class OlympusMakerNote extends OlympusMakerNoteSupport
  {
    private final static long serialVersionUID = 6357805620960118907L;

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, final long offset) 
       throws IOException
      {
        long baseOffsetSave = iis.getBaseOffset();
        iis.seek(offset);
        final byte[] buffer = new byte[8];
        iis.read(buffer);
        final String s = new String(buffer, 0, 5);

        if (s.equals("OLYMP"))
          {
            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
          }

        iis.setBaseOffset(baseOffsetSave);
      }
  }
