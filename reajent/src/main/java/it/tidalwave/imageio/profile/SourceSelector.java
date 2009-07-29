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
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.profile;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.Serializable;

/***********************************************************************************************************************
 *
 * This class allows to implement a dynamic strategy to choose where to load the bits from. In fact, sometimes one might
 * wish that the read() method of ImageReader returns a reasonably large thumbnail, rather than the raw data.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class SourceSelector implements Serializable
  {
    public static final class Source implements Serializable
      {
        protected Source()
          {
          }

        public static Source RAW_DATA()
          {
            return null; // FIXME
          }

        public static Source THUMBNAIL (final @Nonnegative int index)
          {
            return null;
          }
      }

    public abstract Source selectSource (final @Nonnull ImageDescriptor imageDescriptor);
  }
