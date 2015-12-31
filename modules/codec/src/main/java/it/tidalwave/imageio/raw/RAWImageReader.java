/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.raw;

import javax.annotation.Nonnull;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

/***********************************************************************************************************************
 *
 * This class provides support for all RAW image readers.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class RAWImageReader extends ImageReader
  {
    @Nonnull
    private RAWImageReadParam defaultReadParam = RAWImageReadParam.DEFAULT;

    /*******************************************************************************************************************
     * 
     * @param originatingProvider
     * 
     *******************************************************************************/
    protected RAWImageReader (@Nonnull final ImageReaderSpi originatingProvider)
      {
        super(originatingProvider);
      }

    /*******************************************************************************************************************
     *
     * Sets the default read parameter that will be used with {@link #read(int)}, as well as
     * {@link #getImageMetadata(int)). It is not possible to pass a <code>null</code> parameter, use
     * {@link RAWImageReadParam#DEFAULT} instead.
     *
     * @param  defaultReadParam   the default read parameter
     *
     ******************************************************************************************************************/
    public final void setDefaultReadParam (final @Nonnull RAWImageReadParam defaultReadParam)
      {
        if (defaultReadParam == null)
          {
            throw new IllegalArgumentException("defaultReadParam is mandatory");
          }

        this.defaultReadParam = defaultReadParam;
      }

    /*******************************************************************************************************************
     *
     * Returns the default read parameter.
     *
     * @return  the default read parameter
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public final RAWImageReadParam getDefaultReadParam()
      {
        return defaultReadParam;
      }
  }
