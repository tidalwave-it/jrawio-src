/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.io;

import javax.annotation.Nonnegative;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface RAWImageInputStream extends ImageInputStream
  {
    /*******************************************************************************************************************
     *
     * Sets a new base offset.
     *
     * @param baseOffset      the new base offset
     * 
     ******************************************************************************************************************/
    public void setBaseOffset (@Nonnegative long baseOffset);

    /*******************************************************************************************************************
     *
     * Returns the current base offset.
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnegative 
    public long getBaseOffset();

    /*******************************************************************************************************************
     *
     * Select the most suitable {@link BitReader} given the bit count and the buffer size. You will pass a non zero
     * bitCount for uncompressed rasters (where you always read set of bits of the same size); zero in other cases.
     *
     * @param  bitCount     the number of bits that will be read
     * @param  bufferSize   the buffer size
     * 
     ******************************************************************************************************************/
    public void selectBitReader (@Nonnegative final int bitCount,
                                 @Nonnegative int bufferSize);

    /*******************************************************************************************************************
     *
     * Some formats have a zero byte after each byte valued 0xff. This usually need to be skipped and this stream is
     * able to do that automatically.
     * 
     * @param  skipZeroAfterFF  true if you want to skip a zero byte after each 0xff
     * 
     ******************************************************************************************************************/
    public void setSkipZeroAfterFF (final boolean skipZeroAfterFF);

    /*******************************************************************************************************************
     * 
     * Read another string of bits. If the most significant bits of the read string
     * is 1, the 2-complement value is returned. This operation is frequently needed
     * by some RAW formats.
     *
     * @param  bitsToGet    how many bits to read
     * @return              the bits as an integer
     * @throws IOException  if any I/O error occurs
     *
     ******************************************************************************************************************/
    public int readComplementedBits (@Nonnegative final int bitsToGet)
      throws IOException;

    /*******************************************************************************************************************
     * 
     * @param   bitCount
     * @throws  IOException
     * 
     ******************************************************************************************************************/
    public void skipBits (@Nonnegative final int bitCount)
      throws IOException;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public void setDontCloseDelegate();
  }
