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

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class DemosaicOp extends ProcessorOperation<DemosaicOp>
  {
    public static final String PROP_ALGORITHM = "algorithm";

    protected String algorithm;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DemosaicOp (final @Nonnull Profile profile)
      {
        super(profile);
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getAlgorithm()
      {
        return algorithm;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public DemosaicOp setAlgorithm (final @Nonnull String algorithm)
      {
        final String oldAlgorithm = this.algorithm;
        this.algorithm = algorithm;
        propertyChangeSupport.firePropertyChange(PROP_ALGORITHM, oldAlgorithm, algorithm);

        return this;
      }
  }
