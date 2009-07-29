/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2009 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.imageio.ImageReadParam;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RawImageReadParam extends ImageReadParam implements Serializable
  {
    public static class NotFoundException extends Exception
      {
        public NotFoundException (final @Nonnull Class<?> type)
          {
            super("Parameter type not found: " + type);
          }
      }

    private final Set<Serializable> params;

    public RawImageReadParam (final @Nonnull Serializable ... params)
      {
        this.params = new HashSet<Serializable>(Arrays.asList(params));
      }

    @Nonnull
    public <T> T get (final @Nonnull Class<T> type, final @Nonnull T defaultValue)
      {
        //TODO: enforce defaultValue not null
        try
          {
            return get(type);
          }
        catch (NotFoundException e)
          {
            return defaultValue;
          }
      }

    @Nonnull
    public <T> T get (final @Nonnull Class<T> type)
      throws NotFoundException
      {
        for (final Serializable param : params)
          {
            if (type.isAssignableFrom(param.getClass()))
              {
                return (T)param;
              }
          }

        throw new NotFoundException(type);
      }
  }
