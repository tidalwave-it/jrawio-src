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
package it.tidalwave.imageio.util;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class Lookup implements Serializable
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public static class NotFoundException extends Exception
      {
        public NotFoundException (final @Nonnull Class<?> type)
          {
            super("Parameter type not found: " + type);
          }
      }

    private final Set<Serializable> contents;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public Lookup (final @Nonnull Serializable ... contents)
      {
        this.contents = new HashSet<Serializable>(Arrays.asList(contents));
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public <T> T lookup (final @Nonnull Class<T> type, final @Nonnull T defaultValue)
      {
        //TODO: enforce defaultValue not null
        try
          {
            return lookup(type);
          }
        catch (NotFoundException e)
          {
            return defaultValue;
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public <T> T lookup (final @Nonnull Class<T> type)
      throws NotFoundException
      {
        for (final Serializable param : contents)
          {
            if (type.isAssignableFrom(param.getClass()))
              {
                return (T)param;
              }
          }

        throw new NotFoundException(type);
      }
  }
