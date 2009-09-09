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
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2SensorInfo
  {
    private final @Nonnull int[] buffer;

    protected CR2SensorInfo (final @Nonnull int[] buffer)
      {
        this.buffer = buffer;
      }

    @Nonnegative
    public int getWidth()
      {
        return buffer[1];
      }

    @Nonnegative
    public int getHeight()
      {
        return buffer[2];
      }

    @Nonnegative
    public int getCropLeft()
      {
        return buffer[5];
      }

    @Nonnegative
    public int getCropTop()
      {
        return buffer[6];
      }

    @Nonnegative
    public int getCropRight()
      {
        return buffer[7];
      }

    @Nonnegative
    public int getCropBottom()
      {
        return buffer[8];
      }
  }
