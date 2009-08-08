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
package it.tidalwave.imageio.raw;

import java.awt.Dimension;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.imageio.metadata.IIOMetadata;

/***********************************************************************************************************************
 *
 * Represents a post-processing operation in the user's perspective (while Operation is internal only).
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class ImageDescriptor implements Serializable
  {
    @Nonnull
    private final IIOMetadata metadata;

    @Nonnull
    private final Dimension dimension;

    public ImageDescriptor (final @Nonnull IIOMetadata metadata,
                            final @Nonnull Dimension dimension)
      {
        this.metadata = metadata;
        this.dimension = dimension;
      }

    @Nonnull
    public IIOMetadata getMetadata()
      {
        return metadata;
      }

    @Nonnull
    public Dimension getDimension()
      {
        return dimension;
      }
  }
