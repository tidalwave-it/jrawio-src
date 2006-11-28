/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 * 
 * $Id: CanonCRWMakerNote.java,v 1.1 2006/02/13 22:39:39 fabriziogiudici Exp $
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
 * @version CVS $Id: CanonCRWMakerNote.java,v 1.1 2006/02/13 22:39:39 fabriziogiudici Exp $
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
