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
 * $Id: FileImageInputStream2.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;

/*******************************************************************************
 *
 * The purpose of this class is to provide a specialized FileImageInputStream
 * which is able to tell which is the input file that we are using. This is
 * needed by SPIs that handle formats in which there are multiple files (e.g.
 * Canon CRW which is composed of a .CRW file and a .THM file).
 * 
 * @author  fritz
 * @version CVS $Id: FileImageInputStream2.java,v 1.2 2006/02/08 19:54:43 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class FileImageInputStream2 extends FileImageInputStream
  {
    private File file;

    /*******************************************************************************
     * 
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     * 
     *******************************************************************************/
    public FileImageInputStream2 (File file) throws FileNotFoundException, IOException
      {
        super(file);
        this.file = file;
      }

    /*******************************************************************************
     * 
     * Returns the associated file
     * 
     * @return  the associated file
     * 
     *******************************************************************************/
    public File getFile ()
      {
        return file;
      }
  }
