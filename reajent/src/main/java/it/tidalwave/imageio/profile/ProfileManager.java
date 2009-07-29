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

import it.tidalwave.imageio.profile.impl.ProfileManagerImpl;
import java.util.Set;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public abstract class ProfileManager
  {
    /*******************************************************************************************************************
     *
     * @author  Fabrizio Giudici
     * @version $Id$
     *
     ******************************************************************************************************************/
    public static class NotFoundException extends Exception
      {
        public NotFoundException (@Nonnull final String msg)
          {
            super(msg);
          }
      }

    private static ProfileManager instance;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public synchronized static ProfileManager getInstance()
      {
        if (instance == null)
          {
            // TODO: use META-INF to get the thing.
            instance = new ProfileManagerImpl();
          }

        return instance;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public abstract Profile createProfile (final @Nonnull String id,
                                           final @Nonnull String displayName);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public abstract Profile createProfileFrom (final @Nonnull String id,
                                               final @Nonnull String displayName,
                                               final @Nonnull Profile archetype);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public abstract Set<String> getProfileNames();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public abstract  Profile findProfileById (@Nonnull final String id)
      throws NotFoundException;
  }
