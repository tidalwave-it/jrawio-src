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
 * $Id: Curve.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.profile;

import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: Curve.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class Profile
  {
    @Nonnull
    protected final String id;
    
    @Nonnull
    protected final String displayName;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public Profile (final @Nonnull String id, final @Nonnull String displayName)
      {
        this.id = id;
        this.displayName = displayName;
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
        return super.toString();
      }
  }
