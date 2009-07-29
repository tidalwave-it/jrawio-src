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
import java.io.Serializable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class SizeOp extends ProcessorOperation<SizeOp>
  {
    /*******************************************************************************************************************
     *
     * @author  Fabrizio Giudici
     * @version $Id$
     *
     ******************************************************************************************************************/
    public static class Bounds implements Serializable
      {
        public final static Bounds ALL = new Bounds(0, 0, Double.MAX_VALUE, Double.MAX_VALUE);

        private final double left;
        private final double top;
        private final double right;
        private final double bottom;

        public Bounds (final double left, final double top, final double right, final double bottom)
          {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
          }

        @Nonnull
        public static Bounds create()
          {
            return ALL;
          }

        @Nonnull
        public Bounds left (final double left)
          {
            return new Bounds(left, this.top, this.right, this.bottom);
          }

        @Nonnull
        public Bounds right (final double right)
          {
            return new Bounds(this.left, this.top, right, this.bottom);
          }

        @Nonnull
        public Bounds top (final double top)
          {
            return new Bounds(this.left, top, this.right, this.bottom);
          }

        @Nonnull
        public Bounds bottom (final double bottom)
          {
            return new Bounds(this.left, this.top, this.right, bottom);
          }

        @Nonnull
        public Bounds width (final double width)
          {
            return new Bounds(this.left, this.top, this.left + width - 1, bottom);
          }

        @Nonnull
        public Bounds height (final double height)
          {
            return new Bounds(this.left, this.top, this.right, bottom + height - 1);
          }
      }
    
    public static final String PROP_BOUNDS = "bounds";

    @Nonnull
    protected Bounds bounds = Bounds.ALL;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public SizeOp (final @Nonnull Profile profile)
      {
        super(profile);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Bounds getBounds()
      {
        return bounds;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public SizeOp setBounds (final @Nonnull Bounds bounds)
      {
        final Bounds oldBounds = this.bounds;
        this.bounds = bounds;
        propertyChangeSupport.firePropertyChange(PROP_BOUNDS, oldBounds, bounds);

        return this;
      }
  }
