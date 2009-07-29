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
import java.io.Serializable;
import javax.imageio.metadata.IIOMetadata;
import it.tidalwave.imageio.util.Lookup;

/***********************************************************************************************************************
 *
 * Represents a post-processing operation in the user's perspective (while Operation is internal only).
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class ProcessorOperation<T extends ProcessorOperation> implements Serializable
  {
    /*******************************************************************************************************************
     *
     * @author  Fabrizio Giudici
     * @version $Id$
     *
     ******************************************************************************************************************/
    public static final class ImageDescriptor
      {
        @Nonnull
        private final IIOMetadata metadata;

        public ImageDescriptor (final @Nonnull IIOMetadata metadata)
          {
            this.metadata = metadata;
          }

        @Nonnull
        public IIOMetadata getMetadata()
          {
            return metadata;
          }
      }
    
    /*******************************************************************************************************************
     *
     * This class implements a callback that is invoked when the image has been loaded, but not processed yet. This
     * allows to change parameters in function of image properties - for instance, a size operation can compute its
     * parameters from the image size; or a denoise operation can choose different algorithms in function of the image
     * size or properties.
     *
     * @author  Fabrizio Giudici
     * @version $Id$
     *
     ******************************************************************************************************************/
    public static abstract class AdaptivePropertiesHandler<O extends ProcessorOperation>
      {
        /***************************************************************************************************************
         *
         *
         **************************************************************************************************************/
        public abstract void adaptProperties (@Nonnull final O operation, @Nonnull final ImageDescriptor imageDescriptor);
      }
    
    public static final String PROP_ADAPTIVEPROPERTIESHANDLER = "adaptivePropertiesHandler";

    protected final Profile profile;

    protected final Lookup lookup = Lookup.fixed();

    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Nonnull
    protected AdaptivePropertiesHandler<T> adaptivePropertiesHandler = new AdaptivePropertiesHandler<T>()
      {
        @Override
        public void adaptProperties (final @Nonnull T operation,
                                     final @Nonnull ImageDescriptor imageDescriptor)
          {
          }
      };

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected ProcessorOperation (final @Nonnull Profile profile)
      {
        this.profile = profile;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public T addPropertyChangeListener (final @Nonnull PropertyChangeListener listener)
      {
        propertyChangeSupport.addPropertyChangeListener(listener);
        return (T)this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public T removePropertyChangeListener (final @Nonnull PropertyChangeListener listener)
      {
        propertyChangeSupport.removePropertyChangeListener(listener);
        return (T)this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Lookup getLookup()
      {
        return lookup;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public AdaptivePropertiesHandler<T> getAdaptivePropertiesHandler()
      {
        return adaptivePropertiesHandler;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public T setAdaptivePropertiesHandler (final @Nonnull AdaptivePropertiesHandler<T> adaptivePropertiesHandler)
      {
        final AdaptivePropertiesHandler oldAdaptivePropertiesHandler = this.adaptivePropertiesHandler;
        this.adaptivePropertiesHandler = adaptivePropertiesHandler;
        propertyChangeSupport.firePropertyChange(PROP_ADAPTIVEPROPERTIESHANDLER, oldAdaptivePropertiesHandler, adaptivePropertiesHandler);

        return (T)this;
      }

//    @Nonnull
//    public Operation createOperation() TODO put a OperationFactory into Lookup
//      {
//        return null;
//      }
  }
