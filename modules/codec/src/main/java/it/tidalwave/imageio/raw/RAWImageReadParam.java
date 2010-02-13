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
package it.tidalwave.imageio.raw;

import javax.annotation.Nonnull;
import java.io.Serializable;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import it.tidalwave.imageio.util.Lookup;
import it.tidalwave.imageio.util.Lookup.NotFoundException;
import it.tidalwave.imageio.util.DefaultingLookup;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class RAWImageReadParam extends ImageReadParam implements Serializable
  {
    public final static RAWImageReadParam DEFAULT = new RAWImageReadParam();
    private final static long serialVersionUID = 34234346546764574L;
    
    private final Lookup lookup;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public RAWImageReadParam (final @Nonnull Object ... params)
      {
        lookup = new DefaultingLookup(Lookup.fixed(params));

        try
          {
            setDestinationType(lookup.lookup(ImageTypeSpecifier.class));
          }
        catch (Lookup.NotFoundException e)
          {
          }

//        try TODO: setSourceRenderSize is not implemented
//          {
//            setSourceRenderSize(lookup.lookup(Dimension.class));
//          }
//        catch (Lookup.NotFoundException e)
//          {
//          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public final Lookup getLookup()
      {
        return lookup;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public <T> T lookup (final @Nonnull Class<T> type)
      {
        try
          {
            return lookup.lookup(type);
          }
        catch (NotFoundException e) // we're using DefaultingLookup. so there's always a default
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    @Override
    public String toString()
      {
        return String.format("RAWImageReadParam[%s]", lookup.toContentString());
      }
  }


