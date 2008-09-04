/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: TIFFMetadataSupport.java 84 2008-08-24 09:20:11Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.arw;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.IFDSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: TIFFMetadataSupport.java 84 2008-08-24 09:20:11Z fabriziogiudici $
 *
 ******************************************************************************/
public class ARWMakerNote extends IFDSupport
  {
    private static final long serialVersionUID = 4019349837473239457L;
    
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, final long offset)
      throws IOException
      {
       // TODO
      }
  }
