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
 * $Id: CanonCRWMakerNote.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.crw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import it.tidalwave.imageio.crw.CIFFTag;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: CanonCRWMakerNote.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class CanonCRWMakerNote extends CanonCRWMakerNoteSupport
  {
    private final static String CLASS = "it.tidalwave.imageio.crw.CanonCRWMakerNote";

    private final static Logger logger = Logger.getLogger(CLASS);

    private static Properties lensNameByID = new Properties();

    static
      {
        try
          {
            InputStream is = CanonCRWMakerNote.class.getResourceAsStream("CanonLens.properties");
            lensNameByID.load(is);
            is.close();
          }
        catch (IOException e)
          {
            e.printStackTrace(); // TODO
          }
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void loadAll (RAWImageInputStream iis, long directoryOffset) throws IOException 
      {
        logger.fine("loadAll(iis=" + iis + ", directoryOffset=" + directoryOffset + ")");
        loadAll(iis, directoryOffset, 0);
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void loadAll (RAWImageInputStream iis, long directoryOffset, int size) throws IOException 
      {
        logger.fine("loadAll(iis=" + iis + ", directoryOffset=" + directoryOffset + ", size=" + size + ")");
        
        if (size <= 0) 
          {
            iis.seek(iis.length() - 4);
          }
        
        else 
          {
            iis.seek(directoryOffset + size - 4);
          }
        
        int dirStart = iis.readInt();
        logger.finer(">>>>>>>> dirStart: " + dirStart);
        iis.seek(directoryOffset + dirStart);
        int tagCount = iis.readShort();
        logger.finer(">>>>>>>> tagCount: " + tagCount);
        List subDirectoryList = new ArrayList();
        
        for (int i = 0; i < tagCount; i++) 
          {
            int tagCode = iis.readUnsignedShort();
            CIFFTag tag = new CIFFTag(getRegistry(), tagCode, (int)directoryOffset);
            tag.read(iis);
            addTag(tag);
            
            if (tag.isSubDirectory()) 
              {
                subDirectoryList.add(tag);
              }
          }
        
        logger.finest(">>>> " + tagCount + " fields read: " + this);
        CanonCRWMakerNote currentMakerNote = this;
        
        for (Iterator i = subDirectoryList.iterator(); i.hasNext();) 
          {
            CIFFTag tag = (CIFFTag)i.next();
            int offset = tag.getOffset();
            int size2 = tag.getSize();
            
            CanonCRWMakerNote subDirectory = new CanonCRWMakerNote(); 
            subDirectory.loadAll(iis, directoryOffset + offset, size2);
            currentMakerNote.addDirectory(subDirectory);
            
            if (tag.getCode() == 12299) // CanonMakerNote.EXIF_INFORMATION) FIXME
              {
                logger.finer("CanonMakerNote: " + subDirectory);
                currentMakerNote = subDirectory; // FIXME
              }
            }
        
        logger.fine(">>>> loadAll() completed ok");
      }
  
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public int getLensType ()
      {
        return getCanonCameraSettings()[22];
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public String getLensName ()
      {
        return lensNameByID.getProperty("" + getLensType());
      }
  }
