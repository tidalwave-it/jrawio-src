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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class Profile
  {
    @Nonnull
    protected final String id;
    
    @Nonnull
    protected final String displayName;

    protected final boolean readOnly;

    private final List<ProcessorOperation> operations = new ArrayList<ProcessorOperation>();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected Profile (final @Nonnull String id, final @Nonnull String displayName, final boolean readOnly)
      {
        this.id = id;
        this.displayName = displayName;
        this.readOnly = readOnly;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getDisplayName()
      {
        return displayName;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getId()
      {
        return id;
      }

    /*******************************************************************************************************************
     *
     * Preset profiles are read only and can't be changed. Use ProfileManager.createFrom() instead.
     *
     ******************************************************************************************************************/
    public boolean isReadOnly()
      {
        return readOnly;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public List<ProcessorOperation> getOperations()
      {
        return new CopyOnWriteArrayList<ProcessorOperation>(operations);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile addOperation (final @Nonnull ProcessorOperation operation)
      {
        ensureNotReadOnly();
        operations.add(operation);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile insertOperation (final @Nonnull ProcessorOperation operation,
                                    final @Nonnegative int index)
      {
        ensureNotReadOnly();
        operations.add(index, operation);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile removeOperation (final @Nonnegative int index)
      {
        operations.remove(index);
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile clearOperations()
      {
        ensureNotReadOnly();
        operations.clear();
        return this;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public boolean equals (final Object object)
      {
        if (object == null)
          {
            return false;
          }

        if (getClass() != object.getClass())
          {
            return false;
          }

        final Profile other = (Profile)object;

        return this.id.equals(other.id) && displayName.equals(other.displayName);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public int hashCode()
      {
        int hash = 7;
        hash = 59 * hash + this.id.hashCode();
        hash = 59 * hash + this.displayName.hashCode();
        return hash;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        return super.toString(); // TODO
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void ensureNotReadOnly()
      {
        if (readOnly)
          {
            throw new IllegalStateException("Cannot modify a read-only Profile");
          }
      }
  }
