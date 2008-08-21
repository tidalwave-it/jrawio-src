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
 * $Id: MRWMetadata.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: MRWMetadata.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868418676854749L;

    /*******************************************************************************
     *
     ******************************************************************************/
    public MRWMetadata (Directory primaryIFD, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getWidth()
      {
        return getPrimaryIFD().getImageWidth();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getHeight()
      {
        return getPrimaryIFD().getImageLength();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public MinoltaMakerNote getMinoltaMakerNote ()
      {
        return (MinoltaMakerNote)getMakerNote();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return ifd.isImageWidthAvailable();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }
  }
