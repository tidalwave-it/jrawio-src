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
import java.util.HashSet;
import java.util.Set;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DefaultLookup extends Lookup
  {
    private final static String CLASS = DefaultLookup.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private final Set<Object> contents;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    protected DefaultLookup (final @Nonnull Object ... contents)
      {
        this.contents = new HashSet<Object>();

        for (final Object content : contents)
          {
            if (content != null)
              {
                this.contents.add(content);
              }
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    @Override
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
    @Override
    public <T> T lookup (final @Nonnull Class<T> type)
      throws NotFoundException
      {
        for (final Object param : contents)
          {
            if (type.isAssignableFrom(param.getClass()))
              {
                logger.finest("lookup(%s) returning %s", type, param);
                return (T)param;
              }
          }

        logger.finest("lookup(%s) throwing NotFoundException", type);
        throw new NotFoundException(type);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        return String.format("DefaultLookup[%s]", contents);
      }
  }
