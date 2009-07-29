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

import javax.annotation.Nonnull;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Dimension;
import it.tidalwave.imageio.raw.ImageDescriptor;
import it.tidalwave.imageio.profile.ProcessorOperation.AdaptivePropertiesHandler;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class MarginSetter extends AdaptivePropertiesHandler<CropOp>
  {
    public static final String PROP_MARGINS = "margins";

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected Margins margins = Margins.NO_MARGINS;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public MarginSetter setMargins (final @Nonnull Margins margins)
      {
        final Margins oldMargins = this.margins;
        this.margins = margins;
        propertyChangeSupport.firePropertyChange(PROP_MARGINS, oldMargins, margins);

        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Margins getMargins()
      {
        return margins;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public MarginSetter addPropertyChangeListener (final @Nonnull PropertyChangeListener listener)
      {
        propertyChangeSupport.addPropertyChangeListener(listener);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public MarginSetter removePropertyChangeListener (final @Nonnull PropertyChangeListener listener)
      {
        propertyChangeSupport.removePropertyChangeListener(listener);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public void adaptProperties (final @Nonnull CropOp cropOp,
                                 final @Nonnull ImageDescriptor imageDescriptor)
      {
        final Dimension dimension = imageDescriptor.getDimension();
        cropOp.setBounds(Bounds.create().withLeft(margins.getLeft()).
                                         withTop(margins.getTop()).
                                         withRight(dimension.width - margins.getRight()).
                                         withBottom(dimension.height - margins.getBottom()));
      }
  }
