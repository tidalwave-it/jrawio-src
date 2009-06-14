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
 * $Id: CRWSizeOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.crw;

import java.awt.Dimension;
import it.tidalwave.imageio.util.Logger;
import java.awt.Insets;
import it.tidalwave.imageio.crw.CRWMetadata;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: CRWSizeOperation.java 157 2008-09-13 18:43:49Z fabriziogiudici $
 *
 ******************************************************************************/
public class CRWSizeOperation extends SizeOperation
  {
    private final static Logger logger = getLogger(CRWSizeOperation.class);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        logger.fine("getCrop()");
        CRWMetadata metadata = (CRWMetadata)image.getRAWMetadata();
        int l = metadata.getSensorLeftBorder();
        int t = metadata.getSensorTopBorder();
        int r = metadata.getSensorWidth() - metadata.getSensorRightBorder() - 1;
        int b = metadata.getSensorHeight() - metadata.getSensorBottomBorder() - 1;
        Insets crop = new Insets(t, l, b, r);        
        logger.fine(">>>> returning: %s", crop);
        
        return crop;
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Dimension getSize (RAWImage image)
      {
        logger.fine("getSize()");
        CRWMetadata crwMetadata = (CRWMetadata)image.getRAWMetadata();
        Dimension dimension = new Dimension(crwMetadata.getImageWidth(), crwMetadata.getImageHeight());
        logger.fine(">>>> returning: %s", dimension);
        return dimension;
      }
  }
