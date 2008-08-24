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
 * $Id: OlympusMakerNote.java 95 2008-08-24 14:48:47Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.io.IOException;
import it.tidalwave.imageio.tiff.ThumbnailHelper;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: OlympusMakerNote.java 95 2008-08-24 14:48:47Z fabriziogiudici $
 *
 ******************************************************************************/
public class OlympusMakerNote extends OlympusMakerNoteSupport
  {
    private final static String CLASS = ORFMetadata.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 6357805620960118907L;

    private Equipment equipment;
    
    private CameraSettings cameraSettings;
    
    private RawDevelopment rawDevelopment;
    
    private ImageProcessing imageProcessing;
    
    private FocusInfo focusInfo;
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, final long offset) 
       throws IOException
      {
        long baseOffsetSave = iis.getBaseOffset();
        iis.seek(offset);
        final byte[] buffer = new byte[8];
        iis.read(buffer);
        final String s = new String(buffer, 0, 5);

        if (s.equals("OLYMP"))
          {
            iis.skipBytes(4);
            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
            loadCameraSettings(iis);
          }

        iis.setBaseOffset(baseOffsetSave);
      }
    
    /***************************************************************************
     *
     *
     **************************************************************************/
    @CheckForNull
    public CameraSettings getOlympusCameraSettings()
      {
        return cameraSettings;  
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    @CheckForNull
    public Equipment getOlympusEquipment() 
      {
        return equipment;
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    @CheckForNull
    public FocusInfo getOlympusFocusInfo() 
      {
        return focusInfo;
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    @CheckForNull
    public ImageProcessing getOlympusImageProcessing() 
      {
        return imageProcessing;
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    @CheckForNull
    public RawDevelopment getOlympusRawDevelopment() 
      {
        return rawDevelopment;
      }
    
    /***************************************************************************
     *
     *
     **************************************************************************/
    @Nonnull
    protected ThumbnailHelper loadThumbnailHelper (@Nonnull final RAWImageInputStream iis)
      throws IOException, NoSuchElementException
      {
        if ((cameraSettings != null) &&
             cameraSettings.isThumbnailOffsetAvailable() && 
             cameraSettings.isThumbnailSizeAvailable())
          {
            final int makerNoteOffset = (int)getStart() - 12;
            final int thumbnailOffset = cameraSettings.getThumbnailOffset() + makerNoteOffset;
            final int thumbnailSize = cameraSettings.getThumbnailSize();
            return new ThumbnailHelper(iis, thumbnailOffset, thumbnailSize);
          }
        
        throw new NoSuchElementException();
      }

    /***************************************************************************
     *
     *
     **************************************************************************/
    private void loadCameraSettings (@Nonnull final RAWImageInputStream iis)
      throws IOException
      {
        final int makerNoteOffset = (int)getStart() - 12;
        
        if (isEquipmentAvailable())
          {
            equipment = new Equipment();
            equipment.load(iis, makerNoteOffset + getEquipment());
            logger.fine("Equipment: " + equipment);
          }
        
        if (isCameraSettingsAvailable())
          {
            cameraSettings = new CameraSettings();
            cameraSettings.load(iis, makerNoteOffset + getCameraSettings());
            logger.fine("CameraSettings: " + cameraSettings);
          }
        
        if (isRawDevelopmentAvailable())
          {
            rawDevelopment = new RawDevelopment();
            rawDevelopment.load(iis, makerNoteOffset + getRawDevelopment());
            logger.fine("RawDevelopment: " + rawDevelopment);
          }
        
        if (isImageProcessingAvailable())
          {
            imageProcessing = new ImageProcessing();
            imageProcessing.load(iis, makerNoteOffset + getImageProcessing());
            logger.fine("ImageProcessing: " + imageProcessing);
          }
        
        if (isFocusInfoAvailable())
          {
            focusInfo = new FocusInfo();
            focusInfo.load(iis, makerNoteOffset + getFocusInfo());
            logger.fine("FocusInfo: " + focusInfo);
          }
      }
  }
