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
 * $Id: PEFSizeOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.raf;

import it.tidalwave.imageio.pef.PentaxMakerNote;
import java.awt.Dimension;
import java.awt.Insets;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFSizeOperation.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFSizeOperation extends SizeOperation
  {
    private int left;
    
    private int top;
    
    private int width;
    
    private int height;
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    @Override
    public void init (RAWImage image)
      throws Exception
      {
        // TODO
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected Insets getCrop (RAWImage image)
      {
        return new Insets(top, 
                          left, 
                          image.getImage().getHeight() - top - height,
                          image.getImage().getWidth() - left - width);
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    @Override
    protected Dimension getSize (RAWImage image)
      {
        return new Dimension(width, height);
      }
  }
