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
 * $Id: ORFCurveOperation.java,v 1.1 2006/02/25 16:58:37 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.orf;

import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;
import it.tidalwave.imageio.orf.OlympusMakerNote;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: ORFCurveOperation.java,v 1.1 2006/02/25 16:58:37 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class ORFCurveOperation extends CurveOperation
  {
    private int[] blackLevels;
    
    private int validBits;
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void init (RAWImage image) throws Exception
      {        
        OlympusMakerNote orfMakernote = (OlympusMakerNote)image.getRAWMetadata().getMakerNote();
        blackLevels = orfMakernote.getBlackLevel();
        validBits = orfMakernote.getValidBits();
        
        String model = ((TIFFMetadataSupport)image.getRAWMetadata()).getPrimaryIFD().getModel();
        model = model.toUpperCase().trim();
        
        if ("E-1".equals(model) || "E-10".equals(model)) // FIXME: don't know why
          {
            validBits = 16;   
          }
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected int[] getBlackLevel(RAWImage image)
      {
        int blackLevel = (blackLevels[0] + blackLevels[1] + blackLevels[2] + blackLevels[3]) / 4;
        return new int[] { blackLevel, blackLevel, blackLevel };
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        double whiteLevel = (1 << validBits) - 1;
        
        return whiteLevel;
      }
  }
