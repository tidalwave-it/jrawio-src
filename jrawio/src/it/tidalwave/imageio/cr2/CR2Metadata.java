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
 * $Id: CR2Metadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.cr2;

import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.cr2.CanonCR2MakerNote;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;

/*******************************************************************************
 *
 * @author  fritz
 * @version $Id: CR2Metadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class CR2Metadata extends TIFFMetadataSupport
  {
    /*******************************************************************************
     *
     ******************************************************************************/
    public CR2Metadata (Directory primaryIFD, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public CanonCR2MakerNote getCanonMakerNote ()
      {
        return (CanonCR2MakerNote)getMakerNote();
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getWidth()
      {
        return getExifIFD().getPixelXDimension();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getHeight()
      {
        return getExifIFD().getPixelYDimension();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return ifd.isCanon50648Available();
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return !ifd.isCanon50648Available();
      }
  }
