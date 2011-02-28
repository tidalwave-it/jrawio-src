/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.dcr;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class DCRMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 3920868418676854749L;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public DCRMetadata (Directory primaryIFD, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public KodakMakerNote getPentaxMakerNote ()
      {
        return (KodakMakerNote)getMakerNote();
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.IMAGE); 
      }
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.REDUCED_RESOLUTION)
        && ifd.getCompression().intValue() != 65000; 
      }
  }
