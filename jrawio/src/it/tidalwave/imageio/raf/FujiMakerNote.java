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
 * $Id: PentaxMakerNote.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raf;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PentaxMakerNote.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 ******************************************************************************/
public class FujiMakerNote extends FujiMakerNoteSupport
  {
    private final static long serialVersionUID = 6347805620960118907L;

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, final long offset)
       throws IOException
      {
        iis.seek(offset);
        final byte[] buffer = new byte[8];
        iis.readFully(buffer);
        final String s = new String(buffer);

        if (s.equals("FUJIFILM"))
          {
            iis.skipBytes(4);
            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
          }
      }
  }
