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
package it.tidalwave.imageio.profile.impl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import it.tidalwave.imageio.profile.Profile;
import it.tidalwave.imageio.profile.Profile.Changeability;
import it.tidalwave.imageio.profile.ProfileManager;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ProfileManagerImpl extends ProfileManager
  {
    private final Map<String, Profile> profileMap = new HashMap<String, Profile>();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public ProfileManagerImpl()
      {
        final Profile dcrawProfile = new DcrawProfile(); // FIXME: discover with META-INF/services
        profileMap.put(dcrawProfile.getId(), dcrawProfile);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile createProfile (final @Nonnull String id,
                                  final @Nonnull String displayName)
      {
        // TODO
        return new ProfileImpl(id, displayName, Changeability.WRITABLE);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Profile createProfileFrom (final @Nonnull String id,
                                      final @Nonnull String displayName,
                                      final @Nonnull Profile archetype)
      {
        // TODO
        return new ProfileImpl(id, displayName, Changeability.WRITABLE);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public Set<String> getProfileNames()
      {
        return new CopyOnWriteArraySet<String>(profileMap.keySet());
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public synchronized Profile findProfileById (@Nonnull final String id)
      throws NotFoundException
      {
        final Profile profile = profileMap.get(id);

        if (profile == null)
          {
            throw new NotFoundException("No such profile id: " + id);
          }

        return profile;
      }
  }
