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
public final class Bounds implements Serializable
  {
    public final static Bounds ALL = new Bounds(0, 0, Double.MAX_VALUE - 1, Double.MAX_VALUE - 1); // -1 to keep width and height in range

    private final double left;
    private final double top;
    private final double right;
    private final double bottom;

    private Bounds (final double left, final double top, final double right, final double bottom)
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
    public Bounds withLeft (final double left)
      {
        return new Bounds(left, this.top, this.right, this.bottom);
      }

    @Nonnull
    public Bounds withRight (final double right)
      {
        return new Bounds(this.left, this.top, right, this.bottom);
      }

    @Nonnull
    public Bounds withTop (final double top)
      {
        return new Bounds(this.left, top, this.right, this.bottom);
      }

    @Nonnull
    public Bounds withBottom (final double bottom)
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

    public double getBottom()
      {
        return bottom;
      }

    public double getLeft()
      {
        return left;
      }

    public double getRight()
      {
        return right;
      }

    public double getTop()
      {
        return top;
      }

    public double getWidth()
      {
        return right - left + 1;
      }

    public double getHeight()
      {
        return top - bottom + 1;
      }

    @Override
    public boolean equals (final Object object)
      {
        if ((object == null) || (getClass() != object.getClass()))
          {
            return false;
          }

        final Bounds other = (Bounds)object;

        return (this.left == other.left) &&
               (this.top == other.top) &&
               (this.right == other.right) &&
               (this.bottom == other.bottom);
      }

    @Override
    public int hashCode()
      {
        int hash = 7;
        hash = 53 * hash + (int)(Double.doubleToLongBits(left)   ^ (Double.doubleToLongBits(left) >>> 32));
        hash = 53 * hash + (int)(Double.doubleToLongBits(top)    ^ (Double.doubleToLongBits(top) >>> 32));
        hash = 53 * hash + (int)(Double.doubleToLongBits(right)  ^ (Double.doubleToLongBits(right) >>> 32));
        hash = 53 * hash + (int)(Double.doubleToLongBits(bottom) ^ (Double.doubleToLongBits(bottom) >>> 32));
        return hash;
      }

    @Override
    public String toString()
      {
        return this.equals(ALL) ?
               "Bounds[no bounds]" :
               String.format("Bounds[l: %f, t: %f, r: %f, b: %f, w: %f, h: %f]",
                             left, top, right, bottom, getWidth(), getHeight());
      }
  }

