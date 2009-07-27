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
 * $Id: OlympusMakerNote.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import it.tidalwave.imageio.util.Logger;
import java.io.IOException;
import it.tidalwave.imageio.tiff.ThumbnailHelper;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.tiff.TIFFTag;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: OlympusMakerNote.java 159 2008-09-13 19:15:44Z fabriziogiudici $
 *
 **********************************************************************************************************************/
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
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, final long offset) 
       throws IOException
      {
        long baseOffsetSave = iis.getBaseOffset();
        iis.seek(offset);
        final byte[] buffer = new byte[8];
        iis.readFully(buffer);
        final String s = new String(buffer, 0, 5);

        if (s.equals("OLYMP"))
          {
            final long savePosition = iis.getStreamPosition();
            final int xxx = iis.readInt();
            
            if (xxx != 0x34949) // e.g. E-500
              {
                iis.seek(savePosition);  
              }

            super.load(iis, iis.getStreamPosition()); // not loadAll(), there's no next-IFD pointer at the end
            loadCameraSettings(iis);
          }

        iis.setBaseOffset(baseOffsetSave);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public CameraSettings getOlympusCameraSettings()
      {
        return cameraSettings;  
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public Equipment getOlympusEquipment() 
      {
        return equipment;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public FocusInfo getOlympusFocusInfo() 
      {
        return focusInfo;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public ImageProcessing getOlympusImageProcessing() 
      {
        return imageProcessing;
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @CheckForNull
    public RawDevelopment getOlympusRawDevelopment() 
      {
        return rawDevelopment;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
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

    /*******************************************************************************************************************
     *
     * 
     ******************************************************************************************************************/
    private void loadMakerNoteIFD (@Nonnull final RAWImageInputStream iis,
                                   @Nonnull final Directory directory, 
                                   @Nonnull final Object key, 
                                   @Nonnull final String string) 
      throws IOException 
      {
        // Tags describing IFDs in the makernote can be of two types:
        // 1. the classic TIFF 'undefined' type, in which case the tag contains
        //    an array of bytes where the IFD is stored. Unfortunately, this
        //    IFD can reference 'undefined' or 'ascii' tags that are physically
        //    located outside of the array of bytes, so we can't just wrap
        //    an InputStream around the bytes and get the IFD out of it. But
        //    getting the 'valueOffset' of the tag, we can load the IFD as usual.
        // 2. a non standard 'type C', which contains an integer that is the
        //    offset of the IFD, relative to the current MakerNote position.
        final Object value = getObject(key);
        
        if (value instanceof byte[])
          {
            final TIFFTag tag = (TIFFTag)getTag(key);
            directory.load(iis, tag.getValueOffset());
          }
        else
          {
            final int makerNoteOffset = (int)getStart() - 12;
            directory.load(iis, makerNoteOffset + (Integer)value);
          }
        
        logger.fine("%s: %s", string, directory);
      }

    /*******************************************************************************************************************
     *
     * 
     ******************************************************************************************************************/
    private void loadCameraSettings (@Nonnull final RAWImageInputStream iis)
      throws IOException
      {
        if (isEquipmentAvailable())
          {
            loadMakerNoteIFD(iis, equipment = new Equipment(), EQUIPMENT, "Equipment");
          }
        
        if (isCameraSettingsAvailable())
          {
            loadMakerNoteIFD(iis, cameraSettings = new CameraSettings(), CAMERASETTINGS, "CameraSettings");
          }
        
        if (isRawDevelopmentAvailable())
          {
            loadMakerNoteIFD(iis, rawDevelopment = new RawDevelopment(), RAWDEVELOPMENT, "RawDevelopment");
          }
        
        if (isImageProcessingAvailable())
          {
            loadMakerNoteIFD(iis, imageProcessing = new ImageProcessing(), IMAGEPROCESSING, "ImageProcessing");
          }
        
        if (isFocusInfoAvailable())
          {
            loadMakerNoteIFD(iis, focusInfo = new FocusInfo(), FOCUSINFO, "FocusInfo");
          }
      }
  }
