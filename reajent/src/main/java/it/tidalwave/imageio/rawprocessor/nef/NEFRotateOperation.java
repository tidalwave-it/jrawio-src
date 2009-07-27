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
 * $Id: NEFRotateOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.nef;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.nef.NikonCaptureEditorMetadata;
import it.tidalwave.imageio.rawprocessor.raw.RotateOperation;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: NEFRotateOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class NEFRotateOperation extends RotateOperation
  {
    private final static Logger logger = getLogger(NEFRotateOperation.class);
    
    private NikonCaptureEditorMetadata nceMetadata;
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void init (RAWImage image) throws Exception
      {
        super.init(image);
        NEFMetadata metadata = (NEFMetadata)image.getRAWMetadata();
        nceMetadata = (NikonCaptureEditorMetadata)metadata.getCaptureEditorMetadata();
      }
                
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected int getCameraOrientation (RAWImage image)
      {
        return (nceMetadata != null) ? nceMetadata.getOrientation() : super.getCameraOrientation(image);      
      }
  }
